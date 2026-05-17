package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardCommentRepository : JpaRepository<BoardComment, Long> {

    fun findByBoardCommentSnAndUseYn(boardCommentSn: Long, useYn: String): BoardComment?

    @Query(
        """
        select c
        from BoardComment c
        where c.useYn = 'Y'
          and c.boardPostSn = :boardPostSn
        """
    )
    fun findActiveCommentsByBoardPostSn(
        @Param("boardPostSn") boardPostSn: Long,
        pageable: Pageable
    ): Page<BoardComment>

    fun countByBoardPostSnAndUseYn(boardPostSn: Long, useYn: String): Long
}
