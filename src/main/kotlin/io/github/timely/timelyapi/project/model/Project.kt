package io.github.timely.timelyapi.project.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "tb_project")
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_sn")
    val projectSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "company_sn", nullable = false)
    var companySn: Long,

    @Column(name = "project_nm", nullable = false, length = 200)
    var projectNm: String,

    @Column(name = "description", columnDefinition = "text")
    var description: String? = null,

    @Column(name = "owner_user_sn", nullable = false)
    var ownerUserSn: Long,

    @Column(name = "status", nullable = false, length = 30)
    var status: String,

    @Column(name = "progress_rate", nullable = false)
    var progressRate: Int = 0,

    @Column(name = "start_dt")
    var startDt: LocalDate? = null,

    @Column(name = "end_dt")
    var endDt: LocalDate? = null,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
