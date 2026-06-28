package io.github.timely.timelyapi.project.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_project_file")
class ProjectFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_file_sn")
    val projectFileSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "project_sn", nullable = false)
    var projectSn: Long,

    @Column(name = "upload_user_sn", nullable = false)
    var uploadUserSn: Long,

    @Column(name = "original_file_nm", nullable = false, length = 255)
    var originalFileNm: String,

    @Column(name = "stored_file_path", nullable = false, length = 500)
    var storedFilePath: String,

    @Column(name = "file_size")
    var fileSize: Long? = null,

    @Column(name = "content_type", length = 100)
    var contentType: String? = null,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
