package io.github.timely.timelyapi.project.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_project_member")
class ProjectMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_sn")
    val projectMemberSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "project_sn", nullable = false)
    var projectSn: Long,

    @Column(name = "user_sn", nullable = false)
    var userSn: Long,

    @Column(name = "member_role", nullable = false, length = 30)
    var memberRole: String,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
