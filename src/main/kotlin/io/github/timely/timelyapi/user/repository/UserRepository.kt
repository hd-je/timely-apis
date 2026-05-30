package io.github.timely.timelyapi.user.repository

import io.github.timely.timelyapi.user.model.TimelyUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<TimelyUser, Long> {

    fun existsByEmailIgnoreCase(email: String): Boolean

    fun findByEmailIgnoreCaseAndUseYn(email: String, useYn: String): TimelyUser?

    fun findByUserSnAndCompanySnAndUseYn(userSn: Long, companySn: Long, useYn: String): TimelyUser?

    @Query(
        """
        select u
        from TimelyUser u
        where u.useYn = 'Y'
          and (:companySn is null or u.companySn = :companySn)
          and (:deptSn is null or u.deptSn = :deptSn)
          and (:userStatus is null or u.userStatus = :userStatus)
          and (
              :keyword is null
              or lower(u.userNm) like lower(concat('%', :keyword, '%'))
              or lower(u.email) like lower(concat('%', :keyword, '%'))
          )
        order by u.userNm asc, u.userSn asc
        """
    )
    fun searchActiveUsers(
        @Param("companySn") companySn: Long?,
        @Param("deptSn") deptSn: Long?,
        @Param("userStatus") userStatus: String?,
        @Param("keyword") keyword: String?
    ): List<TimelyUser>
}
