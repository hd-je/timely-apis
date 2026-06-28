package io.github.timely.timelyapi.schedule.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_schedule")
class Schedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_sn")
    val scheduleSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "company_sn", nullable = false)
    var companySn: Long,

    @Column(name = "project_sn")
    var projectSn: Long? = null,

    @Column(name = "owner_user_sn", nullable = false)
    var ownerUserSn: Long,

    @Column(name = "title", nullable = false, length = 200)
    var title: String,

    @Column(name = "content", columnDefinition = "text")
    var content: String? = null,

    @Column(name = "schedule_type", nullable = false, length = 30)
    var scheduleType: String,

    @Column(name = "status", nullable = false, length = 30)
    var status: String,

    @Column(name = "start_dt", nullable = false)
    var startDt: LocalDateTime,

    @Column(name = "end_dt", nullable = false)
    var endDt: LocalDateTime,

    @Column(name = "all_day_yn", nullable = false, columnDefinition = "char(1)")
    var allDayYn: String = "N",

    @Column(name = "place", length = 200)
    var place: String? = null,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
