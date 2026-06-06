package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardCommentLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardCommentLikeRepository : JpaRepository<BoardCommentLike, Long> {

    fun findByBoardCommentSnAndUserSn(boardCommentSn: Long, userSn: Long): BoardCommentLike?

    fun countByBoardCommentSnAndUseYn(boardCommentSn: Long, useYn: String): Long

    fun existsByBoardCommentSnAndUserSnAndUseYn(boardCommentSn: Long, userSn: Long, useYn: String): Boolean

    @Modifying
    @Query(
        """
        update BoardCommentLike l
        set l.useYn = 'N'
        where l.boardCommentSn = :boardCommentSn
          and l.useYn = 'Y'
        """
    )
    fun deactivateByBoardCommentSn(@Param("boardCommentSn") boardCommentSn: Long): Int

    @Modifying
    @Query(
        """
        update BoardCommentLike l
        set l.useYn = 'N'
        where l.boardCommentSn in (
            select c.boardCommentSn
            from BoardComment c
            where c.boardPostSn = :boardPostSn
        )
          and l.useYn = 'Y'
        """
    )
    fun deactivateByBoardPostSn(@Param("boardPostSn") boardPostSn: Long): Int

    @Modifying
    @Query(
        """
        update BoardCommentLike l
        set l.useYn = 'N'
        where l.boardCommentSn in (
            select c.boardCommentSn
            from BoardComment c
            where c.boardCommentSn = :boardCommentSn
               or c.parentCommentSn = :boardCommentSn
        )
          and l.useYn = 'Y'
        """
    )
    fun deactivateByCommentThread(@Param("boardCommentSn") boardCommentSn: Long): Int
}
