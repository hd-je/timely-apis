package io.github.timely.timelyapi.project.repository

import io.github.timely.timelyapi.project.model.ProjectUpdateFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectUpdateFileRepository : JpaRepository<ProjectUpdateFile, Long> {

    fun findByProjectUpdateSnAndUseYnOrderByProjectUpdateFileSnAsc(
        projectUpdateSn: Long,
        useYn: String
    ): List<ProjectUpdateFile>
}
