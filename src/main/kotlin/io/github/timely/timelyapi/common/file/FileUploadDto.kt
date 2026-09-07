package io.github.timely.timelyapi.common.file

import io.swagger.v3.oas.annotations.media.Schema

class FileUploadDto {
    @Schema(name = "FileUploadResponse", description = "업로드 파일 메타데이터 응답")
    data class Response(
        @field:Schema(description = "첨부파일 일련번호. 프로필 이미지는 null", example = "1")
        val fileSn: Long?,
        @field:Schema(description = "원본 파일명", example = "document.pdf")
        val originalFileName: String,
        @field:Schema(description = "저장 상대 경로", example = "board-posts/1/uuid.pdf")
        val storedPath: String,
        @field:Schema(description = "파일 크기(byte)", example = "1024")
        val fileSize: Long,
        @field:Schema(description = "Content-Type", example = "application/pdf")
        val contentType: String?
    )
}
