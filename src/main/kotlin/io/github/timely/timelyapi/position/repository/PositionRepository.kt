package io.github.timely.timelyapi.position.repository

import io.github.timely.timelyapi.position.model.Position
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PositionRepository : JpaRepository<Position, Long> {

    fun existsByCompanySnAndPositionCd(
        companySn: Long,
        positionCd: String
    ): Boolean

    fun existsByCompanySnAndPositionCdAndUseYn(
        companySn: Long,
        positionCd: String,
        useYn: String
    ): Boolean

    @Query(
        """
        select p
        from Position p
        where p.useYn = 'Y'
          and p.companySn = :companySn
          and (:keyword is null or lower(p.positionNm) like lower(concat('%', :keyword, '%')))
        order by p.sortOrd asc, p.positionSn asc
        """
    )
    fun searchActivePositions(
        @Param("companySn") companySn: Long,
        @Param("keyword") keyword: String?
    ): List<Position>

    @Query(
        """
        select p
        from Position p
        where (:companySn is null or p.companySn = :companySn)
          and (:useYn is null or p.useYn = :useYn)
          and (
              :keyword is null
              or lower(p.positionCd) like lower(concat('%', :keyword, '%'))
              or lower(p.positionNm) like lower(concat('%', :keyword, '%'))
          )
        order by p.companySn asc, p.sortOrd asc, p.positionSn asc
        """
    )
    fun searchPositions(
        @Param("companySn") companySn: Long?,
        @Param("keyword") keyword: String?,
        @Param("useYn") useYn: String?
    ): List<Position>
}
