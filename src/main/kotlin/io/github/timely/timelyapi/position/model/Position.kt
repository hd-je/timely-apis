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
    @Column(name = "POSITION_SN")
    val positionSn: Long? = null,

    @Column(name = "CREATE_DT", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "UPDATE_DT", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "COMPANY_SN", nullable = false)
    var companySn: Long,

    @Column(name = "POSITION_CD", nullable = false, length = 50)
    var positionCd: String,

    @Column(name = "POSITION_NM", nullable = false, length = 100)
    var positionNm: String,

    @Column(name = "SORT_ORD", nullable = false)
    var sortOrd: Int = 0,

    @Column(name = "USE_YN", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
