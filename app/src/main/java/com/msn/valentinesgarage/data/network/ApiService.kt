package com.msn.valentinesgarage.data.network

import com.msn.valentinesgarage.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth / User ───────────────────────────────────
    @POST("user/loginUser")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("user/registerUser")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("user/getUserWithId/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<User>

    @PUT("user/updateUser/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: RegisterRequest,
    ): Response<Map<String, Int>>

    // ── Client ────────────────────────────────────────
    @POST("client/register")
    suspend fun registerClient(
        @Header("Authorization") token: String,
        @Body request: RegisterClientRequest,
    ): Response<RegisterClientResponse>

    @GET("client/getClientById/{clientId}")
    suspend fun getClientById(
        @Header("Authorization") token: String,
        @Path("clientId") clientId: Int,
    ): Response<Client>

    @GET("client/getClientByHex/{hexId}")
    suspend fun getClientByHex(
        @Header("Authorization") token: String,
        @Path("hexId") hexId: String,
    ): Response<Client>

    @POST("client/newVisit")
    suspend fun createClientVisit(
        @Header("Authorization") token: String,
        @Body request: NewVisitRequest,
    ): Response<NewVisitResponse>

    // ── Truck ─────────────────────────────────────────
    @POST("truck/register")
    suspend fun registerTruck(
        @Header("Authorization") token: String,
        @Body request: RegisterTruckRequest,
    ): Response<RegisterTruckResponse>

    @GET("truck/getTruck/{id}")
    suspend fun getTruckById(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<Truck>

    @GET("truck/my-trucks")
    suspend fun getUserTrucks(
        @Header("Authorization") token: String,
    ): Response<List<Truck>>

    @GET("truck/specialities")
    suspend fun getSpecialities(
        @Header("Authorization") token: String,
    ): Response<List<Map<String, Any>>>

    // ── Booking ───────────────────────────────────────
    @GET("booking")
    suspend fun getBookings(
        @Header("Authorization") token: String,
        @Query("date") date: String,
    ): Response<List<Booking>>

    @GET("bookings/available")
    suspend fun getAvailableBookings(
        @Header("Authorization") token: String,
    ): Response<List<Booking>>

    @POST("booking")
    suspend fun createBooking(
        @Header("Authorization") token: String,
        @Body request: CreateBookingRequest,
    ): Response<CreateBookingResponse>

    @POST("booking/{id}/assign")
    suspend fun assignMechanicToBooking(
        @Header("Authorization") token: String,
        @Path("id") bookingId: Int,
    ): Response<ClientVisit>

    // ── Issues ────────────────────────────────────────
    @GET("issues")
    suspend fun getIssues(
        @Header("Authorization") token: String,
    ): Response<List<Issue>>

    @GET("my-visits")
    suspend fun getMechanicVisits(
        @Header("Authorization") token: String,
    ): Response<List<MechanicVisit>>

    @GET("mechanic/service-history")
    suspend fun getMechanicServiceHistory(
        @Header("Authorization") token: String,
    ): Response<List<MechanicVisit>>

    @GET("visits/active")
    suspend fun getActiveVisits(
        @Header("Authorization") token: String,
    ): Response<List<MechanicVisit>>

    @POST("issues")
    suspend fun createIssue(
        @Header("Authorization") token: String,
        @Body request: CreateIssueRequest,
    ): Response<CreateIssueResponse>

    @PUT("issues/{id}/resolve")
    suspend fun resolveIssue(
        @Header("Authorization") token: String,
        @Path("id") issueId: Int,
        @Body request: ResolveIssueRequest,
    ): Response<Issue>

    @POST("visits/{id}/complete")
    suspend fun completeVisit(
        @Header("Authorization") token: String,
        @Path("id") visitId: Int,
    ): Response<Map<String, Any>>

    // ── Admin ─────────────────────────────────────────
    @GET("admin/stats")
    suspend fun getAdminStats(
        @Header("Authorization") token: String,
    ): Response<AdminStatsResponse>

    @GET("admin/mechanics/workload")
    suspend fun getMechanicWorkload(
        @Header("Authorization") token: String,
    ): Response<List<MechanicWorkloadResponse>>

    @GET("admin/database/stats")
    suspend fun getDatabaseStats(
        @Header("Authorization") token: String,
    ): Response<Map<String, Int>>

    @GET("admin/read/users")
    suspend fun getAdminUsers(
        @Header("Authorization") token: String,
    ): Response<List<AdminUserRead>>

    @GET("admin/mechanics/history")
    suspend fun getAdminMechanicHistory(
        @Header("Authorization") token: String,
    ): Response<List<AdminMechanicHistoryVisit>>

    @GET("client/my-progress")
    suspend fun getClientProgress(
        @Header("Authorization") token: String,
    ): Response<List<MechanicVisit>>
}
