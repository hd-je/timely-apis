package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectUpdate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ProjectUpdateRepository : JpaRepository<ProjectUpdate, Long> {

    fun findByProjectSnAndUseYnOrderByCreateDtDescProjectUpdateSnDesc(projectSn: Long, useYn: String): List<ProjectUpdate>

    fun findByProjectUpdateSnAndProjectSnAndUseYn(projectUpdateSn: Long, projectSn: Long, useYn: String): ProjectUpdate?

    fun countByProjectSnAndUseYn(projectSn: Long, useYn: String): Long

    fun countByProjectSnAndUpdateTypeAndUseYn(projectSn: Long, updateType: String, useYn: String): Long

    @Query(
        """
        select
          u.projectUpdateSn as projectUpdateSn,
          u.projectSn as projectSn,
          p.projectNm as projectNm,
          u.authorUserSn as authorUserSn,
          author.userNm as authorName,
          u.title as title,
          u.content as content,
          u.createDt as createDt
        from ProjectUpdate u
        join Project p on p.projectSn = u.projectSn
        left join TimelyUser author on author.userSn = u.authorUserSn
        where u.useYn = 'Y'
          and p.useYn = 'Y'
          and p.companySn = :companySn
          and (
            p.ownerUserSn = :userSn
            or exists (
              select 1 from ProjectMember m
              where m.projectSn = p.projectSn
                and m.userSn = :userSn
                and m.useYn = 'Y'
            )
          )
        """
    )
    fun findRecentAssignedProjectUpdates(
        @Param("companySn") companySn: Long,
        @Param("userSn") userSn: Long,
        pageable: Pageable
    ): Page<RecentProjectUpdateProjection>
}

interface RecentProjectUpdateProjection {
    val projectUpdateSn: Long
    val projectSn: Long
    val projectNm: String
    val authorUserSn: Long
    val authorName: String?
    val title: String
    val content: String?
    val createDt: LocalDateTime?
}
