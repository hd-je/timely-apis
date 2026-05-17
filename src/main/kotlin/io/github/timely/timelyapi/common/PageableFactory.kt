package io.github.timely.timelyapi.common

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

object PageableFactory {
    fun create(
        page: Int,
        size: Int,
        sort: String?,
        allowedProperties: Set<String>,
        defaultProperty: String,
        defaultDirection: Sort.Direction = Sort.Direction.DESC
    ): Pageable {
        val parsedSort = sort
            ?.split(",")
            ?.map { it.trim() }
            ?.takeIf { it.isNotEmpty() }

        val property = parsedSort
            ?.firstOrNull()
            ?.takeIf { it in allowedProperties }
            ?: defaultProperty

        val direction = parsedSort
            ?.getOrNull(1)
            ?.let { runCatching { Sort.Direction.fromString(it) }.getOrNull() }
            ?: defaultDirection

        return PageRequest.of(page.coerceAtLeast(0), size.coerceAtLeast(1), Sort.by(direction, property))
    }
}
