package com.msn.valentinesgarage.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msn.valentinesgarage.screens.tasks.composables.CategoryCard
import com.msn.valentinesgarage.screens.tasks.composables.CompletedTaskCard
import com.msn.valentinesgarage.screens.tasks.composables.SectionTitle
import com.msn.valentinesgarage.screens.tasks.composables.TaskCard
import com.msn.valentinesgarage.screens.tasks.composables.TaskCategoryUi
import com.msn.valentinesgarage.screens.tasks.composables.TaskUi
import com.msn.valentinesgarage.screens.tasks.composables.WeeklyCalendar
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun TasksScreen(
	modifier: Modifier = Modifier,
	dayNumbers: List<Int> = listOf(22, 23, 24, 25, 26, 27),
	currentDay: Int = 24,
	categories: List<TaskCategoryUi> = listOf(
		TaskCategoryUi(name = "Maintenance", count = 4),
		TaskCategoryUi(name = "Inspection", count = 2),
		TaskCategoryUi(name = "Urgent", count = 1),
	),
	openTasks: List<TaskUi> = listOf(
		TaskUi(issueTitle = "Brake pad replacement", vehicleName = "Toyota Corolla - HSD343", time = "09:00 AM", status = "Open", actionText = "Take Task"),
		TaskUi(issueTitle = "Headlight wiring fault", vehicleName = "Volvo FH16 - JKL918", time = "11:30 AM", status = "Open", actionText = "Take Task"),
	),
	assignedTasks: List<TaskUi> = listOf(
		TaskUi(issueTitle = "Oil leak inspection", vehicleName = "Scania Railer - QTR552", time = "01:00 PM", status = "Assigned to you"),
		TaskUi(issueTitle = "Battery replacement", vehicleName = "MAN TGX - MNB127", time = "02:30 PM", status = "In progress"),
	),
	pendingIssueTasks: List<TaskUi> = listOf(
		TaskUi(issueTitle = "Engine warning diagnostics", vehicleName = "Isuzu NPR - TTX440", time = "04:00 PM", status = "Pending review"),
		TaskUi(issueTitle = "Suspension noise check", vehicleName = "Mercedes Actros - GHY773", time = "05:15 PM", status = "Pending parts"),
	),
	completedTasks: List<TaskUi> = listOf(
		TaskUi(issueTitle = "Fuel filter replacement", vehicleName = "DAF XF - UYE901", time = "Yesterday 03:10 PM", status = "Completed"),
		TaskUi(issueTitle = "Clutch calibration", vehicleName = "Iveco S-Way - LPO220", time = "Yesterday 11:40 AM", status = "Completed"),
	),
) {
	LazyColumn(
		modifier = modifier
			.background(AppColors.OrangeWhite)
			.statusBarsPadding()
			.padding(horizontal = 16.dp),
		contentPadding = PaddingValues(vertical = 20.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		item {
			SectionTitle(text = "Week")
			WeeklyCalendar(dayNumbers = dayNumbers, currentDay = currentDay)
		}

		item {
			SectionTitle(text = "Categories")
		}

		item {
			LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				items(categories) { category ->
					CategoryCard(category = category)
				}
			}
		}

		item {
			SectionTitle(text = "Open Tasks")
		}

		items(openTasks) { task ->
			TaskCard(task = task)
		}

		item {
			SectionTitle(text = "Assigned Tasks")
		}

		items(assignedTasks) { task ->
			TaskCard(task = task)
		}

		item {
			SectionTitle(text = "Pending Issues")
		}

		items(pendingIssueTasks) { task ->
			TaskCard(task = task)
		}

		item {
			SectionTitle(text = "Completed Tasks")
		}

		items(completedTasks) { task ->
			CompletedTaskCard(task = task)
		}
	}
}


@Preview(showBackground = true)
@Composable
fun PreviewTasksScreen() {
	TasksScreen()
}

