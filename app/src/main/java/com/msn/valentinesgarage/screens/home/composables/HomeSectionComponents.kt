package com.msn.valentinesgarage.screens.home.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bell

@Composable
fun HomeHeaderBanner(
    @DrawableRes imageRes: Int,
    @DrawableRes profileImageRes: Int,
    greetingText: String,
    profileName: String,
    modifier: Modifier = Modifier,
    notificationCount: Int = 0,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp,
                ),
            ),
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Home header image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Overlay filter to make the image darker and improve visual hierarchy.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Orange.copy(alpha = 0.1f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            ProfileGreetingCard(
                profileImageRes = profileImageRes,
                greetingText = greetingText,
                profileName = profileName,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp),
            )
        }
    }
}

@Composable
fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(AppColors.White.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(AppColors.Orange),
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Bell,
                contentDescription = "Notifications",
                tint = AppColors.White,
                modifier = Modifier.size(12.dp),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$count notifications",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.FontBlackStrong,
        )
    }
}

@Composable
fun ProfileGreetingCard(
    @DrawableRes profileImageRes: Int,
    greetingText: String,
    profileName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.White.copy(alpha = 0.8f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = profileImageRes),
            contentDescription = "Profile image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = greetingText,
                fontSize = 11.sp,
                color = AppColors.TextHint,
            )
            Text(
                text = profileName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.FontBlackStrong,
            )
        }
    }
}

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        fontSize = 16.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.FontBlackStrong,
    )
}
