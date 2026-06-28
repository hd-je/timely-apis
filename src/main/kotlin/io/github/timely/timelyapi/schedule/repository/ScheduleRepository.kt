package io.github.timely.timelyapi.schedule.repository

import io.github.timely.timelyapi.schedule.model.Schedule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ScheduleRepository : JpaRepository<Schedule, Long> {

    fun findByScheduleSnAndCompanySnAndUseYn(scheduleSn: Long, companySn: Long, useYn: String): Schedule?

    @Query(
        """
        select distinct s
        from Schedule s
        left join ScheduleParticipant p
          on p.scheduleSn = s.scheduleSn
         and p.useYn = 'Y'
        where s.useYn = 'Y'
          and s.companySn = :companySn
          and s.startDt <= :endDt
          and s.endDt >= :startDt
          and (:projectSn is null or s.projectSn = :projectSn)
          and (:userSn is null or s.ownerUserSn = :userSn or p.userSn = :userSn)
          and (:scheduleType is null or s.scheduleType = :scheduleType)
          and (:status is null or s.status = :status)
        """
    )
    fun searchActiveSchedules(
        @Param("companySn") companySn: Long,
        @Param("startDt") startDt: LocalDateTime,
        @Param("endDt") endDt: LocalDateTime,
        @Param("projectSn") projectSn: Long?,
        @Param("userSn") userSn: Long?,
        @Param("scheduleType") scheduleType: String?,
        @Param("status") status: String?,
        pageable: Pageable
    ): Page<Schedule>

    @Query(
        """
        select distinct s
        from Schedule s
        where s.useYn = 'Y'
          and s.companySn = :companySn
          and s.startDt <= :endDt
          and s.endDt >= :startDt
          and (:projectSn is null or s.projectSn = :projectSn)
          and (:scheduleType is null or s.scheduleType = :scheduleType)
          and (:status is null or s.status = :status)
          and (
              exists (
                  select 1
                  from TimelyUser ownerUser
                  where ownerUser.userSn = s.ownerUserSn
                    and ownerUser.companySn = s.companySn
                    and ownerUser.deptSn = :deptSn
                    and ownerUser.userStatus = 'ACTIVE'
                    and ownerUser.useYn = 'Y'
              )
              or exists (
                  select 1
                  from ScheduleParticipant p
                  join TimelyUser participantUser
                    on participantUser.userSn = p.userSn
                   and participantUser.companySn = p.companySn
                  where p.scheduleSn = s.scheduleSn
                    and p.companySn = s.companySn
                    and p.useYn = 'Y'
                    and participantUser.deptSn = :deptSn
                    and participantUser.userStatus = 'ACTIVE'
                    and participantUser.useYn = 'Y'
              )
          )
        """
    )
    fun searchActiveTeamSchedules(
        @Param("companySn") companySn: Long,
        @Param("deptSn") deptSn: Long,
        @Param("startDt") startDt: LocalDateTime,
        @Param("endDt") endDt: LocalDateTime,
        @Param("projectSn") projectSn: Long?,
        @Param("scheduleType") scheduleType: String?,
        @Param("status") status: String?,
        pageable: Pageable
    ): Page<Schedule>
}
