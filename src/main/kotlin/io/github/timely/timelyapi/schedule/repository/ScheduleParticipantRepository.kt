package io.github.timely.timelyapi.schedule.repository

import io.github.timely.timelyapi.schedule.model.ScheduleParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScheduleParticipantRepository : JpaRepository<ScheduleParticipant, Long> {

    fun findByScheduleSnAndUseYnOrderByScheduleParticipantSnAsc(
        scheduleSn: Long,
        useYn: String
    ): List<ScheduleParticipant>

    fun findByScheduleSnOrderByScheduleParticipantSnAsc(scheduleSn: Long): List<ScheduleParticipant>

    fun findByScheduleSnInAndUseYnOrderByScheduleParticipantSnAsc(
        scheduleSns: Collection<Long>,
        useYn: String
    ): List<ScheduleParticipant>
}
