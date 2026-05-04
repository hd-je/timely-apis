package io.github.timely.timelyapi.department.repository

import io.github.timely.timelyapi.department.model.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DepartmentRepository : JpaRepository<Department, Long> {

    @Query(
        """
        select d
        from Department d
        where d.useYn = 'Y'
          and (:companySn is null or d.companySn = :companySn)
          and (:keyword is null or lower(d.deptNm) like lower(concat('%', :keyword, '%')))
        order by d.deptNm asc
        """
    )
    fun searchActiveDepartments(
        @Param("companySn") companySn: Long?,
        @Param("keyword") keyword: String?
    ): List<Department>
}
