package org.example.project.ui.screens.notesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import android.util.Log
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
actual fun MapPickerScreen(
    onPicked: (lat: Double, lon: Double, name: String?, colorLong: Long?) -> Unit,
    onBack: () -> Unit
) {
    Log.d("GoogleMapPicker", "Google Maps MapPickerScreen started")

    var tagName by remember { mutableStateOf("") }
    var tagColor by remember { mutableStateOf(Color.Blue) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isBottomSheetExpanded by remember { mutableStateOf(false) }

    // Moscow coordinates as default
    val defaultLocation = LatLng(55.751244, 37.618423)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    val colors = listOf(
        Color.Red to "Красный",
        Color.Green to "Зелёный",
        Color.Blue to "Синий",
        Color.Magenta to "Пурпурный",
        Color.Cyan to "Голубой",
        Color.Yellow to "Жёлтый"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Maps
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                Log.d("GoogleMapPicker", "Map clicked at: ${latLng.latitude}, ${latLng.longitude}")
                selectedLocation = latLng
                isBottomSheetExpanded = true
            }
        ) {
            // Show marker if location is selected
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Выбранная геометка",
                    snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                )
            }
        }

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    CircleShape
                )
        ) {
            Text("←", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
        }

        // Bottom panel for geotag settings
        if (isBottomSheetExpanded && selectedLocation != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Настройка геометки",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = {
                                isBottomSheetExpanded = false
                                selectedLocation = null
                                tagName = ""
                            }
                        ) {
                            Text("✕", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Coordinates display and editing
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "📍 Координаты:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = selectedLocation?.latitude?.toString() ?: "",
                                    onValueChange = { value ->
                                        value.toDoubleOrNull()?.let { lat ->
                                            selectedLocation?.let { currentLocation ->
                                                selectedLocation = LatLng(lat, currentLocation.longitude)
                                            }
                                        }
                                    },
                                    label = { Text("Широта", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = selectedLocation?.longitude?.toString() ?: "",
                                    onValueChange = { value ->
                                        value.toDoubleOrNull()?.let { lng ->
                                            selectedLocation?.let { currentLocation ->
                                                selectedLocation = LatLng(currentLocation.latitude, lng)
                                            }
                                        }
                                    },
                                    label = { Text("Долгота", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tag name input
                    OutlinedTextField(
                        value = tagName,
                        onValueChange = { tagName = it },
                        label = { Text("Название геометки") },
                        placeholder = { Text("Например: Дом, Офис, Магазин") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Цвет геометки:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Color selection
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(colors) { (color, name) ->
                            val isSelected = tagColor == color
                            AssistChip(
                                onClick = { tagColor = color },
                                label = {
                                    Text(
                                        name,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) color else color.copy(alpha = 0.2f),
                                    labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                border = if (isSelected)
                                    BorderStroke(2.dp, color.copy(alpha = 0.8f))
                                else
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.height(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                isBottomSheetExpanded = false
                                selectedLocation = null
                                tagName = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Отмена")
                        }

                        Button(
                            onClick = {
                                selectedLocation?.let { location ->
                                    onPicked(
                                        location.latitude,
                                        location.longitude,
                                        tagName.ifBlank { null },
                                        tagColor.toArgb().toLong()
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Создать геометку")
                        }
                    }
                }
            }
        }

        // Instructions overlay if no location selected
        if (!isBottomSheetExpanded) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                )
            ) {
                Text(
                    "👆 Нажмите на карту для выбора места",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
