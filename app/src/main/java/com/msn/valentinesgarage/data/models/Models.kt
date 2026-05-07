package com.msn.valentinesgarage.data.models

import com.google.gson.annotations.SerializedName

// ── User ──────────────────────────────────────────────
data class User(
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("hex_id") val hex_id: String,
    @SerializedName("full_name") val full_name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val role: String,
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
)

// ── Client ────────────────────────────────────────────
data class Client(
    @SerializedName("client_id") val client_id: Int,
    @SerializedName("hex_id") val hex_id: String,
    @SerializedName("full_name") val full_name: String,
    val email: String?,
    val phone: String?,
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
    @SerializedName("client_id") val client_id: Int,
    @SerializedName("visit_date") val visit_date: String,
    val notes: String?,
)

data class NewVisitRequest(
    @SerializedName("client_id") val client_id: Int,
    val notes: String?,
)

data class NewVisitResponse(
    val visitId: Int,
)

// ── Truck ─────────────────────────────────────────────
data class Truck(
    @SerializedName("truck_id") val truck_id: Int,
    @SerializedName("hex_id") val hex_id: String,
    @SerializedName("plate_number") val plate_number: String,
    val make: String?,
    val model: String?,
    val year: Int?,
    val status: String?,
)

data class RegisterTruckRequest(
    @SerializedName("plate_number") val plate_number: String,
    val make: String?,
    val model: String?,
    val year: Int?,
)

data class RegisterTruckResponse(
    val truckId: Int,
)

data class BookingSlot(
    val id: Int,
    @SerializedName("slot_date") val slot_date: String,
    @SerializedName("customer_name") val customer_name: String,
    @SerializedName("created_by_user_id") val created_by_user_id: Int,
)

data class BookingSlotsResponse(
    val date: String,
    val usedSlots: Int,
    val maxSlots: Int,
    val remainingSlots: Int,
    val bookings: List<BookingSlot>,
)

data class CreateBookingRequest(
    val date: String,
    @SerializedName("customerName") val customerName: String,
)

data class CreateBookingResponse(
    val bookingId: Int,
)

data class Issue(
    @SerializedName("issue_id") val id: Int,
    @SerializedName("title") val title: String? = null,
    @SerializedName("issue_description") val description: String,
    @SerializedName("status") val status: String? = "Pending",
    @SerializedName("created_at") val created_at: String? = null,
)

data class CreateIssueRequest(
    @SerializedName("visit_id") val visit_id: Int,
    @SerializedName("issue_description") val description: String,
    @SerializedName("mechanic_id") val mechanic_id: Int? = null,
)

data class CreateIssueResponse(
    val issueId: Int,
)

data class Task(
    val id: Int,
    @SerializedName("issue_id") val issue_id: Int,
    val title: String,
    val description: String,
    @SerializedName("assigned_category") val assigned_category: String,
    @SerializedName("created_at") val created_at: String,
)

data class CreateTaskRequest(
    @SerializedName("issueId") val issueId: Int,
    val title: String,
    val description: String,
    @SerializedName("assignedCategory") val assignedCategory: String,
)

data class CreateTaskResponse(
    val taskId: Int,
)

data class AdminUserRead(
    @SerializedName("user_id") val id: Int,
    @SerializedName("full_name") val full_name: String,
    val email: String,
    val role: String,
)
