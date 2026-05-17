package io.github.timely.timelyapi.department.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_department")
class Department(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_sn")
    val deptSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "company_sn")
    var companySn: Long? = null,

    @Column(name = "dept_nm", nullable = false, length = 100)
    var deptNm: String,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
