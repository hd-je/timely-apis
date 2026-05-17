package io.github.timely.timelyapi.user.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_user")
class TimelyUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sn")
    val userSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "company_sn", nullable = false)
    var companySn: Long,

    @Column(name = "dept_sn", nullable = false)
    var deptSn: Long,

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "password", nullable = false)
    var passwordHash: String,

    @Column(name = "user_nm", nullable = false, length = 100)
    var userNm: String,

    @Column(name = "position_cd", nullable = false, length = 50)
    var position: String,

    @Column(name = "phone_no", nullable = false, length = 30)
    var phoneNo: String,

    @Column(name = "avatar_url", length = 500)
    var avatarUrl: String? = null,

    @Column(name = "user_status", nullable = false, length = 30)
    var userStatus: String,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
