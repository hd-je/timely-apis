package io.github.timely.timelyapi.project.service

import io.github.timely.timelyapi.common.file.FileUploadDto
import io.github.timely.timelyapi.common.file.LocalFileStorageService
import io.github.timely.timelyapi.project.model.ProjectUpdateFile
import io.github.timely.timelyapi.project.repository.ProjectUpdateFileRepository
import io.github.timely.timelyapi.project.repository.ProjectUpdateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ProjectUpdateAttachmentService(
    private val projectService: ProjectService,
    private val projectUpdateRepository: ProjectUpdateRepository,
    private val projectUpdateFileRepository: ProjectUpdateFileRepository,
    private val fileStorageService: LocalFileStorageService
) {
    @Transactional
    fun upload(
        userSn: Long,
        companySn: Long,
        projectSn: Long,
        projectUpdateSn: Long,
        file: MultipartFile
    ): FileUploadDto.Response {
        projectService.getActiveProject(companySn, projectSn)
        val update = projectUpdateRepository.findByProjectUpdateSnAndProjectSnAndUseYn(projectUpdateSn, projectSn, "Y")
            ?: throw IllegalArgumentException("Project update not found")
        require(update.authorUserSn == userSn) { "Only the update author can upload attachments" }

        val stored = fileStorageService.store("project-updates/$projectUpdateSn", file)
        val metadata = try {
            projectUpdateFileRepository.saveAndFlush(ProjectUpdateFile(
                projectUpdateSn = projectUpdateSn,
                uploadUserSn = userSn,
                originalFileNm = stored.originalFileName,
                storedFilePath = stored.storedPath,
                fileSize = stored.fileSize,
                contentType = stored.contentType
            ))
        } catch (exception: RuntimeException) {
            runCatching { fileStorageService.deleteStoredFile(stored.storedPath) }
                .onFailure(exception::addSuppressed)
            throw exception
        }
        return FileUploadDto.Response(
            fileSn = metadata.projectUpdateFileSn!!,
            originalFileName = metadata.originalFileNm,
            storedPath = metadata.storedFilePath,
            fileSize = metadata.fileSize,
            contentType = metadata.contentType
        )
    }
}
