package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectFileRepository : JpaRepository<ProjectFile, Long> {

    fun findByProjectSnAndUseYnOrderByProjectFileSnAsc(projectSn: Long, useYn: String): List<ProjectFile>
}
