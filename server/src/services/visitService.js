import { db } from "../db.js";
import crypto from "crypto";

export const visitService = {
  async createVisit(visitData) {
    const { data, error } = await db.from("visit").insert([visitData]).select();
    if (error) throw error;
    return data[0];
  },

  async getAllIssues() {
    const { data, error } = await db.from("issues").select("*").order("issue_id", { ascending: false });
    if (error) throw error;
    return data;
  },

  async createIssue(issueData) {
    const { data, error } = await db.from("issues").insert([issueData]).select();
    if (error) throw error;
    return data[0];
  },

  async getBookings(date) {
    const { data, error } = await db.from("bookings").select("*").eq("booking_date", date);
    if (error) throw error;
    return data;
  },

  async createBooking(bookingData) {
    const { data, error } = await db.from("bookings").insert([bookingData]).select();
    if (error) throw error;
    return data[0];
  },

  async assignMechanicToBooking(bookingId, mechanicId) {
    // 1. Get booking details
    const { data: booking, error: bError } = await db.from("bookings").select("*").eq("booking_id", bookingId).single();
    if (bError) throw bError;

    // 2. Create a visit record
    const visitData = {
      client_id: booking.client_id,
      truck_id: booking.vehicle_id,
      mechanic_id: mechanicId,
      client_notes: `From booking #${bookingId}`
    };

    return await this.createVisit(visitData);
  }
};
