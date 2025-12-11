package org.example.project.ui.screens.notesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.mapview.MapView
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider
import org.example.project.AppDependencies
import org.example.project.data.geo.GeoTagPreset
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@Composable
actual fun MapPickerScreen(
    onPicked: (lat: Double, lon: Double, name: String?, colorLong: Long?) -> Unit,
    onBack: () -> Unit
) {
    val geoRepo = remember { AppDependencies.container.geoTagRepository }
    var selectedPoint by remember { mutableStateOf<Point?>(null) }
    var tagName by remember { mutableStateOf("") }
    var tagColor by remember { mutableStateOf(Color.Blue) }
    var placemark by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    var mapObjectCollection: MapObjectCollection? by remember { mutableStateOf(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        AndroidView(
            factory = { ctx ->
                val mapView = MapView(ctx)
                val map = mapView.map
                mapObjectCollection = map.mapObjects
                map.move(CameraPosition(Point(55.751244, 37.618423), 12f, 0f, 0f))

                map.addInputListener(object : InputListener {
                    override fun onMapTap(map: com.yandex.mapkit.map.Map, point: Point) {
                        selectedPoint = point
                        placemark?.let { mapObjectCollection?.remove(it) }
                        placemark = mapObjectCollection?.addPlacemark(
                            point,
                            ImageProvider.fromResource(ctx, android.R.drawable.star_on),
                            IconStyle()
                        )
                        map.move(CameraPosition(point, 14f, 0f, 0f))
                    }

                    override fun onMapLongTap(map: com.yandex.mapkit.map.Map, point: Point) {}
                })
                mapView
            },
            modifier = Modifier.weight(1f)
        )

        HorizontalDivider()

        OutlinedTextField(
            value = tagName,
            onValueChange = { tagName = it },
            label = { Text("Имя тега") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val colors = listOf(
                Color.Red to "Красный",
                Color.Green to "Зелёный",
                Color.Blue to "Синий",
                Color.Magenta to "Пурпурный",
                Color.Cyan to "Голубой",
                Color.Yellow to "Жёлтый"
            )
            items(colors) { (c, name) ->
                val isSelected = tagColor == c
                AssistChip(
                    onClick = { tagColor = c },
                    label = {
                        Text(
                            name,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) c.copy(alpha = 0.45f) else c.copy(alpha = 0.18f),
                        labelColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                    ),
                    border = if (isSelected) null else BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Выбран:", style = MaterialTheme.typography.bodyMedium)
                    Box(
                        Modifier
                            .size(18.dp)
                            .background(
                                tagColor,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(onClick = onBack) { Text("Назад") }
            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    val lat = selectedPoint?.latitude
                    val lon = selectedPoint?.longitude
                    if (lat != null && lon != null && tagName.isNotBlank()) {
                        val preset = GeoTagPreset(
                            name = tagName,
                            latitude = lat,
                            longitude = lon,
                            colorValueLong = tagColor.value.toLong()
                        )
                        scope.launch {
                            geoRepo.addPreset(preset)
                            onPicked(lat, lon, tagName, tagColor.value.toLong())
                        }
                    }
                },
                enabled = selectedPoint != null && tagName.isNotBlank()
            ) { Text("Создать тег и выбрать") }
        }
    }
}
