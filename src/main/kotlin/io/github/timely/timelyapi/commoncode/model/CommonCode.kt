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
    @Column(name = "common_code_sn")
    val commonCodeSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "code_group", nullable = false, length = 100)
    var codeGroup: String,

    @Column(name = "code", nullable = false, length = 100)
    var code: String,

    @Column(name = "code_nm", nullable = false, length = 200)
    var codeNm: String,

    @Column(name = "sort_seq", nullable = false)
    var sortSeq: Int,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
