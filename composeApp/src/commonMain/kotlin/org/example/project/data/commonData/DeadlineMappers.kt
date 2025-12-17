package org.example.project.data.commonData

import org.example.project.data.network.models.DeadlineDto
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Deadline.toDeadlineDto() : DeadlineDto {
    return  DeadlineDto(
        time = Instant.fromEpochMilliseconds(time).toString(),
        remindByTime =  remindByTime
    )
}

@OptIn(ExperimentalTime::class)
fun DeadlineDto.toDeadline() : Deadline {
    return  Deadline(
        time = Instant.parse(time).toEpochMilliseconds(),
        remindByTime =  remindByTime
    )
}
