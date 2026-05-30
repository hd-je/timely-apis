package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectTagRepository : JpaRepository<ProjectTag, Long> {

    fun findByProjectSnAndUseYnOrderByProjectTagSnAsc(projectSn: Long, useYn: String): List<ProjectTag>

    fun findByProjectSnAndTagNm(projectSn: Long, tagNm: String): ProjectTag?
}
