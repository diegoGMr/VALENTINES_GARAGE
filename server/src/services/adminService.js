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
      .eq("issue_resolved", false);

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
            .eq("issue_resolved", false);

        const { data: completedTodayData } = await db
            .from("issues")
            .select("issue_id")
            .eq("mechanic_id", m.user_id)
            .eq("issue_resolved", true);

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
  },

  async getMechanicHistoryByVisit() {
    const { data: visits, error: visitError } = await db
      .from("visit")
      .select(
        "visit_id, booking_id, client_id, truck_id, mechanic_id, client_notes, bookings!visit_booking_id_fkey(booking_id, booking_date, booking_time, client_notes), clients!fk_client(user_id, full_name, email, phone), trucks!fk_truck(truck_id, license_plate, kilometers, speciality_trucks(name)), issues(issue_id, issue_description, issue_resolved, issue_resolution_notes, cost, mechanic_id, created_at), visit_mechanics(mechanic_id), completed_trucks(visit_id)"
      )
      .order("visit_id", { ascending: false });

    if (visitError) {
      console.error("Error fetching mechanic history visits:", visitError.message);
      throw new Error("Failed to fetch mechanic visit history");
    }

    const mechanicIds = new Set();
    (visits || []).forEach((visit) => {
      if (visit.mechanic_id) mechanicIds.add(Number(visit.mechanic_id));
      (visit.visit_mechanics || []).forEach((vm) => {
        if (vm.mechanic_id) mechanicIds.add(Number(vm.mechanic_id));
      });
      (visit.issues || []).forEach((issue) => {
        if (issue.mechanic_id) mechanicIds.add(Number(issue.mechanic_id));
      });
    });

    let usersById = {};
    if (mechanicIds.size > 0) {
      const { data: users, error: userError } = await db
        .from("users")
        .select("user_id, full_name, email, role")
        .in("user_id", Array.from(mechanicIds));

      if (userError) {
        console.error("Error fetching mechanic users:", userError.message);
        throw new Error("Failed to fetch mechanic user details");
      }

      usersById = (users || []).reduce((acc, user) => {
        acc[user.user_id] = user;
        return acc;
      }, {});
    }

    return (visits || []).map((visit) => {
      const assignedMechanicIds = new Set();
      if (visit.mechanic_id) assignedMechanicIds.add(Number(visit.mechanic_id));
      (visit.visit_mechanics || []).forEach((vm) => {
        if (vm.mechanic_id) assignedMechanicIds.add(Number(vm.mechanic_id));
      });

      const mechanics = Array.from(assignedMechanicIds).map((id) => {
        const user = usersById[id];
        return {
          user_id: id,
          full_name: user?.full_name || `Mechanic #${id}`,
          role: user?.role || "mechanic",
          email: user?.email || null,
        };
      });

      const issues = (visit.issues || []).map((issue) => {
        const issueMechanic = usersById[issue.mechanic_id];
        return {
          issue_id: issue.issue_id,
          issue_description: issue.issue_description,
          issue_resolved: issue.issue_resolved === true,
          issue_resolution_notes: issue.issue_resolution_notes || null,
          cost: issue.cost != null ? Number(issue.cost) : null,
          mechanic: {
            user_id: issue.mechanic_id,
            full_name: issueMechanic?.full_name || `Mechanic #${issue.mechanic_id}`,
          },
          created_at: issue.created_at || null,
        };
      });

      const totalCost = issues.reduce((sum, issue) => sum + (issue.cost || 0), 0);
      const resolvedCount = issues.filter((issue) => issue.issue_resolved).length;

      return {
        visit_id: visit.visit_id,
        booking: visit.bookings
          ? {
              booking_id: visit.bookings.booking_id,
              booking_date: visit.bookings.booking_date,
              booking_time: visit.bookings.booking_time,
              client_notes: visit.bookings.client_notes,
            }
          : null,
        client: visit.clients
          ? {
              user_id: visit.clients.user_id,
              full_name: visit.clients.full_name,
              email: visit.clients.email,
              phone: visit.clients.phone,
            }
          : null,
        truck: visit.trucks
          ? {
              truck_id: visit.trucks.truck_id,
              license_plate: visit.trucks.license_plate,
              kilometers: visit.trucks.kilometers,
              speciality: visit.trucks.speciality_trucks?.name || null,
            }
          : null,
        service_notes: visit.client_notes || null,
        mechanics,
        issues,
        summary: {
          total_issues: issues.length,
          resolved_issues: resolvedCount,
          unresolved_issues: issues.length - resolvedCount,
          total_cost: totalCost,
          is_completed: (visit.completed_trucks || []).length > 0,
        },
      };
    });
  }
};
