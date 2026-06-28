package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectTaskRepository : JpaRepository<ProjectTask, Long> {

    fun findByProjectSnAndUseYnOrderBySortSeqAscDueDtAscProjectTaskSnAsc(projectSn: Long, useYn: String): List<ProjectTask>

    fun findByProjectTaskSnAndProjectSnAndUseYn(projectTaskSn: Long, projectSn: Long, useYn: String): ProjectTask?

    fun countByProjectSnAndUseYn(projectSn: Long, useYn: String): Long

    fun countByProjectSnAndStatusAndUseYn(projectSn: Long, status: String, useYn: String): Long
}
