import { db } from "../db.js";

export const truckService = {
  async registerTruck(truckData) {
    const { data, error } = await db.from("trucks").insert([truckData]).select();
    if (error) throw error;
    return data[0];
  },

  async getTruckById(truckId) {
    const { data, error } = await db.from("trucks").select("*").eq("truck_id", truckId).single();
    if (error) throw error;
    return data;
  },

  async getTrucksByUserId(userId) {
    const { data, error } = await db.from("trucks").select("*").eq("user_id", userId);
    if (error) throw error;
    return data;
  },

  async getSpecialities() {
    const { data, error } = await db.from("speciality_trucks").select("*");
    if (error) throw error;
    return data;
  }
};
