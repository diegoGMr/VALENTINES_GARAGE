package com.msn.valentinesgarage.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.screens.tasks.composables.CategoryFilterChip
import com.msn.valentinesgarage.screens.tasks.composables.CompletedTaskCard
import com.msn.valentinesgarage.screens.tasks.composables.TaskCard
import com.msn.valentinesgarage.screens.tasks.composables.TaskNoteDialog
import com.msn.valentinesgarage.screens.tasks.composables.TaskStatus
import com.msn.valentinesgarage.screens.tasks.composables.TasksEmptyState
import com.msn.valentinesgarage.screens.tasks.composables.TasksSectionTitle
import com.msn.valentinesgarage.screens.tasks.composables.WeeklyCalendar
import com.msn.valentinesgarage.screens.tasks.viewmodels.TasksViewModel
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun TasksScreen(
	modifier: Modifier = Modifier,
	viewModel: TasksViewModel = viewModel(),
) {
	val state by viewModel.uiState.collectAsState()
	val allTasks = viewModel.filteredTasks(state)

	val openTasks      = allTasks.filter { it.status == TaskStatus.Open }
	val assignedTasks  = allTasks.filter { it.status == TaskStatus.Assigned || it.status == TaskStatus.InProgress }
	val pendingTasks   = allTasks.filter { it.status == TaskStatus.PendingReview || it.status == TaskStatus.PendingParts }
	val completedTasks = allTasks.filter { it.status == TaskStatus.Completed }

	Box(modifier = modifier.fillMaxSize()) {

		//main content
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.background(AppColors.OrangeWhite)
				.statusBarsPadding()
				.padding(horizontal = 16.dp),
			contentPadding = PaddingValues(vertical = 20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {

			// week strip
			item(key = "week") {
				TasksSectionTitle(text = "Week")
				WeeklyCalendar(
					dayNumbers = viewModel.dayNumbers,
					selectedDay = state.selectedDay,
					onDaySelected = viewModel::onDaySelected,
				)
			}

			item(key = "categories_title") {
				TasksSectionTitle(text = "Categories")
			}
			item(key = "categories_row") {
				LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
					items(viewModel.categories, key = { it.name }) { category ->
						CategoryFilterChip(
							category = category,
							isSelected = (state.selectedCategory ?: "All") == category.name,
							onClick = {
								viewModel.onCategorySelected(
									if (category.name == "All") null else category.name
								)
							},
						)
					}
				}
			}

			//open tasks
			item(key = "open_title") { TasksSectionTitle(text = "Open Tasks") }
			if (openTasks.isEmpty()) {
				item(key = "open_empty") {
					TasksEmptyState(message = "No open tasks — all claimed!")
				}
			} else {
				items(openTasks, key = { it.id }) { task ->
					TaskCard(
						task = task,
						onTakeTask = viewModel::onTakeTask,
					)
				}
			}

//assigned tasks
			item(key = "assigned_title") { TasksSectionTitle(text = "Assigned Tasks") }
			if (assignedTasks.isEmpty()) {
				item(key = "assigned_empty") {
					TasksEmptyState(message = "No tasks assigned to you yet")
				}
			} else {
				items(assignedTasks, key = { it.id }) { task ->
					TaskCard(
						task = task,
						onAddNote = viewModel::onOpenNoteDialog,
					)
				}
			}

//pending tasks
			item(key = "pending_title") { TasksSectionTitle(text = "Pending Issues") }
			if (pendingTasks.isEmpty()) {
				item(key = "pending_empty") {
					TasksEmptyState(message = "No tasks pending")
				}
			} else {
				items(pendingTasks, key = { it.id }) { task ->
					TaskCard(task = task)
				}
			}

//completed tasks
			item(key = "completed_title") { TasksSectionTitle(text = "Completed Tasks") }
			if (completedTasks.isEmpty()) {
				item(key = "completed_empty") {
					TasksEmptyState(message = "No completed tasks yet")
				}
			} else {
				items(completedTasks, key = { it.id }) { task ->
					CompletedTaskCard(task = task)
				}
			}
		}

		state.noteDialogTaskId?.let { taskId ->
			val task = state.tasks.find { it.id == taskId } ?: return@let
			TaskNoteDialog(
				task = task,
				noteValue = state.noteInput,
				onNoteChanged = viewModel::onNoteInputChanged,
				onSaveNote = viewModel::onSaveNote,
				onCompleteTask = viewModel::onCompleteTask,
				onDismiss = viewModel::onDismissNoteDialog,
			)
		}
	}
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun PreviewTasksScreen() {
	TasksScreen()
}
