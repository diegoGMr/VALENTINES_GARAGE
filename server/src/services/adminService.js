import { db } from "../db.js";

export const adminService = {
  async getStats() {
    console.log("Fetching admin overview stats...");

    // Total Trucks - Using .length for maximum reliability in this environment
    const { data: trucks, error: truckError } = await db
      .from("trucks")
      .select("truck_id");

    // Mechanics - Strictly role 'mechanic'
    const { data: mechanics, error: mechanicError } = await db
      .from("users")
      .select("user_id")
      .eq("role", "mechanic");

    // Open Issues
    const { data: openIssues, error: issueError } = await db
      .from("issues")
      .select("issue_id")
      .eq("issue_resolved?", false);

    // Today's Appointments
    const today = new Date().toISOString().split('T')[0];
    const { data: appointments, error: appointmentError } = await db
      .from("bookings")
      .select("booking_id")
      .eq("booking_date", today);

    if (truckError || mechanicError || issueError || appointmentError) {
        console.error("Stats fetch error:", { truckError, mechanicError, issueError, appointmentError });
        throw new Error("Failed to fetch admin stats");
    }

    const stats = {
      totalTrucks: trucks?.length || 0,
      totalMechanics: mechanics?.length || 0,
      openIssues: openIssues?.length || 0,
      todayAppointments: appointments?.length || 0
    };

    console.log("Calculated Overview Stats:", stats);
    return stats;
  },

  async getMechanicWorkload() {
    console.log("Fetching mechanic workload list...");

    const { data: mechanics, error: mechError } = await db
        .from("users")
        .select("user_id, full_name, role")
        .eq("role", "mechanic");

    if (mechError) {
      console.error("Error fetching mechanics:", mechError.message);
      throw mechError;
    }

    const workload = await Promise.all(mechanics.map(async (m) => {
        // Using .length for individual counts as well to ensure UI display
        const { data: openTasksData } = await db
            .from("issues")
            .select("issue_id")
            .eq("mechanic_id", m.user_id)
            .eq("issue_resolved?", false);

        const { data: completedTodayData } = await db
            .from("issues")
            .select("issue_id")
            .eq("mechanic_id", m.user_id)
            .eq("issue_resolved?", true);

        return {
            id: m.user_id.toString(),
            name: m.full_name,
            role: m.role,
            openTasks: openTasksData?.length || 0,
            completedToday: completedTodayData?.length || 0
        };
    }));

    console.log(`Successfully fetched workload for ${workload.length} mechanics.`);
    return workload;
  },

  async getDatabaseStats() {
    const counts = {};
    const tables = ["users", "trucks", "issues", "bookings"];

    await Promise.all(tables.map(async (table) => {
        const { data, error } = await db.from(table).select("*");
        if (!error) counts[table] = data?.length || 0;
    }));

    return counts;
  }
};
