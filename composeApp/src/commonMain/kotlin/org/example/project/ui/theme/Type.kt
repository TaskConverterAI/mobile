package org.example.project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

import taskconvertaiapp.composeapp.generated.resources.Res
import taskconvertaiapp.composeapp.generated.resources.inter_bold
import taskconvertaiapp.composeapp.generated.resources.inter_medium
import taskconvertaiapp.composeapp.generated.resources.inter_regular

@Composable
fun provideInterFontFamily(): FontFamily {
    val regularFont = Font(Res.font.inter_regular, FontWeight.Normal)
    val mediumFont = Font(Res.font.inter_medium, FontWeight.Medium)
    val boldFont = Font(Res.font.inter_bold, FontWeight.Bold)

    return remember(regularFont, mediumFont, boldFont) {
        FontFamily(regularFont, mediumFont, boldFont)
    }
}

@Composable
fun provideTypography(): Typography {
    val inter = provideInterFontFamily()
    return remember(inter) {
        Typography(
            // 2XL
            displayLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
            displayMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 30.sp),
            displaySmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 24.sp, lineHeight = 30.sp),

            // LG
            headlineLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
            headlineMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
            headlineSmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 24.sp),

            // BASE
            bodyLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
            bodyMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
            bodySmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),

            // XS
            labelLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
            labelMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
            labelSmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
        )
    }
}

//val Inter = FontFamily(
//    Font(R.font.inter_regular, FontWeight.Normal),
//    Font(R.font.inter_medium, FontWeight.Medium),
//    Font(R.font.inter_bold, FontWeight.Bold)
//)

///**
// * Custom Typography with all sizes and 3 weight variations each
// *
// * Sizes: 2XL, LG, BASE, XS
// */
//val Typography = Typography(
//    // 2XL
//    displayLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
//    displayMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 30.sp),
//    displaySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 24.sp, lineHeight = 30.sp),
//
//    // LG
//    headlineLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
//    headlineMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
//    headlineSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 24.sp),
//
//    // BASE
//    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
//    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
//    bodySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
//
//    // XS
//    labelLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
//    labelMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
//    labelSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
//)
