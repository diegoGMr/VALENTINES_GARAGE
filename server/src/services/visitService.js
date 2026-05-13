import { db } from "../db.js";
import crypto from "crypto";

export const visitService = {
  async createVisit(visitData) {
    const { data, error } = await db.from("visit").insert([visitData]).select();
    if (error) throw error;
    return data[0];
  },

  async getAllIssues() {
    // 1. Get visit_ids from completed_trucks to filter them out
    const { data: completed, error: cError } = await db.from("completed_trucks").select("visit_id");
    if (cError) throw cError;
    const completedIds = completed?.map(c => c.visit_id) || [];

    // 2. Query issues
    let query = db
      .from("issues")
      .select("*, visit(visit_id, trucks!fk_truck(truck_id, license_plate))")
      .order("issue_id", { ascending: false });

    if (completedIds.length > 0) {
      query = query.not("visit_id", "in", `(${completedIds.join(",")})`);
    }

    const { data, error } = await query;
    if (error) throw error;
    return data;
  },

  async createIssue(issueData) {
    const preparedData = {
      visit_id: issueData.visit_id,
      issue_description: issueData.issue_description,
      mechanic_id: issueData.mechanic_id,
      "issue_resolved": issueData.issue_resolved ?? false,
      ...(issueData.cost != null && { cost: issueData.cost })
    };
    const { data, error } = await db.from("issues").insert([preparedData]).select();
    if (error) throw error;
    return data[0];
  },

  async resolveIssue(issueId, resolutionNotes) {
    const updateData = { issue_resolved: true };
    if (resolutionNotes) updateData.issue_resolution_notes = resolutionNotes;
    const { data, error } = await db
      .from("issues")
      .update(updateData)
      .eq("issue_id", issueId)
      .select();
    if (error) throw error;
    return data[0];
  },

  async getBookings(date) {
    const { data, error } = await db
      .from("bookings")
      .select("*, clients!bookings_client_id_fkey(full_name), trucks(*, speciality_trucks(name)), visit(booking_id)")
      .eq("booking_date", date);
    if (error) throw error;
    return data;
  },

  async createBooking(bookingData) {
    // 1. Prevent double bookings within 10 minutes
    const { data: existing, error: eError } = await db
      .from("bookings")
      .select("booking_time")
      .eq("booking_date", bookingData.booking_date);

    if (eError) throw eError;

    const timeToMinutes = (t) => {
      if (!t) return -100;
      const [h, m] = t.split(":").map(Number);
      return h * 60 + m;
    };

    const newMinutes = timeToMinutes(bookingData.booking_time);
    const isTooClose = existing.some(b => {
      const existingMinutes = timeToMinutes(b.booking_time);
      return Math.abs(newMinutes - existingMinutes) < 10;
    });

    if (isTooClose) {
      const err = new Error("Another booking exists within 10 minutes of this time.");
      err.status = 400;
      throw err;
    }

    // 2. Update truck kilometers if provided
    if (bookingData.kilometers != null) {
      const { error: kError } = await db
        .from("trucks")
        .update({ kilometers: bookingData.kilometers })
        .eq("truck_id", bookingData.truck_id);
      if (kError) throw kError;
    }

    // 3. Insert the booking
    const preparedData = {
      client_id: bookingData.client_id,
      truck_id: bookingData.truck_id,
      booking_date: bookingData.booking_date,
      booking_time: bookingData.booking_time,
      client_notes: bookingData.client_notes
    };
    const { data, error } = await db.from("bookings").insert([preparedData]).select();
    if (error) throw error;
    return data[0];
  },

  async assignMechanicToBooking(bookingId, mechanicId) {
    // 1. Check if a visit already exists for this booking
    const { data: existingVisit, error: eError } = await db
      .from("visit")
      .select("visit_id")
      .eq("booking_id", bookingId)
      .maybeSingle();
    if (eError) throw eError;

    let visitId;
    if (existingVisit) {
      visitId = existingVisit.visit_id;
    } else {
      // 2. Create the visit for this booking
      const { data: booking, error: bError } = await db.from("bookings").select("*").eq("booking_id", bookingId).single();
      if (bError) throw bError;

      const visit = await this.createVisit({
        client_id: booking.client_id,
        truck_id: booking.truck_id,
        mechanic_id: mechanicId,
        booking_id: bookingId,
        client_notes: booking.client_notes || `From booking #${bookingId}`
      });
      visitId = visit.visit_id;
    }

    // 3. Add mechanic to visit_mechanics (upsert ignores duplicates)
    const { error: vmError } = await db
      .from("visit_mechanics")
      .upsert([{ visit_id: visitId, mechanic_id: mechanicId }]);
    if (vmError) throw vmError;

    return { visit_id: visitId, mechanic_id: mechanicId };
  },

  async completeVisit(visitId) {
    // 1. Get the visit to get the truck_id
    const { data: visit, error: vError } = await db.from("visit").select("truck_id").eq("visit_id", visitId).single();
    if (vError) throw vError;

    // 2. Check for unresolved issues
    const { data: unresolved, error: iError } = await db
      .from("issues")
      .select("issue_id")
      .eq("visit_id", visitId)
      .eq("issue_resolved", false);

    if (iError) throw iError;
    if (unresolved && unresolved.length > 0) {
      const err = new Error("Cannot complete: There are unresolved issues for this vehicle.");
      err.status = 400;
      throw err;
    }

    // 3. Add to completed_trucks
    const { data, error } = await db
      .from("completed_trucks")
      .insert([{ visit_id: visitId, truck_id: visit.truck_id }])
      .select();

    if (error) {
      if (error.code === '23505') { // Unique violation
        const err = new Error("This visit is already marked as completed.");
        err.status = 400;
        throw err;
      }
      throw error;
    }
    return data[0];
  }
};
