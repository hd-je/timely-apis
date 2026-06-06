package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardPostBookmark
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardPostBookmarkRepository : JpaRepository<BoardPostBookmark, Long> {

    fun findByBoardPostSnAndUserSn(boardPostSn: Long, userSn: Long): BoardPostBookmark?

    fun existsByBoardPostSnAndUserSnAndUseYn(boardPostSn: Long, userSn: Long, useYn: String): Boolean

    @Modifying
    @Query(
        """
        update BoardPostBookmark b
        set b.useYn = 'N'
        where b.boardPostSn = :boardPostSn
          and b.useYn = 'Y'
        """
    )
    fun deactivateByBoardPostSn(@Param("boardPostSn") boardPostSn: Long): Int
}
