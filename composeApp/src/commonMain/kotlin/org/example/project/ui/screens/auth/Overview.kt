package org.example.project.ui.screens.auth

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

import org.example.project.ui.theme.TaskConvertAIAppTheme
import taskconvertaiapp.composeapp.generated.resources.Res
import taskconvertaiapp.composeapp.generated.resources.*

data class Slide(
    val imageRes: DrawableResource,
    val textRes: StringResource
)

@Composable
fun OverviewScreen(
    onCompleteOverviewButtonClicked: () -> Unit
) {
    var hasResourceError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val slides = try {
        listOf(
            Slide(Res.drawable.overview_img_1, Res.string.overview_txt_1),
            Slide(Res.drawable.overview_img_2, Res.string.overview_txt_2),
            Slide(Res.drawable.overview_img_3, Res.string.overview_txt_3)
        )
    } catch (e: Exception) {
        hasResourceError = true
        errorMessage = "Failed to load slides: ${e.message}"
        emptyList()
    }

    val buttonTexts = try {
        listOf(
            Res.string.next_btn_1,
            Res.string.next_btn_2,
            Res.string.next_btn_3
        )
    } catch (e: Exception) {
        hasResourceError = true
        errorMessage = "Failed to load button texts: ${e.message}"
        emptyList()
    }

    var currentSlide by remember { mutableStateOf(0) }

    // Показываем экран ошибки, если ресурсы не загрузились
    if (hasResourceError || slides.isEmpty()) {
        ErrorScreen(errorMessage = errorMessage)
        return
    }

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
                val imageResult = runCatching { painterResource(slide.imageRes) }

                if (imageResult.isSuccess) {
                    Image(
                        painter = imageResult.getOrThrow(),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 40.dp)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Изображение не загружено: ${imageResult.exceptionOrNull()?.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                val textResult = runCatching { stringResource(slide.textRes) }

                if (textResult.isSuccess) {
                    Text(
                        text = textResult.getOrThrow(),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                } else {
                    Text(
                        text = "Текст не загружен: ${textResult.exceptionOrNull()?.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewButtons(
    currentSlide: Int,
    totalSlides: Int,
    buttonTexts: List<StringResource>,
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
    btnText: StringResource,
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
                text = stringResource(btnText),
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
                    text = stringResource(Res.string.back_btn),
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
fun ErrorScreen(errorMessage: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ошибка загрузки ресурсов",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = errorMessage.ifEmpty { "Не удалось загрузить необходимые ресурсы" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OverviewPreview() {
    TaskConvertAIAppTheme {
        OverviewScreen(){}
    }
}
