package io.github.timely.timelyapi.position.service

import io.github.timely.timelyapi.company.repository.CompanyRepository
import io.github.timely.timelyapi.position.dto.PositionDto
import io.github.timely.timelyapi.position.model.Position
import io.github.timely.timelyapi.position.repository.PositionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PositionService(
    private val positionRepository: PositionRepository,
    private val companyRepository: CompanyRepository
) {

    @Transactional(readOnly = true)
    fun searchPositions(
        companySn: Long?,
        keyword: String?,
        useYn: String?
    ): List<PositionDto.Response> {
        useYn?.let { validateUseYn(it) }

        return positionRepository.searchPositions(
            companySn = companySn,
            keyword = keyword?.takeIf { it.isNotBlank() },
            useYn = useYn?.uppercase()
        ).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getPosition(positionSn: Long): PositionDto.Response {
        val position = positionRepository.findById(positionSn)
            .orElseThrow { IllegalArgumentException("Position not found") }

        return position.toResponse()
    }

    @Transactional
    fun createPosition(request: PositionDto.CreateRequest): PositionDto.Response {
        validateCompany(request.companySn)
        val positionCd = request.positionCd.trim().uppercase()
        val positionNm = request.positionNm.trim()
        validatePosition(positionCd, positionNm, request.useYn)

        require(!positionRepository.existsByCompanySnAndPositionCd(request.companySn, positionCd)) {
            "Position code already exists"
        }

        return positionRepository.save(
            Position(
                companySn = request.companySn,
                positionCd = positionCd,
                positionNm = positionNm,
                sortOrd = request.sortOrd,
                useYn = request.useYn.uppercase()
            )
        ).toResponse()
    }

    @Transactional
    fun updatePosition(positionSn: Long, request: PositionDto.UpdateRequest): PositionDto.Response {
        validateCompany(request.companySn)
        val positionCd = request.positionCd.trim().uppercase()
        val positionNm = request.positionNm.trim()
        validatePosition(positionCd, positionNm, request.useYn)

        val position = positionRepository.findById(positionSn)
            .orElseThrow { IllegalArgumentException("Position not found") }

        if (position.companySn != request.companySn || position.positionCd != positionCd) {
            require(!positionRepository.existsByCompanySnAndPositionCd(request.companySn, positionCd)) {
                "Position code already exists"
            }
        }

        position.companySn = request.companySn
        position.positionCd = positionCd
        position.positionNm = positionNm
        position.sortOrd = request.sortOrd
        position.useYn = request.useYn.uppercase()

        return position.toResponse()
    }

    @Transactional
    fun updatePositionUseYn(positionSn: Long, request: PositionDto.UseYnRequest): PositionDto.Response {
        validateUseYn(request.useYn)

        val position = positionRepository.findById(positionSn)
            .orElseThrow { IllegalArgumentException("Position not found") }

        position.useYn = request.useYn.uppercase()

        return position.toResponse()
    }

    private fun validateCompany(companySn: Long) {
        val company = companyRepository.findById(companySn)
            .orElseThrow { IllegalArgumentException("Company not found") }

        require(company.useYn == "Y") {
            "Company is not active"
        }
    }

    private fun validatePosition(positionCd: String, positionNm: String, useYn: String) {
        require(positionCd.isNotBlank()) {
            "Position code is required"
        }
        require(positionNm.isNotBlank()) {
            "Position name is required"
        }
        validateUseYn(useYn)
    }

    private fun validateUseYn(useYn: String) {
        require(useYn.uppercase() in setOf("Y", "N")) {
            "Use flag must be Y or N"
        }
    }

    private fun Position.toResponse() =
        PositionDto.Response(
            positionSn = positionSn!!,
            companySn = companySn,
            positionCd = positionCd,
            positionNm = positionNm,
            sortOrd = sortOrd,
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )
}
