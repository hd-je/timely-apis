package io.github.timely.timelyapi.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Configuration
class DateTimeConfig {
    @Bean
    fun localDateTimeResponseModule(): Module = SimpleModule().apply {
        addSerializer(LocalDateTime::class.java, SeoulLocalDateTimeSerializer)
    }
}

/**
 * DB에는 시간대가 없는 LocalDateTime을 유지하되 API 응답에는 서비스 기준 시간대를 명시한다.
 * 역직렬화기는 등록하지 않으므로 기존 요청의 LocalDateTime 입력 계약은 변경되지 않는다.
 */
internal object SeoulLocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {
    private val seoulZone = ZoneId.of("Asia/Seoul")

    override fun serialize(value: LocalDateTime, generator: JsonGenerator, serializers: SerializerProvider) {
        val offsetDateTime = value.atZone(seoulZone).toOffsetDateTime()
        generator.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime))
    }
}
