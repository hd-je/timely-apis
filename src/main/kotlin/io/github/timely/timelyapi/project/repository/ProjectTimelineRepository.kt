package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectTimeline
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectTimelineRepository : JpaRepository<ProjectTimeline, Long> {

    fun findByProjectSnAndUseYnOrderBySortSeqAscStartDtAscProjectTimelineSnAsc(
        projectSn: Long,
        useYn: String
    ): List<ProjectTimeline>

    fun findByProjectTimelineSnAndProjectSnAndUseYn(
        projectTimelineSn: Long,
        projectSn: Long,
        useYn: String
    ): ProjectTimeline?

    fun countByProjectSnAndUseYn(projectSn: Long, useYn: String): Long

    fun countByProjectSnAndStatusAndUseYn(projectSn: Long, status: String, useYn: String): Long
}
