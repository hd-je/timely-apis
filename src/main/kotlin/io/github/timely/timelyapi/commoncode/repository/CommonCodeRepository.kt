package io.github.timely.timelyapi.commoncode.repository

import io.github.timely.timelyapi.commoncode.model.CommonCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommonCodeRepository : JpaRepository<CommonCode, Long> {
    fun findByUseYnOrderByCodeGroupAscSortSeqAscCodeAsc(useYn: String): List<CommonCode>

    fun findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc(codeGroup: String, useYn: String): List<CommonCode>

    fun existsByCodeGroupAndCodeAndUseYn(codeGroup: String, code: String, useYn: String): Boolean
}
