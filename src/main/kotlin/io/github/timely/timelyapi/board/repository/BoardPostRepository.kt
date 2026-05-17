package io.github.timely.timelyapi.board.repository

import io.github.timely.timelyapi.board.model.BoardPost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BoardPostRepository : JpaRepository<BoardPost, Long> {

    fun findByBoardPostSnAndCompanySnAndUseYn(boardPostSn: Long, companySn: Long, useYn: String): BoardPost?

    @Query(
        """
        select p
        from BoardPost p
        where p.useYn = 'Y'
          and p.companySn = :companySn
          and (:category is null or p.category = :category)
          and (:status is null or p.status = :status)
          and (
              :keyword is null
              or lower(p.title) like lower(concat('%', :keyword, '%'))
              or lower(p.content) like lower(concat('%', :keyword, '%'))
          )
        """
    )
    fun searchActivePosts(
        @Param("companySn") companySn: Long,
        @Param("category") category: String?,
        @Param("status") status: String?,
        @Param("keyword") keyword: String?,
        pageable: Pageable
    ): Page<BoardPost>
}
