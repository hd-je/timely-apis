package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {

    fun findByProjectSnAndCompanySnAndUseYn(projectSn: Long, companySn: Long, useYn: String): Project?

    fun countByCompanySnAndUseYn(companySn: Long, useYn: String): Long

    fun countByCompanySnAndStatusAndUseYn(companySn: Long, status: String, useYn: String): Long

    @Query(
        """
        select p
        from Project p
        where p.useYn = 'Y'
          and p.companySn = :companySn
          and (:status is null or p.status = :status)
          and (
              :keyword is null
              or lower(p.projectNm) like lower(concat('%', :keyword, '%'))
              or lower(p.description) like lower(concat('%', :keyword, '%'))
          )
        """
    )
    fun searchActiveProjects(
        @Param("companySn") companySn: Long,
        @Param("status") status: String?,
        @Param("keyword") keyword: String?,
        pageable: Pageable
    ): Page<Project>

    @Query(
        """
        select distinct p
        from Project p
        left join ProjectMember m
          on m.projectSn = p.projectSn
         and m.userSn = :userSn
         and m.useYn = 'Y'
        where p.useYn = 'Y'
          and p.companySn = :companySn
          and (p.ownerUserSn = :userSn or m.projectMemberSn is not null)
          and (:status is null or p.status = :status)
          and (
              :keyword is null
              or lower(p.projectNm) like lower(concat('%', :keyword, '%'))
              or lower(p.description) like lower(concat('%', :keyword, '%'))
          )
        """
    )
    fun searchAssignedProjects(
        @Param("companySn") companySn: Long,
        @Param("userSn") userSn: Long,
        @Param("status") status: String?,
        @Param("keyword") keyword: String?,
        pageable: Pageable
    ): Page<Project>
}
