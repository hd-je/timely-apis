package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectAccessUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectAccessUserRepository : JpaRepository<ProjectAccessUser, Long> {

    fun findByProjectSnAndUseYnOrderByProjectAccessUserSnAsc(projectSn: Long, useYn: String): List<ProjectAccessUser>

    fun findByProjectSnAndUserSn(projectSn: Long, userSn: Long): ProjectAccessUser?

    fun countByProjectSnAndUseYn(projectSn: Long, useYn: String): Long
}
