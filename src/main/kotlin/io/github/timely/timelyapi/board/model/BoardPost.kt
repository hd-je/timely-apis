package io.github.timely.timelyapi.board.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_board_post")
class BoardPost(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_post_sn")
    val boardPostSn: Long? = null,

    @Column(name = "create_dt", insertable = false, updatable = false)
    var createDt: LocalDateTime? = null,

    @Column(name = "update_dt", insertable = false, updatable = false)
    var updateDt: LocalDateTime? = null,

    @Column(name = "author_user_sn", nullable = false)
    var authorUserSn: Long,

    @Column(name = "company_sn", nullable = false)
    var companySn: Long,

    @Column(name = "category", nullable = false, length = 30)
    var category: String,

    @Column(name = "status", nullable = false, length = 30)
    var status: String,

    @Column(name = "title", nullable = false, length = 200)
    var title: String,

    @Column(name = "content", nullable = false, columnDefinition = "text")
    var content: String,

    @Column(name = "view_cnt", nullable = false)
    var viewCnt: Long = 0,

    @Column(name = "use_yn", nullable = false, columnDefinition = "char(1)")
    var useYn: String = "Y"
)
