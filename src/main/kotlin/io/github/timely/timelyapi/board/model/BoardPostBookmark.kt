package io.github.timely.timelyapi.board.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_board_post_bookmark")
class BoardPostBookmark(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_post_bookmark_sn")
    val boardPostBookmarkSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "board_post_sn", nullable = false)
    var boardPostSn: Long,

    @Column(name = "user_sn", nullable = false)
    var userSn: Long,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
