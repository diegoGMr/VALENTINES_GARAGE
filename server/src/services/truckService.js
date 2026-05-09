import { db } from "../db.js";

export const truckService = {
  async registerTruck(truckData) {
    // 1. Ensure the user exists in the 'clients' table if they are a client
    // because of the foreign key constraint on user_id.
    const { data: user } = await db.from("users").select("role, full_name, email, phone").eq("user_id", truckData.user_id).single();

    if (user && user.role === 'client') {
      const { data: client } = await db.from("clients").select("user_id").eq("user_id", truckData.user_id).single();
      if (!client) {
        console.log("Auto-creating missing client entry for user:", truckData.user_id);
        await db.from("clients").insert([{
          user_id: truckData.user_id,
          full_name: user.full_name || "Unknown",
          email: user.email,
          phone: user.phone
        }]);
      }
    }

    // 2. Insert the truck
    const { data, error } = await db.from("trucks").insert([truckData]).select();
    if (error) {
        console.error("Supabase insert error:", error);
        throw error;
    }
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
