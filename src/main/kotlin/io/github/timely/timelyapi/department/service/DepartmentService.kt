package io.github.timely.timelyapi.department.service

import io.github.timely.timelyapi.company.repository.CompanyRepository
import io.github.timely.timelyapi.department.dto.DepartmentDto
import io.github.timely.timelyapi.department.model.Department
import io.github.timely.timelyapi.department.repository.DepartmentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val companyRepository: CompanyRepository
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

    @Transactional
    fun createDepartment(request: DepartmentDto.CreateRequest): DepartmentDto.Response {
        validateCompany(request.companySn)
        val deptNm = request.deptNm.trim()
        validateDepartment(deptNm, request.useYn)

        return departmentRepository.save(
            Department(
                companySn = request.companySn,
                deptNm = deptNm,
                useYn = request.useYn.uppercase()
            )
        ).toResponse()
    }

    @Transactional
    fun updateDepartment(deptSn: Long, request: DepartmentDto.UpdateRequest): DepartmentDto.Response {
        validateCompany(request.companySn)
        val deptNm = request.deptNm.trim()
        validateDepartment(deptNm, request.useYn)

        val department = departmentRepository.findById(deptSn)
            .orElseThrow { IllegalArgumentException("Department not found") }

        department.companySn = request.companySn
        department.deptNm = deptNm
        department.useYn = request.useYn.uppercase()

        return department.toResponse()
    }

    @Transactional
    fun updateDepartmentUseYn(deptSn: Long, request: DepartmentDto.UseYnRequest): DepartmentDto.Response {
        validateUseYn(request.useYn)

        val department = departmentRepository.findById(deptSn)
            .orElseThrow { IllegalArgumentException("Department not found") }

        department.useYn = request.useYn.uppercase()

        return department.toResponse()
    }

    private fun validateCompany(companySn: Long) {
        val company = companyRepository.findById(companySn)
            .orElseThrow { IllegalArgumentException("Company not found") }

        require(company.useYn == "Y") {
            "Company is not active"
        }
    }

    private fun validateDepartment(deptNm: String, useYn: String) {
        require(deptNm.isNotBlank()) {
            "Department name is required"
        }
        validateUseYn(useYn)
    }

    private fun validateUseYn(useYn: String) {
        require(useYn.uppercase() in setOf("Y", "N")) {
            "Use flag must be Y or N"
        }
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
