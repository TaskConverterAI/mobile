package org.example.project.data.commonData

import org.example.project.data.network.models.LocationDto

fun Location.toLocationDto(): LocationDto {
    return LocationDto(
        latitude = latitude,
        longitude = longitude,
        name = name,
        remindByLocation = remindByLocation
    )
}

fun LocationDto.toLocation() : Location {
    return Location(
        latitude = latitude,
        longitude = longitude,
        name = name,
        remindByLocation = remindByLocation
    )
}