package io.github.timely.timelyapi.project.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_project_update")
class ProjectUpdate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_update_sn")
    val projectUpdateSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "project_sn", nullable = false)
    var projectSn: Long,

    @Column(name = "author_user_sn", nullable = false)
    var authorUserSn: Long,

    @Column(name = "project_task_sn")
    var projectTaskSn: Long? = null,

    @Column(name = "update_type", nullable = false, length = 30)
    var updateType: String,

    @Column(name = "title", nullable = false, length = 200)
    var title: String,

    @Column(name = "content", columnDefinition = "text")
    var content: String? = null,

    @Column(name = "auto_yn", nullable = false, columnDefinition = "char(1)")
    var autoYn: String = "N",

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
