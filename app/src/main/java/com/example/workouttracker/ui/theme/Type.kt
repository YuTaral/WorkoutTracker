package com.example.workouttracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 31.sp,
        letterSpacing = 0.sp,
        color = ColorWhite
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        color = ColorWhite
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = ColorWhite
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = labelLargeSize,
        lineHeight = labelLargeLineHeight,
        letterSpacing = labelLargeLetterSpacing,
        color = ColorWhite
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = labelMediumSize,
        lineHeight = labelMediumLineHeight,
        letterSpacing = labelMediumLetterSpacing,
        color = ColorWhite
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp,
        color = ColorWhite
    )
)

val LabelMediumGrey = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = labelMediumSize,
    lineHeight = labelMediumLineHeight,
    letterSpacing = labelMediumLetterSpacing,
    color = ColorSecondary
)

val labelLargeBold = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = labelLargeSize,
    lineHeight = labelLargeLineHeight,
    letterSpacing = labelLargeLetterSpacing,
    color = ColorWhite
)

val labelMediumOrange= TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = labelMediumSize,
    lineHeight = labelMediumLineHeight,
    letterSpacing = labelMediumLetterSpacing,
    color = ColorOrange
)

val labelMediumGreen = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = labelMediumSize,
    lineHeight = labelMediumLineHeight,
    letterSpacing = 0.5.sp,
    color = ColorGreen
)

val LabelNavItem = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.5.sp,
    color = ColorWhite
)