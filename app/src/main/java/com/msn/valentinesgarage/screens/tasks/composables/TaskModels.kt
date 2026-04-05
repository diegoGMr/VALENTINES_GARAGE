package com.msn.valentinesgarage.screens.tasks.composables
enum class TaskStatus(val label: String) {
    Open("Open"),
    Assigned("Assigned to you"),
    InProgress("In progress"),
    PendingReview("Pending review"),
    PendingParts("Pending parts"),
    Completed("Completed"),
}

data class TaskUi(
    val id: String,
    val issueTitle: String,
    val vehicleName: String,
    val time: String,
    val category: String,
    val status: TaskStatus,
    val actionText: String? = null,
    val notes: String = "",
    val completedAt: String? = null,
)

data class TaskCategoryUi(
    val name: String,
    val count: Int,
)
