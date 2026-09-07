package io.github.timely.timelyapi.common.file

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class LocalFileStorageService(
    @Value("\${timely.file.storage-root:./uploads}") storageRoot: String
) {
    private val root: Path = Paths.get(storageRoot).toAbsolutePath().normalize()

    fun store(directory: String, file: MultipartFile): StoredFile {
        require(!file.isEmpty) { "File must not be empty" }
        require(file.size <= MAX_FILE_SIZE) { "File size must not exceed 3MB" }

        val originalFileName = (file.originalFilename ?: "file")
            .substringAfterLast('/')
            .substringAfterLast('\\')
            .trim()
            .takeIf { it.isNotBlank() } ?: "file"
        val safeDirectory = root.resolve(directory).normalize()
        require(safeDirectory.startsWith(root)) { "Invalid storage directory" }

        Files.createDirectories(safeDirectory)
        val storedName = UUID.randomUUID().toString() + safeExtension(originalFileName)
        val target = safeDirectory.resolve(storedName).normalize()
        require(target.startsWith(safeDirectory)) { "Invalid file path" }
        file.inputStream.use { Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING) }

        return StoredFile(
            originalFileName = originalFileName,
            storedPath = root.relativize(target).toString().replace('\\', '/'),
            fileSize = file.size,
            contentType = file.contentType?.takeIf { it.isNotBlank() }
        )
    }

    fun deleteStoredFile(storedPath: String) {
        val target = root.resolve(storedPath).normalize()
        require(target.startsWith(root) && target != root) { "Invalid stored file path" }
        Files.deleteIfExists(target)
    }

    private fun safeExtension(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "")
        return if (extension.isNotBlank() && extension.length <= 10 && extension.all { it.isLetterOrDigit() }) {
            ".${extension.lowercase()}"
        } else {
            ""
        }
    }

    companion object {
        const val MAX_FILE_SIZE = 3L * 1024 * 1024
    }
}
