package io.github.timely.timelyapi.company.service

import io.github.timely.timelyapi.company.dto.CompanyDto
import io.github.timely.timelyapi.company.model.Company
import io.github.timely.timelyapi.company.repository.CompanyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompanyService(
    private val companyRepository: CompanyRepository
) {

    @Transactional(readOnly = true)
    fun getCompany(companySn: Long): CompanyDto.Response {
        val company = companyRepository.findById(companySn)
            .orElseThrow { IllegalArgumentException("Company not found") }

        return company.toResponse()
    }

    @Transactional(readOnly = true)
    fun searchCompanies(keyword: String?): List<CompanyDto.SimpleResponse> {
        return companyRepository.searchActiveCompanies(keyword?.takeIf { it.isNotBlank() })
            .map { CompanyDto.SimpleResponse(it.companySn!!, it.companyNm) }
    }

    private fun Company.toResponse() =
        CompanyDto.Response(
            companySn = companySn!!,
            companyNm = companyNm,
            useYn = useYn,
            createDt = createDt,
            updateDt = updateDt
        )
}
