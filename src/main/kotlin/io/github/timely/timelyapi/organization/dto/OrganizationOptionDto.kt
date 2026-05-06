package io.github.timely.timelyapi.organization.dto

import io.swagger.v3.oas.annotations.media.Schema

class OrganizationOptionDto {
    @Schema(description = "Select option response")
    data class OptionResponse(
        @field:Schema(description = "Option value", example = "MANAGER")
        val value: String,

        @field:Schema(description = "Option label", example = "Manager")
        val label: String
    )
}
