package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardPostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardPostLikeRepository : JpaRepository<BoardPostLike, Long> {

    fun findByBoardPostSnAndUserSn(boardPostSn: Long, userSn: Long): BoardPostLike?

    fun countByBoardPostSnAndUseYn(boardPostSn: Long, useYn: String): Long

    fun existsByBoardPostSnAndUserSnAndUseYn(boardPostSn: Long, userSn: Long, useYn: String): Boolean

    @Modifying
    @Query(
        """
        update BoardPostLike l
        set l.useYn = 'N'
        where l.boardPostSn = :boardPostSn
          and l.useYn = 'Y'
        """
    )
    fun deactivateByBoardPostSn(@Param("boardPostSn") boardPostSn: Long): Int
}
