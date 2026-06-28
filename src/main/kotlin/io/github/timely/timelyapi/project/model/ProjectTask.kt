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
@Table(name = "tb_project_task")
class ProjectTask(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_task_sn")
    val projectTaskSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "project_sn", nullable = false)
    var projectSn: Long,

    @Column(name = "task_nm", nullable = false, length = 200)
    var taskNm: String,

    @Column(name = "description", columnDefinition = "text")
    var description: String? = null,

    @Column(name = "assignee_user_sn")
    var assigneeUserSn: Long? = null,

    @Column(name = "status", nullable = false, length = 30)
    var status: String = "PENDING",

    @Column(name = "priority", nullable = false, length = 30)
    var priority: String = "MEDIUM",

    @Column(name = "sort_seq", nullable = false)
    var sortSeq: Int = 0,

    @Column(name = "due_dt")
    var dueDt: LocalDate? = null,

    @Column(name = "complete_dt")
    var completeDt: LocalDateTime? = null,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
