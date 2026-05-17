package io.github.timely.timelyapi.commoncode.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_common_code")
class CommonCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMON_CODE_SN")
    val commonCodeSn: Long? = null,

    @Column(name = "CREATE_DT", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "UPDATE_DT", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "CODE_GROUP", nullable = false, length = 100)
    var codeGroup: String,

    @Column(name = "CODE", nullable = false, length = 100)
    var code: String,

    @Column(name = "CODE_NM", nullable = false, length = 200)
    var codeNm: String,

    @Column(name = "SORT_SEQ", nullable = false)
    var sortSeq: Int,

    @Column(name = "USE_YN", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
