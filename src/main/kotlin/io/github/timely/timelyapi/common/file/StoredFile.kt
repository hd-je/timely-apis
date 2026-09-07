package io.github.timely.timelyapi.common.file

data class StoredFile(
    val originalFileName: String,
    val storedPath: String,
    val fileSize: Long,
    val contentType: String?
)
