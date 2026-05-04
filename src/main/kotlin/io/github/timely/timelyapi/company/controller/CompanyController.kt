package io.github.timely.timelyapi.company.controller

import io.github.timely.timelyapi.company.service.CompanyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Company", description = "회사 API")
@RestController
@RequestMapping("/v1/companies")
class CompanyController(
    private val companyService: CompanyService
) {

    @Operation(summary = "회사 목록 검색")
    @GetMapping
    fun searchCompanies(
        @Parameter(description = "회사명 검색어", example = "삼성")
        @RequestParam(required = false)
        keyword: String?
    ) =
        companyService.searchCompanies(keyword)

    @Operation(summary = "회사 상세 조회")
    @GetMapping("/{companySn}")
    fun getCompany(
        @Parameter(description = "회사 일련번호", example = "1")
        @PathVariable
        companySn: Long
    ) =
        companyService.getCompany(companySn)
}
