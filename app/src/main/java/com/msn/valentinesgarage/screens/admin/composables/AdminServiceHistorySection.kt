package com.msn.valentinesgarage.screens.admin.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.screens.admin.viewmodels.AdminServiceFlowUi
import com.msn.valentinesgarage.screens.admin.viewmodels.AdminServiceIssueUi
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.NotesMedical
import compose.icons.fontawesomeicons.solid.Tools

@Composable
fun AdminServiceHistorySection(
    services: List<AdminServiceFlowUi>,
    modifier: Modifier = Modifier,
) {
    if (services.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Text(
                text = "No active services to summarize yet.",
                color = AppColors.TextHint,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    val groupedByMechanic = services.groupBy { it.mechanicName }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        groupedByMechanic.forEach { (mechanicName, mechanicServices) ->
            MechanicServiceGroup(
                mechanicName = mechanicName,
                role = mechanicServices.firstOrNull()?.mechanicRole.orEmpty(),
                services = mechanicServices,
            )
        }
    }
}

@Composable
private fun MechanicServiceGroup(
    mechanicName: String,
    role: String,
    services: List<AdminServiceFlowUi>,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(AppColors.OrangeWhite, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Tools,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(14.dp),
                    )
                }
                Column {
                    Text(
                        text = mechanicName,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.FontBlackStrong,
                    )
                    Text(
                        text = role.uppercase(),
                        color = AppColors.TextHint,
                        fontSize = 11.sp,
                    )
                }
            }

            services.forEachIndexed { index, service ->
                if (index > 0) {
                    HorizontalDivider(color = AppColors.LightGray.copy(alpha = 0.5f))
                }
                ServiceCard(service = service)
            }
        }
    }
}

@Composable
private fun ServiceCard(service: AdminServiceFlowUi) {
    val resolvedCount = service.issues.count { it.resolved }
    val totalCount = service.issues.size

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "SERVICE #${service.serviceId}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Orange,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Car,
                contentDescription = null,
                tint = AppColors.FontBlackSoft,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = "${service.vehicleLabel} • ${service.clientName}",
                fontSize = 13.sp,
                color = AppColors.FontBlackMedium,
            )
        }

        Text(
            text = "Booking: ${service.bookingId?.toString() ?: "Not linked"}",
            fontSize = 12.sp,
            color = AppColors.TextHint,
        )

        val notes = service.clientNotes?.takeIf { it.isNotBlank() } ?: "No client notes"
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.NotesMedical,
                contentDescription = null,
                tint = AppColors.TextHint,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = "Client Notes: $notes",
                fontSize = 12.sp,
                color = AppColors.FontBlackSoft,
            )
        }

        Text(
            text = "Issues: $resolvedCount/$totalCount resolved",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (resolvedCount == totalCount && totalCount > 0) AppColors.Green else AppColors.Orange,
        )

        if (service.issues.isEmpty()) {
            Text(
                text = "No issues reported yet for this service.",
                fontSize = 12.sp,
                color = AppColors.TextHint,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                service.issues.forEach { issue ->
                    IssueRow(issue = issue)
                }
            }
        }
    }
}

@Composable
private fun IssueRow(issue: AdminServiceIssueUi) {
    val icon = if (issue.resolved) FontAwesomeIcons.Solid.Check else FontAwesomeIcons.Solid.ExclamationTriangle
    val tint = if (issue.resolved) AppColors.Green else AppColors.Orange
    val resolution = issue.resolutionNotes?.takeIf { it.isNotBlank() } ?: "No resolution note yet"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.LightGray.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(11.dp),
            )
            Text(
                text = "Issue #${issue.issueId}: ${issue.description}",
                fontSize = 12.sp,
                color = AppColors.FontBlackMedium,
            )
        }

        Text(
            text = "Resolution: $resolution",
            fontSize = 11.sp,
            color = if (issue.resolved) AppColors.Green else AppColors.TextHint,
        )

        issue.cost?.let {
            Text(
                text = "Cost: R${"%.2f".format(it)}",
                fontSize = 11.sp,
                color = AppColors.FontBlackSoft,
            )
        }
    }
}


