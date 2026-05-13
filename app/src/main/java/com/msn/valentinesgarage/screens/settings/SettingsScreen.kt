package com.msn.valentinesgarage.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.data.models.User
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.screens.settings.composables.ProfileCard
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ChevronLeft
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.ChevronDown
import compose.icons.fontawesomeicons.solid.ChevronRight

@Composable
fun SettingsScreen(
    user: User? = null,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
){
    var isDarkTheme by remember { mutableStateOf(false) }
    var selectedTextSize by remember { mutableStateOf("Medium") }
    var selectedLanguage by remember { mutableStateOf("English") }

    val textSizes = listOf("Small", "Medium", "Large")
    val languages = listOf("English")

    ConfigureSystemBars(statusBarColor = Color.White)

    LazyColumn(
        modifier = modifier
            .topSafeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(color = Color.White)
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item(key = "settings-header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Cog,
                    contentDescription = null,
                    tint = AppColors.Orange,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Settings",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.FontBlackStrong,
                    fontSize = 20.sp,
                )
            }
        }

        item {
            ProfileCard(
                profileImageRes = R.drawable.defaultprofileicon,
                fullName = user?.full_name ?: "Loading...",
                email = user?.email ?: "...",
                modifier = Modifier.fillMaxWidth(),
            )

        }

        item {
            SettingsSectionItem(title = "Appearance") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Theme", color = Color.Black, fontSize = 16.sp)
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Text Size", color = Color.Black, fontSize = 14.sp)
                    DropdownSelector(
                        options = textSizes,
                        selected = selectedTextSize,
                        onSelected = { selectedTextSize = it },
                    )
                }
            }
        }

        item {
            SettingsSectionItem(title = "Language") {
                DropdownSelector(
                    options = languages,
                    selected = selectedLanguage,
                    onSelected = { selectedLanguage = it },
                )
            }
        }

        item {
            SettingsSectionItem(title = "Support") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SupportRowItem(label = "Terms and Conditions", onClick = { })
                    SupportRowItem(label = "App Information", onClick = { })
                }
            }
        }

        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCC3333),
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Logout")
            }
        }
    }
}

@Composable
private fun SettingsSectionItem(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionLabel(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(12.dp),
        )
        {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun DropdownSelector(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = selected, color = Color.Black, fontSize = 14.sp)
            Icon(
                imageVector = FontAwesomeIcons.Solid.ChevronDown,
                contentDescription = "Open options",
                tint = Color.Black,
                modifier = Modifier.size(12.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SupportRowItem(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = Color.Black, fontSize = 14.sp)
        Icon(
            imageVector = FontAwesomeIcons.Solid.ChevronRight,
            contentDescription = "$label action",
            tint = Color.Black,
            modifier = Modifier.size(12.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun SettingsScreenPreview(){
    SettingsScreen()
}