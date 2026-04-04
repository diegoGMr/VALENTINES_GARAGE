package com.msn.valentinesgarage.data.models

// ── User ──────────────────────────────────────────────
data class User(
    val user_id: Int,
    val hex_id: String,
    val full_name: String,
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
    val user_id: Int,
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
    val client_id: Int,
    val hex_id: String,
    val full_name: String,
    val email: String?,
    val phone: String?,
)

data class RegisterClientRequest(
    val full_name: String,
    val email: String?,
    val phone: String?,
)

data class RegisterClientResponse(
    val clientId: Int,
)

data class ClientVisit(
    val visit_id: Int,
    val client_id: Int,
    val visit_date: String,
    val notes: String?,
)

data class NewVisitRequest(
    val client_id: Int,
    val notes: String?,
)

data class NewVisitResponse(
    val visitId: Int,
)

// ── Truck ─────────────────────────────────────────────
data class Truck(
    val truck_id: Int,
    val hex_id: String,
    val plate_number: String,
    val make: String?,
    val model: String?,
    val year: Int?,
    val status: String?,
)

data class RegisterTruckRequest(
    val plate_number: String,
    val make: String?,
    val model: String?,
    val year: Int?,
)

data class RegisterTruckResponse(
    val truckId: Int,
)
