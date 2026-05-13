package com.msn.valentinesgarage.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.screens.home.composables.InformationCard
import com.msn.valentinesgarage.screens.home.composables.IssueTaskCard
import com.msn.valentinesgarage.screens.home.composables.MechanicCard
import com.msn.valentinesgarage.screens.home.composables.MoreIssuesCard
import com.msn.valentinesgarage.screens.home.composables.SectionHeaderRow
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars

private data class VehicleInfoField(
    val title: String,
    val value: String,
)

private data class MechanicUi(
    val id: String,
    val fullName: String,
    val imageRes: Int,
)

private data class IssueUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val severity: String,
)

private val issueImageUrls = listOf(
    "https://i.pinimg.com/1200x/f5/ff/28/f5ff28479532dafbc506ba7bcf3ff40d.jpg",
    "https://i.pinimg.com/1200x/e0/ed/58/e0ed587bf26df8ede3d77d20ed6c7b39.jpg",
)

private fun imageForIssue(issueId: String): String {
    val safeIndex = kotlin.math.abs(issueId.hashCode()) % issueImageUrls.size
    return issueImageUrls[safeIndex]
}

@Composable
fun VehicleInformationScreen(
    modifier: Modifier = Modifier,
) {
    ConfigureSystemBars(
        statusBarColor = Color.Transparent,
        darkStatusBarIcons = false,
        navigationBarColor = AppColors.White,
    )

    val severeIssues = sampleIssues.filter { it.severity == "Severe" }
    val mildIssues = sampleIssues.filter { it.severity == "Mild" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(key = "vehicle_hero") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
            ) {
                AsyncImage(
                    model = "https://i.pinimg.com/736x/57/86/d6/5786d607000a6ada2c2bc8245ce533d6.jpg",
                    contentDescription = "Vehicle image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                        .background(AppColors.White)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SectionHeaderRow(title = "Vehicle Information")

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.applogo),
                            contentDescription = "Brand logo",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape),
                        )
                        Text(
                            text = "Volvo",
                            color = AppColors.FontBlackStrong,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        item(key = "vehicle_information_grid") {
            VehicleInformationGrid(modifier = Modifier.padding(horizontal = 16.dp))
        }

        item(key = "mechanics_header") {
            SectionHeaderRow(
                title = "Mechanics Assigned For Vehicle",
                actionText = "See All",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        item(key = "mechanics_list") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(items = sampleMechanics, key = { it.id }) { mechanic ->
                    MechanicCard(
                        imageRes = mechanic.imageRes,
                        fullName = mechanic.fullName,
                    )
                }
            }
        }

        item(key = "issues_header") {
            SectionHeaderRow(
                title = "Pending Issues",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        item(key = "severe_issues_subsection") {
            IssueSeveritySubsection(
                title = "Severe",
                issues = severeIssues,
            )
        }

        item(key = "mild_issues_subsection") {
            IssueSeveritySubsection(
                title = "Mild",
                issues = mildIssues,
            )
        }
    }
}

@Composable
private fun IssueSeveritySubsection(
    title: String,
    issues: List<IssueUi>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionHeaderRow(
            title = title,
            actionText = "See more",
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items = issues.take(3), key = { it.id }) { issue ->
                IssueTaskCard(
                    imageUrl = imageForIssue(issue.id),
                    title = issue.title,
                    subtitle = issue.subtitle,
                    modifier = Modifier.width(220.dp),
                )
            }

            item {
                MoreIssuesCard(
                    remainingCount = (issues.size - 3).coerceAtLeast(0),
                    modifier = Modifier.width(220.dp),
                )
            }
        }
    }
}

@Composable
private fun VehicleInformationGrid(
    modifier: Modifier = Modifier,
) {
    val fields = listOf(
        VehicleInfoField(title = "VIN", value = "BHDCGD2323CSBHD67UHSY"),
        VehicleInfoField(title = "Year", value = "2016"),
        VehicleInfoField(title = "Brand", value = "Volvo"),
        VehicleInfoField(title = "Model", value = "FH16"),
        VehicleInfoField(title = "Category", value = "Long Haul"),
        VehicleInfoField(title = "Mileage", value = "12,024.23 Km"),
        VehicleInfoField(title = "Number Plate", value = "N2312431W"),
        VehicleInfoField(title = "Fuel Type", value = "Diesel"),
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InformationCard(
                title = fields[0].title,
                value = fields[0].value,
                modifier = Modifier.weight(2f),
            )
            InformationCard(
                title = fields[1].title,
                value = fields[1].value,
                modifier = Modifier.weight(1f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InformationCard(title = fields[2].title, value = fields[2].value, modifier = Modifier.weight(1f))
            InformationCard(title = fields[3].title, value = fields[3].value, modifier = Modifier.weight(1f))
            InformationCard(title = fields[4].title, value = fields[4].value, modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InformationCard(title = fields[5].title, value = fields[5].value, modifier = Modifier.weight(1f))
            InformationCard(title = fields[6].title, value = fields[6].value, modifier = Modifier.weight(1f))
            InformationCard(title = fields[7].title, value = fields[7].value, modifier = Modifier.weight(1f))
        }
    }
}

private val sampleMechanics = listOf(
    MechanicUi(id = "1", fullName = "Robert Mount", imageRes = R.drawable.defaultprofileicon),
    MechanicUi(id = "2", fullName = "Simon Rivers", imageRes = R.drawable.defaultprofileicon),
    MechanicUi(id = "3", fullName = "Joseph Ocean", imageRes = R.drawable.defaultprofileicon),
    MechanicUi(id = "4", fullName = "Jonas Desert", imageRes = R.drawable.defaultprofileicon),
)

private val sampleIssues = listOf(
    IssueUi(
        id = "1",
        title = "Engine warning light",
        subtitle = "Diagnostic scan required before dispatch.",
        severity = "Severe",
    ),
    IssueUi(
        id = "2",
        title = "Rear brake pad wear",
        subtitle = "Parts available. Schedule replacement this week.",
        severity = "Mild",
    ),
    IssueUi(
        id = "3",
        title = "Cabin filter replaced",
        subtitle = "Completed during last preventive maintenance.",
        severity = "Mild",
    ),
    IssueUi(
        id = "4",
        title = "Steering alignment drift",
        subtitle = "Requires calibration after suspension service.",
        severity = "Mild",
    ),
    IssueUi(
        id = "5",
        title = "Coolant level alert",
        subtitle = "Sensor reads intermittent low coolant warnings.",
        severity = "Severe",
    ),
    IssueUi(
        id = "6",
        title = "Brake pressure warning",
        subtitle = "ABS and brake line pressure need urgent checks.",
        severity = "Severe",
    ),
    IssueUi(
        id = "7",
        title = "Cabin vibration report",
        subtitle = "Driver noted mild vibration at idle speed.",
        severity = "Mild",
    ),
)

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun VehicleInformationScreenPreview() {
    VehicleInformationScreen()
}
