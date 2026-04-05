package com.msn.valentinesgarage.screens.tasks.viewmodels

import androidx.lifecycle.ViewModel
import com.msn.valentinesgarage.screens.tasks.composables.TaskCategoryUi
import com.msn.valentinesgarage.screens.tasks.composables.TaskStatus
import com.msn.valentinesgarage.screens.tasks.composables.TaskUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class TasksUiState(
    val selectedDay: Int = 24,
    val selectedCategory: String? = null,
    val tasks: List<TaskUi> = emptyList(),
    val noteDialogTaskId: String? = null,
    val noteInput: String = "",
    val confirmCompleteTaskId: String? = null,
)

class TasksViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState(tasks = sampleTasks))
    val uiState: StateFlow<TasksUiState> = _uiState

    val dayNumbers = listOf(22, 23, 24, 25, 26, 27, 28)

    val categories = listOf(
        TaskCategoryUi(name = "All", count = sampleTasks.size),
        TaskCategoryUi(name = "Maintenance", count = sampleTasks.count { it.category == "Maintenance" }),
        TaskCategoryUi(name = "Inspection", count = sampleTasks.count { it.category == "Inspection" }),
        TaskCategoryUi(name = "Urgent", count = sampleTasks.count { it.category == "Urgent" }),
    )

    fun onDaySelected(day: Int) {
        _uiState.update { it.copy(selectedDay = day) }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    /**mechanic claims an open task — sets it to "Assigned to you"*/
    fun onTakeTask(taskId: String) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map { task ->
                    if (task.id == taskId) task.copy(status = TaskStatus.Assigned, actionText = "Add Note / Complete")
                    else task
                }
            )
        }
    }

    /*oppen the note dialog for a specific task */
    fun onOpenNoteDialog(taskId: String) {
        val existing = _uiState.value.tasks.find { it.id == taskId }?.notes ?: ""
        _uiState.update { it.copy(noteDialogTaskId = taskId, noteInput = existing) }
    }

    fun onNoteInputChanged(note: String) {
        _uiState.update { it.copy(noteInput = note) }
    }

    /** save note to task without completing */
    fun onSaveNote() {
        val state = _uiState.value
        val taskId = state.noteDialogTaskId ?: return
        _uiState.update { s ->
            s.copy(
                tasks = s.tasks.map { t ->
                    if (t.id == taskId) t.copy(notes = s.noteInput) else t
                },
                noteDialogTaskId = null,
                noteInput = "",
            )
        }
    }

    /** mark task complete */
    fun onCompleteTask() {
        val state = _uiState.value
        val taskId = state.noteDialogTaskId ?: state.confirmCompleteTaskId ?: return
        _uiState.update { s ->
            s.copy(
                tasks = s.tasks.map { t ->
                    if (t.id == taskId) t.copy(
                        status = TaskStatus.Completed,
                        notes = if (s.noteInput.isNotBlank()) s.noteInput else t.notes,
                        actionText = null,
                        completedAt = "Today ${java.time.LocalTime.now().let { "%02d:%02d".format(it.hour, it.minute) }}",
                    ) else t
                },
                noteDialogTaskId = null,
                confirmCompleteTaskId = null,
                noteInput = "",
            )
        }
    }

    fun onDismissNoteDialog() {
        _uiState.update { it.copy(noteDialogTaskId = null, noteInput = "") }
    }

    fun onRequestConfirmComplete(taskId: String) {
        _uiState.update { it.copy(confirmCompleteTaskId = taskId) }
    }

    fun onDismissConfirmComplete() {
        _uiState.update { it.copy(confirmCompleteTaskId = null) }
    }

    /*filtered task lists used by the screen*/
    fun filteredTasks(state: TasksUiState): List<TaskUi> {
        val cat = state.selectedCategory
        return if (cat == null || cat == "All") state.tasks
        else state.tasks.filter { it.category == cat }
    }
}




private val sampleTasks = listOf(
    TaskUi(
        id = "t1",
        issueTitle = "Brake pad replacement",
        vehicleName = "Toyota Corolla – HSD343",
        time = "09:00 AM",
        category = "Maintenance",
        status = TaskStatus.Open,
        actionText = "Take Task",
    ),
    TaskUi(
        id = "t2",
        issueTitle = "Headlight wiring fault",
        vehicleName = "Volvo FH16 – JKL918",
        time = "11:30 AM",
        category = "Urgent",
        status = TaskStatus.Open,
        actionText = "Take Task",
    ),
    TaskUi(
        id = "t3",
        issueTitle = "Oil leak inspection",
        vehicleName = "Scania Railer – QTR552",
        time = "01:00 PM",
        category = "Inspection",
        status = TaskStatus.Assigned,
        actionText = "Add Note / Complete",
        notes = "Found minor seep around rear main seal.",
    ),
    TaskUi(
        id = "t4",
        issueTitle = "Battery replacement",
        vehicleName = "MAN TGX – MNB127",
        time = "02:30 PM",
        category = "Maintenance",
        status = TaskStatus.InProgress,
        actionText = "Add Note / Complete",
    ),
    TaskUi(
        id = "t5",
        issueTitle = "Engine warning diagnostics",
        vehicleName = "Isuzu NPR – TTX440",
        time = "04:00 PM",
        category = "Inspection",
        status = TaskStatus.PendingReview,
    ),
    TaskUi(
        id = "t6",
        issueTitle = "Suspension noise check",
        vehicleName = "Mercedes Actros – GHY773",
        time = "05:15 PM",
        category = "Inspection",
        status = TaskStatus.PendingParts,
    ),
    TaskUi(
        id = "t7",
        issueTitle = "Fuel filter replacement",
        vehicleName = "DAF XF – UYE901",
        time = "Yesterday 03:10 PM",
        category = "Maintenance",
        status = TaskStatus.Completed,
        completedAt = "Yesterday 03:10 PM",
        notes = "Replaced with OEM filter. Test drive ok.",
    ),
    TaskUi(
        id = "t8",
        issueTitle = "Clutch calibration",
        vehicleName = "Iveco S-Way – LPO220",
        time = "Yesterday 11:40 AM",
        category = "Maintenance",
        status = TaskStatus.Completed,
        completedAt = "Yesterday 11:40 AM",
    ),
)
