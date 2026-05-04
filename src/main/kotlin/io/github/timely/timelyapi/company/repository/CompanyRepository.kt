package io.github.timely.timelyapi.company.repository

import io.github.timely.timelyapi.company.model.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {

    @Query(
        """
        select c
        from Company c
        where c.useYn = 'Y'
          and (:keyword is null or lower(c.companyNm) like lower(concat('%', :keyword, '%')))
        order by c.companyNm asc
        """
    )
    fun searchActiveCompanies(@Param("keyword") keyword: String?): List<Company>
}
