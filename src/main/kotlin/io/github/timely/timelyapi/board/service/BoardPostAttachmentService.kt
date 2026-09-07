package io.github.timely.timelyapi.board.service

import io.github.timely.timelyapi.board.model.BoardPostFile
import io.github.timely.timelyapi.board.repository.BoardPostFileRepository
import io.github.timely.timelyapi.board.repository.BoardPostRepository
import io.github.timely.timelyapi.common.file.FileUploadDto
import io.github.timely.timelyapi.common.file.LocalFileStorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class BoardPostAttachmentService(
    private val boardPostRepository: BoardPostRepository,
    private val boardPostFileRepository: BoardPostFileRepository,
    private val fileStorageService: LocalFileStorageService
) {
    @Transactional
    fun upload(userSn: Long, companySn: Long, boardPostSn: Long, file: MultipartFile): FileUploadDto.Response {
        val post = boardPostRepository.findByBoardPostSnAndCompanySnAndUseYn(boardPostSn, companySn, "Y")
            ?: throw IllegalArgumentException("Board post not found")
        require(post.authorUserSn == userSn) { "Only the post author can upload attachments" }

        val stored = fileStorageService.store("board-posts/$boardPostSn", file)
        val metadata = try {
            boardPostFileRepository.saveAndFlush(BoardPostFile(
                boardPostSn = boardPostSn,
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
            fileSn = metadata.boardPostFileSn!!,
            originalFileName = metadata.originalFileNm,
            storedPath = metadata.storedFilePath,
            fileSize = metadata.fileSize,
            contentType = metadata.contentType
        )
    }
}
