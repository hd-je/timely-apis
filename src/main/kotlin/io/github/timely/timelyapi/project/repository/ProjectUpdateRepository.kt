package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectUpdate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectUpdateRepository : JpaRepository<ProjectUpdate, Long> {

    fun findByProjectSnAndUseYnOrderByCreateDtDescProjectUpdateSnDesc(projectSn: Long, useYn: String): List<ProjectUpdate>

    fun findByProjectUpdateSnAndProjectSnAndUseYn(projectUpdateSn: Long, projectSn: Long, useYn: String): ProjectUpdate?

    fun countByProjectSnAndUseYn(projectSn: Long, useYn: String): Long

    fun countByProjectSnAndUpdateTypeAndUseYn(projectSn: Long, updateType: String, useYn: String): Long
}
