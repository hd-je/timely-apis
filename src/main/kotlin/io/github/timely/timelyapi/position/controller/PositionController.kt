package io.github.timely.timelyapi.position.controller

import io.github.timely.timelyapi.position.dto.PositionDto
import io.github.timely.timelyapi.position.service.PositionService
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

@Tag(name = "Position", description = "Position API")
@RestController
@RequestMapping("/v1/positions")
class PositionController(
    private val positionService: PositionService
) {

    @Operation(summary = "Search positions")
    @GetMapping
    fun searchPositions(
        @Parameter(description = "Company serial number", example = "1")
        @RequestParam(required = false)
        companySn: Long?,

        @Parameter(description = "Position code or name keyword", example = "LEAD")
        @RequestParam(required = false)
        keyword: String?,

        @Parameter(description = "Use flag", example = "Y")
        @RequestParam(required = false)
        useYn: String?
    ) =
        positionService.searchPositions(companySn, keyword, useYn)

    @Operation(summary = "Get position")
    @GetMapping("/{positionSn}")
    fun getPosition(
        @Parameter(description = "Position serial number", example = "1")
        @PathVariable
        positionSn: Long
    ) =
        positionService.getPosition(positionSn)

    @Operation(summary = "Create position")
    @PostMapping
    fun createPosition(
        @RequestBody
        request: PositionDto.CreateRequest
    ) =
        positionService.createPosition(request)

    @Operation(summary = "Update position")
    @PutMapping("/{positionSn}")
    fun updatePosition(
        @Parameter(description = "Position serial number", example = "1")
        @PathVariable
        positionSn: Long,

        @RequestBody
        request: PositionDto.UpdateRequest
    ) =
        positionService.updatePosition(positionSn, request)

    @Operation(summary = "Update position use flag")
    @PatchMapping("/{positionSn}")
    fun updatePositionUseYn(
        @Parameter(description = "Position serial number", example = "1")
        @PathVariable
        positionSn: Long,

        @RequestBody
        request: PositionDto.UseYnRequest
    ) =
        positionService.updatePositionUseYn(positionSn, request)
}
