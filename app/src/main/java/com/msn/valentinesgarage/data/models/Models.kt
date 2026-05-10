package com.msn.valentinesgarage.data.models

import com.google.gson.annotations.SerializedName

// ── User ──────────────────────────────────────────────
data class User(
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("full_name") val full_name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val role: String,
    @SerializedName("created_at") val created_at: String? = null,
    @SerializedName("updated_at") val updated_at: String? = null,
)

// ── Auth ──────────────────────────────────────────────
data class LoginRequest(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val message: String,
    val token: String,
    @SerializedName("user_id") val user_id: Int,
    val role: String,
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String?,
    val role: String,
)

data class RegisterResponse(
    val userId: Int,
    val token: String,
    val role: String,
)

// ── Client ────────────────────────────────────────────
data class Client(
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("full_name") val full_name: String,
    val email: String?,
    val phone: String?,
    val address: String? = null,
    @SerializedName("company_name") val company_name: String? = null,
)

data class RegisterClientRequest(
    @SerializedName("full_name") val full_name: String,
    val email: String?,
    val phone: String?,
)

data class RegisterClientResponse(
    val clientId: Int,
)

data class ClientVisit(
    @SerializedName("visit_id") val visit_id: Int,
    @SerializedName("client_id") val client_id: Int?,
    @SerializedName("truck_id") val truck_id: Int?,
    @SerializedName("mechanic_id") val mechanic_id: Int?,
    @SerializedName("client_notes") val client_notes: String?,
)

data class MechanicVisit(
    @SerializedName("visit_id") val visitId: Int,
    @SerializedName("client_id") val clientId: Int?,
    @SerializedName("truck_id") val truckId: Int?,
    @SerializedName("mechanic_id") val mechanicId: Int?,
    @SerializedName("client_notes") val clientNotes: String?,
    @SerializedName("trucks") val truck: Truck?,
    @SerializedName("clients") val client: Client?,
    val issues: List<Issue> = emptyList(),
    @SerializedName("completed_trucks") val completedInfo: List<Map<String, Any>>? = null,
)

data class NewVisitRequest(
    @SerializedName("client_id") val client_id: Int,
    val notes: String?,
)

data class NewVisitResponse(
    val visitId: Int,
)

data class SpecialityInfo(
    @SerializedName("name") val name: String
)

// ── Truck ─────────────────────────────────────────────
data class Truck(
    @SerializedName("truck_id") val truck_id: Int,
    @SerializedName("license_plate") val plate_number: String,
    @SerializedName("truck_image_url") val truck_image_url: String?,
    val kilometers: Int?,
    @SerializedName("last_service_date") val last_service_date: String?,
    @SerializedName("next_service_due") val next_service_due: String?,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("speciality_id") val speciality_id: Int,
    @SerializedName("speciality_trucks") val speciality: SpecialityInfo? = null,
)

data class RegisterTruckRequest(
    @SerializedName("license_plate") val plate_number: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("speciality_id") val specialityId: Int,
    @SerializedName("truck_image_url") val imageUrl: String? = null,
    val kilometers: Int? = null,
)

data class RegisterTruckResponse(
    @SerializedName("truck_id") val truckId: Int,
)

// ── Booking ───────────────────────────────────────────
data class Booking(
    @SerializedName("booking_id") val id: Int,
    @SerializedName("client_id") val clientId: Int?,
    @SerializedName("truck_id") val truckId: Int?,
    @SerializedName("booking_date") val date: String?,
    @SerializedName("booking_time") val time: String?,
    @SerializedName("client_notes") val clientNotes: String?,
    @SerializedName("mechanic_id") val mechanicId: Int?,
    @SerializedName("clients") val client: Client? = null,
    @SerializedName("trucks") val truck: Truck? = null,
    @SerializedName("visit") val visit: List<Map<String, Any>>? = null,
)

data class CreateBookingRequest(
    @SerializedName("client_id") val clientId: Int,
    @SerializedName("truck_id") val truckId: Int,
    @SerializedName("booking_date") val date: String,
    @SerializedName("booking_time") val time: String,
    @SerializedName("client_notes") val clientNotes: String?,
)

data class CreateBookingResponse(
    @SerializedName("booking_id") val bookingId: Int,
)

// ── Issue ─────────────────────────────────────────────
data class Issue(
    @SerializedName("issue_id") val id: Int,
    @SerializedName("visit_id") val visitId: Int,
    @SerializedName("issue_description") val description: String,
    @SerializedName("mechanic_id") val mechanicId: Int,
    @SerializedName("issue_resolved?") val resolved: Boolean? = false,
    @SerializedName("created_at") val created_at: String? = null,
    val status: String? = null,
    val visit: IssueVisit? = null,
)

data class IssueVisit(
    @SerializedName("visit_id") val visitId: Int,
    val trucks: Truck? = null,
)

data class CreateIssueRequest(
    @SerializedName("visit_id") val visitId: Int,
    @SerializedName("issue_description") val description: String,
    @SerializedName("mechanic_id") val mechanicId: Int? = null,
)

data class CreateIssueResponse(
    val issueId: Int,
)

// ── Admin ─────────────────────────────────────────────
data class AdminUserRead(
    @SerializedName("user_id") val id: Int,
    @SerializedName("full_name") val full_name: String,
    val email: String,
    val role: String,
)

data class AdminStatsResponse(
    @SerializedName("totalTrucks") val totalTrucks: Int,
    @SerializedName("totalMechanics") val totalMechanics: Int,
    @SerializedName("openIssues") val openIssues: Int,
    @SerializedName("todayAppointments") val todayAppointments: Int
)

data class MechanicWorkloadResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("openTasks") val openTasks: Int,
    @SerializedName("completedToday") val completedToday: Int
)
