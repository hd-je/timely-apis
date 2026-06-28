package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectAccessRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectAccessRoleRepository : JpaRepository<ProjectAccessRole, Long> {

    fun findByProjectSnAndUseYnOrderByProjectAccessRoleSnAsc(projectSn: Long, useYn: String): List<ProjectAccessRole>

    fun findByProjectSnAndAuthorityCd(projectSn: Long, authorityCd: String): ProjectAccessRole?

    fun countByProjectSnAndUseYn(projectSn: Long, useYn: String): Long
}
