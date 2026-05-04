package io.github.timely.timelyapi.department.service

import io.github.timely.timelyapi.department.dto.DepartmentDto
import io.github.timely.timelyapi.department.model.Department
import io.github.timely.timelyapi.department.repository.DepartmentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository
) {

    @Transactional(readOnly = true)
    fun getDepartment(deptSn: Long): DepartmentDto.Response {
        val department = departmentRepository.findById(deptSn)
            .orElseThrow { IllegalArgumentException("Department not found") }

        return department.toResponse()
    }

    @Transactional(readOnly = true)
    fun searchDepartments(companySn: Long?, keyword: String?): List<DepartmentDto.SimpleResponse> {
        return departmentRepository
            .searchActiveDepartments(companySn, keyword?.takeIf { it.isNotBlank() })
            .map { DepartmentDto.SimpleResponse(it.deptSn!!, it.companySn, it.deptNm) }
    }

    private fun Department.toResponse() =
        DepartmentDto.Response(
            deptSn = deptSn!!,
            companySn = companySn,
            deptNm = deptNm,
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )
}
