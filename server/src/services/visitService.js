import { db } from "../db.js";
import crypto from "crypto";

export const visitService = {
  async createVisit(visitData) {
    if (!visitData.visit_reference) {
      visitData.visit_reference = crypto.randomBytes(8).toString('hex');
    }
    const { data, error } = await db.from("visits").insert([visitData]).select();
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
  }
};
