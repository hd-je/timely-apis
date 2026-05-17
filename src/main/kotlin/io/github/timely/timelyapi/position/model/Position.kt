package io.github.timely.timelyapi.position.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_position")
class Position(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_sn")
    val positionSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "company_sn", nullable = false)
    var companySn: Long,

    @Column(name = "position_cd", nullable = false, length = 50)
    var positionCd: String,

    @Column(name = "position_nm", nullable = false, length = 100)
    var positionNm: String,

    @Column(name = "sort_ord", nullable = false)
    var sortOrd: Int = 0,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
