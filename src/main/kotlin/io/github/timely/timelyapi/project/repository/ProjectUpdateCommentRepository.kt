package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectUpdateComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectUpdateCommentRepository : JpaRepository<ProjectUpdateComment, Long> {

    fun findByProjectUpdateSnAndUseYnOrderByProjectUpdateCommentSnAsc(
        projectUpdateSn: Long,
        useYn: String
    ): List<ProjectUpdateComment>

    fun findByProjectUpdateCommentSnAndProjectUpdateSnAndUseYn(
        projectUpdateCommentSn: Long,
        projectUpdateSn: Long,
        useYn: String
    ): ProjectUpdateComment?

    fun countByProjectUpdateSnAndUseYn(projectUpdateSn: Long, useYn: String): Long
}
