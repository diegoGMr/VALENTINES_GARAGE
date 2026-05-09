import { db } from "../db.js";
import crypto from "crypto";

export const visitService = {
  async createVisit(visitData) {
    const { data, error } = await db.from("visit").insert([visitData]).select();
    if (error) throw error;
    return data[0];
  },

  async getAllIssues() {
    const { data, error } = await db
      .from("issues")
      .select("*, visit(visit_id, trucks!fk_truck(truck_id, license_plate))")
      .order("issue_id", { ascending: false });
    if (error) throw error;
    return data;
  },

  async createIssue(issueData) {
    const preparedData = {
      visit_id: issueData.visit_id,
      issue_description: issueData.issue_description,
      mechanic_id: issueData.mechanic_id,
      "issue_resolved?": issueData.issue_resolved ?? false
    };
    const { data, error } = await db.from("issues").insert([preparedData]).select();
    if (error) throw error;
    return data[0];
  },

  async resolveIssue(issueId) {
    const { data, error } = await db
      .from("issues")
      .update({ "issue_resolved?": true })
      .eq("issue_id", issueId)
      .select();
    if (error) throw error;
    return data[0];
  },

  async getBookings(date) {
    const { data, error } = await db
      .from("bookings")
      .select("*, clients!bookings_client_id_fkey(full_name), trucks!bookings_vehicle_id_fkey(*, speciality_trucks(name)), visit(booking_id)")
      .eq("booking_date", date);
    if (error) throw error;
    return data;
  },

  async createBooking(bookingData) {
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
    // 1. Get booking details
    const { data: booking, error: bError } = await db.from("bookings").select("*").eq("booking_id", bookingId).single();
    if (bError) throw bError;

    // 2. Create a visit record linked to the booking
    const visitData = {
      client_id: booking.client_id,
      truck_id: booking.truck_id,
      mechanic_id: mechanicId,
      booking_id: bookingId,
      client_notes: booking.client_notes || `From booking #${bookingId}`
    };

    return await this.createVisit(visitData);
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
      .eq("issue_resolved?", false);

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
