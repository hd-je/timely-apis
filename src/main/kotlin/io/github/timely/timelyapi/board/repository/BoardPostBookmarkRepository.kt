package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardPostBookmark
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BoardPostBookmarkRepository : JpaRepository<BoardPostBookmark, Long> {

    fun findByBoardPostSnAndUserSn(boardPostSn: Long, userSn: Long): BoardPostBookmark?

    fun existsByBoardPostSnAndUserSnAndUseYn(boardPostSn: Long, userSn: Long, useYn: String): Boolean

    @Query(
        """
        select count(b.boardPostBookmarkSn)
        from BoardPostBookmark b
        join BoardPost p on p.boardPostSn = b.boardPostSn
        where b.userSn = :userSn
          and b.useYn = 'Y'
          and p.companySn = :companySn
          and p.useYn = 'Y'
        """
    )
    fun countActiveBookmarks(
        @Param("userSn") userSn: Long,
        @Param("companySn") companySn: Long
    ): Long

    @Query(
        value = """
            select
              p.boardPostSn as boardPostSn,
              p.category as category,
              p.title as title,
              p.createDt as createDt,
              b.createDt as bookmarkedDt
            from BoardPostBookmark b
            join BoardPost p on p.boardPostSn = b.boardPostSn
            where b.userSn = :userSn
              and b.useYn = 'Y'
              and p.companySn = :companySn
              and p.useYn = 'Y'
            order by b.createDt desc, b.boardPostBookmarkSn desc
        """,
        countQuery = """
            select count(b.boardPostBookmarkSn)
            from BoardPostBookmark b
            join BoardPost p on p.boardPostSn = b.boardPostSn
            where b.userSn = :userSn
              and b.useYn = 'Y'
              and p.companySn = :companySn
              and p.useYn = 'Y'
        """
    )
    fun findRecentActiveBookmarks(
        @Param("userSn") userSn: Long,
        @Param("companySn") companySn: Long,
        pageable: Pageable
    ): Page<RecentBookmarkProjection>

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

interface RecentBookmarkProjection {
    val boardPostSn: Long
    val category: String
    val title: String
    val createDt: LocalDateTime?
    val bookmarkedDt: LocalDateTime?
}
