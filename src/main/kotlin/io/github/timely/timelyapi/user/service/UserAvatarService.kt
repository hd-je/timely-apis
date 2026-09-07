package io.github.timely.timelyapi.user.service

import io.github.timely.timelyapi.common.file.FileUploadDto
import io.github.timely.timelyapi.common.file.LocalFileStorageService
import io.github.timely.timelyapi.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserAvatarService(
    private val userRepository: UserRepository,
    private val fileStorageService: LocalFileStorageService
) {
    @Transactional
    fun upload(userSn: Long, companySn: Long, file: MultipartFile): FileUploadDto.Response {
        val user = userRepository.findByUserSnAndCompanySnAndUseYn(userSn, companySn, "Y")
            ?: throw IllegalArgumentException("User not found")
        require(file.contentType?.startsWith("image/") == true) { "Avatar file must be an image" }
        val stored = fileStorageService.store("avatars/$userSn", file)
        user.avatarUrl = stored.storedPath
        try {
            userRepository.saveAndFlush(user)
        } catch (exception: RuntimeException) {
            runCatching { fileStorageService.deleteStoredFile(stored.storedPath) }
                .onFailure(exception::addSuppressed)
            throw exception
        }
        return FileUploadDto.Response(
            fileSn = null,
            originalFileName = stored.originalFileName,
            storedPath = stored.storedPath,
            fileSize = stored.fileSize,
            contentType = stored.contentType
        )
    }
}
