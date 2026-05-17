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
    @Column(name = "board_comment_sn")
    val boardCommentSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "board_post_sn", nullable = false)
    var boardPostSn: Long,

    @Column(name = "author_user_sn", nullable = false)
    var authorUserSn: Long,

    @Column(name = "content", nullable = false, columnDefinition = "text")
    var content: String,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
