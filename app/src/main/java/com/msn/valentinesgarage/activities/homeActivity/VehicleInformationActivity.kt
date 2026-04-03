package com.msn.valentinesgarage.activities.homeActivity

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.activities.homeActivity.composables.InformationCard
import com.msn.valentinesgarage.activities.homeActivity.composables.IssueTaskCard
import com.msn.valentinesgarage.activities.homeActivity.composables.MechanicCard
import com.msn.valentinesgarage.activities.homeActivity.composables.SectionHeaderRow
import com.msn.valentinesgarage.theme.AppColors

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
    val priority: String,
    val isOpen: Boolean,
)

@Composable
fun VehicleInformationActivity(
    modifier: Modifier = Modifier,
) {
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
                title = "Pending Issues and Tasks",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        items(items = sampleIssues, key = { it.id }) { issue ->
            IssueTaskCard(
                title = issue.title,
                subtitle = issue.subtitle,
                priority = issue.priority,
                isOpen = issue.isOpen,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
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
        priority = "High priority",
        isOpen = true,
    ),
    IssueUi(
        id = "2",
        title = "Rear brake pad wear",
        subtitle = "Parts available. Schedule replacement this week.",
        priority = "Medium priority",
        isOpen = true,
    ),
    IssueUi(
        id = "3",
        title = "Cabin filter replaced",
        subtitle = "Completed during last preventive maintenance.",
        priority = "Maintenance",
        isOpen = false,
    ),
)

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun VehicleInformationActivityPreview() {
    VehicleInformationActivity()
}
