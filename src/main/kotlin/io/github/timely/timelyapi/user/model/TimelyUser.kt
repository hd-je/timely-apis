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
    @Column(name = "USER_SN")
    val userSn: Long? = null,

    @Column(name = "CREATE_DT", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "UPDATE_DT", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "COMPANY_SN", nullable = false)
    var companySn: Long,

    @Column(name = "DEPT_SN", nullable = false)
    var deptSn: Long,

    @Column(name = "LOGIN_ID", nullable = false, unique = true, length = 50)
    var loginId: String,

    @Column(name = "PASSWORD_HASH", nullable = false)
    var passwordHash: String,

    @Column(name = "USER_NM", nullable = false, length = 100)
    var userNm: String,

    @Column(name = "POSITION", nullable = false, length = 30)
    var position: String,

    @Column(name = "PHONE_NO", nullable = false, length = 30)
    var phoneNo: String,

    @Column(name = "EMAIL")
    var email: String? = null,

    @Column(name = "AVATAR_URL", length = 500)
    var avatarUrl: String? = null,

    @Column(name = "USER_STATUS", nullable = false, length = 30)
    var userStatus: String,

    @Column(name = "USE_YN", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
