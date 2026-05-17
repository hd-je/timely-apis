package io.github.timely.timelyapi.board.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_board_comment")
class BoardComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_COMMENT_SN")
    val boardCommentSn: Long? = null,

    @Column(name = "CREATE_DT", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "UPDATE_DT", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "BOARD_POST_SN", nullable = false)
    var boardPostSn: Long,

    @Column(name = "AUTHOR_USER_SN", nullable = false)
    var authorUserSn: Long,

    @Column(name = "CONTENT", nullable = false, columnDefinition = "text")
    var content: String,

    @Column(name = "USE_YN", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
