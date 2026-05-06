package io.github.timely.timelyapi.organization.controller

import io.github.timely.timelyapi.organization.service.OrganizationOptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "OrganizationOption", description = "Organization option API")
@RestController
@RequestMapping("/v1/organization-options")
class OrganizationOptionController(
    private val organizationOptionService: OrganizationOptionService
) {

    @Operation(summary = "Department options")
    @GetMapping("/departments")
    fun getDepartmentOptions(
        @Parameter(description = "Company serial number", example = "1")
        @RequestParam(required = false)
        companySn: Long?,

        @Parameter(description = "Department name keyword", example = "개발")
        @RequestParam(required = false)
        keyword: String?
    ) = organizationOptionService.getDepartmentOptions(companySn, keyword)

    @Operation(summary = "Position options")
    @GetMapping("/positions")
    fun getPositionOptions(
        @Parameter(description = "Company serial number", example = "1")
        @RequestParam
        companySn: Long,

        @Parameter(description = "Position name keyword", example = "책임")
        @RequestParam(required = false)
        keyword: String?
    ) =
        organizationOptionService.getPositionOptions(companySn, keyword)
}
