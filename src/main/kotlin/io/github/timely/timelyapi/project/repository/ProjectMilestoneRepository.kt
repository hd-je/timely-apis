package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectMilestone
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectMilestoneRepository : JpaRepository<ProjectMilestone, Long> {

    fun findByProjectSnAndUseYnOrderBySortSeqAscDueDtAscProjectMilestoneSnAsc(
        projectSn: Long,
        useYn: String
    ): List<ProjectMilestone>

    fun findByProjectMilestoneSnAndProjectSnAndUseYn(
        projectMilestoneSn: Long,
        projectSn: Long,
        useYn: String
    ): ProjectMilestone?

    fun countByProjectSnAndUseYn(projectSn: Long, useYn: String): Long

    fun countByProjectSnAndStatusAndUseYn(projectSn: Long, status: String, useYn: String): Long
}
