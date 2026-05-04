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
    @Column(name = "DEPT_SN")
    val deptSn: Long? = null,

    @Column(name = "CREATE_DT", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "UPDATE_DT", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "COMPANY_SN")
    var companySn: Long? = null,

    @Column(name = "DEPT_NM", nullable = false, length = 100)
    var deptNm: String,

    @Column(name = "USE_YN", nullable = false, columnDefinition = "char(1)")
    var useYn: String
)
