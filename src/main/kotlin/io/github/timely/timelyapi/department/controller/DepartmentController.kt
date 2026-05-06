package io.github.timely.timelyapi.department.controller

import io.github.timely.timelyapi.department.dto.DepartmentDto
import io.github.timely.timelyapi.department.service.DepartmentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Department", description = "Department API")
@RestController
@RequestMapping("/v1/departments")
class DepartmentController(
    private val departmentService: DepartmentService
) {

    @Operation(summary = "Search departments")
    @GetMapping
    fun searchDepartments(
        @Parameter(description = "Company serial number", example = "1")
        @RequestParam(required = false)
        companySn: Long?,

        @Parameter(description = "Department name keyword", example = "개발")
        @RequestParam(required = false)
        keyword: String?
    ) =
        departmentService.searchDepartments(companySn, keyword)

    @Operation(summary = "Get department")
    @GetMapping("/{deptSn}")
    fun getDepartment(
        @Parameter(description = "Department serial number", example = "1")
        @PathVariable
        deptSn: Long
    ) =
        departmentService.getDepartment(deptSn)

    @Operation(summary = "Create department")
    @PostMapping
    fun createDepartment(
        @RequestBody
        request: DepartmentDto.CreateRequest
    ) =
        departmentService.createDepartment(request)

    @Operation(summary = "Update department")
    @PutMapping("/{deptSn}")
    fun updateDepartment(
        @Parameter(description = "Department serial number", example = "1")
        @PathVariable
        deptSn: Long,

        @RequestBody
        request: DepartmentDto.UpdateRequest
    ) =
        departmentService.updateDepartment(deptSn, request)

    @Operation(summary = "Update department use flag")
    @PatchMapping("/{deptSn}/use-yn")
    fun updateDepartmentUseYn(
        @Parameter(description = "Department serial number", example = "1")
        @PathVariable
        deptSn: Long,

        @RequestBody
        request: DepartmentDto.UseYnRequest
    ) =
        departmentService.updateDepartmentUseYn(deptSn, request)
}
