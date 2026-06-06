package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardCommentRepository : JpaRepository<BoardComment, Long> {

    fun findByBoardCommentSnAndUseYn(boardCommentSn: Long, useYn: String): BoardComment?

    @Query(
        value = """
            select c
            from BoardComment c
            left join BoardComment p on p.boardCommentSn = c.parentCommentSn
            where c.useYn = 'Y'
              and c.boardPostSn = :boardPostSn
              and (c.parentCommentSn is null or p.useYn = 'Y')
            order by
              coalesce(c.parentCommentSn, c.boardCommentSn) asc,
              case when c.parentCommentSn is null then 0 else 1 end asc,
              c.createDt asc,
              c.boardCommentSn asc
        """,
        countQuery = """
            select count(c)
            from BoardComment c
            left join BoardComment p on p.boardCommentSn = c.parentCommentSn
            where c.useYn = 'Y'
              and c.boardPostSn = :boardPostSn
              and (c.parentCommentSn is null or p.useYn = 'Y')
        """
    )
    fun findActiveCommentsByBoardPostSn(
        @Param("boardPostSn") boardPostSn: Long,
        pageable: Pageable
    ): Page<BoardComment>

    fun countByBoardPostSnAndUseYn(boardPostSn: Long, useYn: String): Long

    @Modifying
    @Query(
        """
        update BoardComment c
        set c.useYn = 'N'
        where c.boardPostSn = :boardPostSn
          and c.useYn = 'Y'
        """
    )
    fun deactivateByBoardPostSn(@Param("boardPostSn") boardPostSn: Long): Int

    @Modifying
    @Query(
        """
        update BoardComment c
        set c.useYn = 'N'
        where (c.boardCommentSn = :boardCommentSn or c.parentCommentSn = :boardCommentSn)
          and c.useYn = 'Y'
        """
    )
    fun deactivateCommentThread(@Param("boardCommentSn") boardCommentSn: Long): Int
}
