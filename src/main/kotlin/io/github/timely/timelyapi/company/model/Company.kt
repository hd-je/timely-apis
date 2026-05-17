package io.github.timely.timelyapi.company.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_company")
class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_sn")
    val companySn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "company_nm", nullable = false, length = 200)
    var companyNm: String,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
