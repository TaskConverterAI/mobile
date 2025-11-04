package com.example.taskconvertaiapp.shared.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.taskconvertaiapp.shared.ui.theme.TaskConvertAIAppTheme

data class Slide(
    val imageRes: Int,
    val text: String
)

@Composable
fun OverviewScreen(
    onCompleteOverviewButtonClicked: () -> Unit
) {
    val slides = listOf(
        Slide(R.drawable.overview_img_1, stringResource(id = R.string.overview_txt_1)),
        Slide(R.drawable.overview_img_2, stringResource(id = R.string.overview_txt_2)),
        Slide(R.drawable.overview_img_3, stringResource(id = R.string.overview_txt_3))
    )

    val buttonTexts = listOf(
        stringResource(id = R.string.next_btn_1),
        stringResource(id = R.string.next_btn_2),
        stringResource(id = R.string.next_btn_3)
    )

    var currentSlide by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            ) {
                OverviewContent(
                    slide = slides[currentSlide],
                    currentSlide = currentSlide,
                    totalSlides = slides.size
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                OverviewButtons(
                    currentSlide = currentSlide,
                    totalSlides = slides.size,
                    buttonTexts = buttonTexts,
                    onNextClick = {
                        if (currentSlide < slides.size - 1) {
                            currentSlide++
                        }
                        else{
                            onCompleteOverviewButtonClicked()
                        }
                    },
                    onBackClick = {
                        if (currentSlide > 0) {
                            currentSlide--
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun OverviewContent(
    slide: Slide,
    currentSlide: Int,
    totalSlides: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OverviewSlide(slide = slide)
        SlideIndicator(
            totalDots = totalSlides,
            selectedIndex = currentSlide,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
fun OverviewSlide(slide: Slide) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = slide.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
                Text(
                    text = slide.text,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
fun OverviewButtons(
    currentSlide: Int,
    totalSlides: Int,
    buttonTexts: List<String>,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val isExtraVisible = currentSlide >= 1
    val isLastSlide = currentSlide == totalSlides - 1

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MainButton(
            btnText = buttonTexts[currentSlide],
            onClick = onNextClick,
            isLastSlide = isLastSlide
        )
        ExtraButton(
            isVisible = isExtraVisible,
            onClick = onBackClick
        )
    }
}

@Composable
fun MainButton(
    btnText: String,
    onClick: () -> Unit,
    isLastSlide: Boolean
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = btnText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "next",
                modifier = Modifier.size(20.dp).align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun ExtraButton(
    isVisible: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .graphicsLayer { alpha = if (isVisible) 1f else 0f }
            .pointerInput(Unit) {}
    ) {
        if (isVisible) {
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Назад",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SlideIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isSelected) 10.dp else 8.dp)
                    .background(
                        color = if (isSelected)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun OverviewPreview() {
    TaskConvertAIAppTheme {
        OverviewScreen(){}
    }
}
