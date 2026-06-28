package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectMemberRepository : JpaRepository<ProjectMember, Long> {

    fun findByProjectSnAndUseYnOrderByProjectMemberSnAsc(projectSn: Long, useYn: String): List<ProjectMember>

    fun findByProjectSnAndUserSn(projectSn: Long, userSn: Long): ProjectMember?
}
