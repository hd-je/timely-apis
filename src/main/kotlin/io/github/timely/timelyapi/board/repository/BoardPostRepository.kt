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

    fun countByCompanySnAndAuthorUserSnAndUseYn(companySn: Long, authorUserSn: Long, useYn: String): Long

    @Query(
        value = """
            select
              u.user_sn as userSn,
              u.user_nm as userName,
              u.avatar_url as avatarUrl,
              coalesce(pc.post_count, 0) as postCount,
              coalesce(cc.comment_count, 0) as commentCount
            from tb_user u
            left join (
              select p.author_user_sn, count(*) as post_count
              from tb_board_post p
              where p.company_sn = :companySn
                and p.use_yn = 'Y'
              group by p.author_user_sn
            ) pc on pc.author_user_sn = u.user_sn
            left join (
              select c.author_user_sn, count(*) as comment_count
              from tb_board_comment c
              join tb_board_post p on p.board_post_sn = c.board_post_sn
              where p.company_sn = :companySn
                and p.use_yn = 'Y'
                and c.use_yn = 'Y'
              group by c.author_user_sn
            ) cc on cc.author_user_sn = u.user_sn
            where u.company_sn = :companySn
              and u.use_yn = 'Y'
              and u.user_status = 'ACTIVE'
              and (coalesce(pc.post_count, 0) > 0 or coalesce(cc.comment_count, 0) > 0)
            order by coalesce(pc.post_count, 0) desc,
                     coalesce(cc.comment_count, 0) desc,
                     u.user_sn asc
        """,
        nativeQuery = true
    )
    fun findActiveUserActivities(
        @Param("companySn") companySn: Long,
        pageable: Pageable
    ): List<ActiveUserActivityProjection>

    @Query(
        value = """
            select
              (
                select count(*)
                from tb_board_post p
                where p.company_sn = :companySn
                  and p.author_user_sn = :userSn
                  and p.use_yn = 'Y'
              ) as postCount,
              (
                select count(*)
                from tb_board_comment c
                join tb_board_post p on p.board_post_sn = c.board_post_sn
                where p.company_sn = :companySn
                  and p.use_yn = 'Y'
                  and c.author_user_sn = :userSn
                  and c.use_yn = 'Y'
              ) as commentCount
        """,
        nativeQuery = true
    )
    fun findUserActivity(
        @Param("userSn") userSn: Long,
        @Param("companySn") companySn: Long
    ): UserActivityProjection

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

interface ActiveUserActivityProjection {
    val userSn: Long
    val userName: String
    val avatarUrl: String?
    val postCount: Long
    val commentCount: Long
}

interface UserActivityProjection {
    val postCount: Long
    val commentCount: Long
}
