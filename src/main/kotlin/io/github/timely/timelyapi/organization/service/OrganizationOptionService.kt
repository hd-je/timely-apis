package io.github.timely.timelyapi.organization.service

import io.github.timely.timelyapi.department.repository.DepartmentRepository
import io.github.timely.timelyapi.organization.dto.OrganizationOptionDto
import io.github.timely.timelyapi.position.repository.PositionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrganizationOptionService(
    private val departmentRepository: DepartmentRepository,
    private val positionRepository: PositionRepository
) {

    @Transactional(readOnly = true)
    fun getDepartmentOptions(
        companySn: Long?,
        keyword: String?
    ): List<OrganizationOptionDto.OptionResponse> {
        val departmentOptions = departmentRepository
            .searchActiveDepartments(companySn, keyword?.takeIf { it.isNotBlank() })
            .map {
                OrganizationOptionDto.OptionResponse(
                    value = it.deptSn!!.toString(),
                    label = it.deptNm
                )
            }

        return listOf(OrganizationOptionDto.OptionResponse("", "부서 선택")) + departmentOptions
    }

    @Transactional(readOnly = true)
    fun getPositionOptions(
        companySn: Long,
        keyword: String?
    ): List<OrganizationOptionDto.OptionResponse> {
        val positionOptions = positionRepository
            .searchActivePositions(companySn, keyword?.takeIf { it.isNotBlank() })
            .map {
                OrganizationOptionDto.OptionResponse(
                    value = it.positionCd,
                    label = it.positionNm
                )
            }

        return listOf(OrganizationOptionDto.OptionResponse("", "직급 선택")) + positionOptions
    }
}
