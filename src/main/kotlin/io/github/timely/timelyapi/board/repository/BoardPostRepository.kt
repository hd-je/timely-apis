package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardPost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BoardPostRepository : JpaRepository<BoardPost, Long> {

    fun findByBoardPostSnAndCompanySnAndUseYn(boardPostSn: Long, companySn: Long, useYn: String): BoardPost?

    @Query(
        value = """
            select
              p.boardPostSn as boardPostSn,
              p.authorUserSn as authorUserSn,
              u.userNm as authorName,
              p.category as category,
              p.status as status,
              p.title as title,
              p.content as content,
              p.viewCnt as viewCnt,
              (
                select count(c.boardCommentSn)
                from BoardComment c
                where c.boardPostSn = p.boardPostSn
                  and c.useYn = 'Y'
              ) as commentCount,
              (
                select count(l.boardPostLikeSn)
                from BoardPostLike l
                where l.boardPostSn = p.boardPostSn
                  and l.useYn = 'Y'
              ) as likeCount,
              case when (
                select count(ml.boardPostLikeSn)
                from BoardPostLike ml
                where ml.boardPostSn = p.boardPostSn
                  and ml.userSn = :userSn
                  and ml.useYn = 'Y'
              ) > 0 then true else false end as likedByMe,
              case when (
                select count(b.boardPostBookmarkSn)
                from BoardPostBookmark b
                where b.boardPostSn = p.boardPostSn
                  and b.userSn = :userSn
                  and b.useYn = 'Y'
              ) > 0 then true else false end as bookmarkedByMe,
              p.createDt as createDt,
              p.updateDt as updateDt
            from BoardPost p
            left join TimelyUser u on u.userSn = p.authorUserSn and u.companySn = p.companySn
            where p.useYn = 'Y'
              and p.companySn = :companySn
              and (:category is null or p.category = :category)
              and (:status is null or p.status = :status)
              and (
                  :keyword is null
                  or lower(p.title) like lower(concat('%', :keyword, '%'))
                  or lower(p.content) like lower(concat('%', :keyword, '%'))
                  or lower(u.userNm) like lower(concat('%', :keyword, '%'))
              )
        """,
        countQuery = """
            select count(p)
            from BoardPost p
            left join TimelyUser u on u.userSn = p.authorUserSn and u.companySn = p.companySn
            where p.useYn = 'Y'
              and p.companySn = :companySn
              and (:category is null or p.category = :category)
              and (:status is null or p.status = :status)
              and (
                  :keyword is null
                  or lower(p.title) like lower(concat('%', :keyword, '%'))
                  or lower(p.content) like lower(concat('%', :keyword, '%'))
                  or lower(u.userNm) like lower(concat('%', :keyword, '%'))
              )
        """
    )
    fun searchActivePostSummaries(
        @Param("userSn") userSn: Long,
        @Param("companySn") companySn: Long,
        @Param("category") category: String?,
        @Param("status") status: String?,
        @Param("keyword") keyword: String?,
        pageable: Pageable
    ): Page<BoardPostSummaryProjection>

    @Modifying
    @Query(
        """
        update BoardPost p
        set p.viewCnt = p.viewCnt + 1
        where p.boardPostSn = :boardPostSn
          and p.companySn = :companySn
          and p.useYn = 'Y'
        """
    )
    fun increaseViewCount(
        @Param("companySn") companySn: Long,
        @Param("boardPostSn") boardPostSn: Long
    ): Int

    @Query(
        """
        select
          p.boardPostSn as boardPostSn,
          p.authorUserSn as authorUserSn,
          u.userNm as authorName,
          p.category as category,
          p.status as status,
          p.title as title,
          p.content as content,
          p.viewCnt as viewCnt,
          (
            select count(c.boardCommentSn)
            from BoardComment c
            where c.boardPostSn = p.boardPostSn
              and c.useYn = 'Y'
          ) as commentCount,
          (
            select count(l.boardPostLikeSn)
            from BoardPostLike l
            where l.boardPostSn = p.boardPostSn
              and l.useYn = 'Y'
          ) as likeCount,
          case when (
            select count(ml.boardPostLikeSn)
            from BoardPostLike ml
            where ml.boardPostSn = p.boardPostSn
              and ml.userSn = :userSn
              and ml.useYn = 'Y'
          ) > 0 then true else false end as likedByMe,
          case when (
            select count(b.boardPostBookmarkSn)
            from BoardPostBookmark b
            where b.boardPostSn = p.boardPostSn
              and b.userSn = :userSn
              and b.useYn = 'Y'
          ) > 0 then true else false end as bookmarkedByMe,
          p.useYn as useYn,
          p.createDt as createDt,
          p.updateDt as updateDt
        from BoardPost p
        left join TimelyUser u on u.userSn = p.authorUserSn and u.companySn = p.companySn
        where p.useYn = 'Y'
          and p.companySn = :companySn
          and p.boardPostSn = :boardPostSn
        """
    )
    fun findActivePostDetail(
        @Param("userSn") userSn: Long,
        @Param("companySn") companySn: Long,
        @Param("boardPostSn") boardPostSn: Long
    ): BoardPostDetailProjection?

    @Query(
        """
        select p.category as category, count(p.boardPostSn) as postCount
        from BoardPost p
        where p.useYn = 'Y'
          and p.companySn = :companySn
        group by p.category
        """
    )
    fun countActivePostsByCategory(@Param("companySn") companySn: Long): List<CategoryCountProjection>

    fun countByCompanySnAndUseYn(companySn: Long, useYn: String): Long

    fun findByCompanySnAndCategoryAndUseYn(companySn: Long, category: String, useYn: String, pageable: Pageable): Page<BoardPost>
}

interface CategoryCountProjection {
    val category: String
    val postCount: Long
}

interface BoardPostSummaryProjection {
    val boardPostSn: Long
    val authorUserSn: Long
    val authorName: String?
    val category: String
    val status: String
    val title: String
    val content: String
    val viewCnt: Long
    val commentCount: Long
    val likeCount: Long
    val likedByMe: Boolean
    val bookmarkedByMe: Boolean
    val createDt: LocalDateTime?
    val updateDt: LocalDateTime?
}

interface BoardPostDetailProjection : BoardPostSummaryProjection {
    val useYn: String
}
