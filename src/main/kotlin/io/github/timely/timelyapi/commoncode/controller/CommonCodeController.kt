package io.github.timely.timelyapi.commoncode.controller

import io.github.timely.timelyapi.commoncode.service.CommonCodeService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "CommonCode", description = "공통코드 API")
@RestController
@RequestMapping("/v1/common-codes")
class CommonCodeController(
    private val commonCodeService: CommonCodeService
) {

    @Operation(summary = "공통코드 목록 조회", description = "코드 그룹을 지정하면 해당 그룹의 활성 공통코드를 조회하고, 지정하지 않으면 활성 공통코드 전체를 조회한다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping
    fun getCommonCodes(
        @Parameter(description = "코드 그룹. 미입력 시 전체 활성 공통코드 조회", example = "BOARD_CATEGORY")
        @RequestParam(required = false)
        codeGroup: String?
    ) = commonCodeService.getActiveCodes(codeGroup)
}
