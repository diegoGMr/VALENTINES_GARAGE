import { db } from "../db.js";

export const clientService = {
  async registerClient(clientData) {
    const { data, error } = await db.from("clients").insert([clientData]).select();
    if (error) throw error;
    return data[0];
  },

  async getClientById(clientId) {
    const { data, error } = await db.from("clients").select("*").eq("user_id", clientId).single();
    if (error) throw error;
    return data;
  },

  async getClientByHex(hexId) {
    // Note: hex_id/visit_reference was removed from schema.
    // This might need a different implementation or removal if no longer needed.
    const { data, error } = await db.from("clients").select("*").eq("user_id", hexId).single();
    if (error) throw error;
    return data;
  },

  async updateClient(clientId, clientData) {
    const { error } = await db.from("clients").update(clientData).eq("user_id", clientId);
    if (error) throw error;
    return true;
  }
};
