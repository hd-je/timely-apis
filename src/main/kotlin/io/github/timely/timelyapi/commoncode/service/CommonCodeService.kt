package io.github.timely.timelyapi.commoncode.service

import io.github.timely.timelyapi.commoncode.dto.CommonCodeDto
import io.github.timely.timelyapi.commoncode.repository.CommonCodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommonCodeService(
    private val commonCodeRepository: CommonCodeRepository
) {

    @Transactional(readOnly = true)
    fun getActiveCodes(codeGroup: String?): List<CommonCodeDto.Response> {
        val codes = if (codeGroup.isNullOrBlank()) {
            commonCodeRepository.findByUseYnOrderByCodeGroupAscSortSeqAscCodeAsc("Y")
        } else {
            commonCodeRepository.findByCodeGroupAndUseYnOrderBySortSeqAscCodeAsc(codeGroup.trim(), "Y")
        }

        return codes
            .map {
                CommonCodeDto.Response(
                    codeGroup = it.codeGroup,
                    code = it.code,
                    codeNm = it.codeNm,
                    sortSeq = it.sortSeq
                )
            }
    }
}
