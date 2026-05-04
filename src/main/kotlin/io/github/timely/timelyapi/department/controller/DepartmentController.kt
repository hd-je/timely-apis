package io.github.timely.timelyapi.department.controller

import io.github.timely.timelyapi.department.service.DepartmentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Department", description = "부서 API")
@RestController
@RequestMapping("/v1/departments")
class DepartmentController(
    private val departmentService: DepartmentService
) {

    @Operation(summary = "부서 목록 검색")
    @GetMapping
    fun searchDepartments(
        @Parameter(description = "회사 일련번호. 입력하면 해당 회사의 부서만 조회", example = "1")
        @RequestParam(required = false)
        companySn: Long?,

        @Parameter(description = "부서명 검색어", example = "개발")
        @RequestParam(required = false)
        keyword: String?
    ) = departmentService.searchDepartments(companySn, keyword)

    @Operation(summary = "부서 상세 조회")
    @GetMapping("/{deptSn}")
    fun getDepartment(
        @Parameter(description = "부서 일련번호", example = "1")
        @PathVariable
        deptSn: Long
    ) =
        departmentService.getDepartment(deptSn)
}
