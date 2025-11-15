package com.microservicio.rutas.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Converter(autoApply = false)
public class LocalDateTimeEpochAttributeConverter implements AttributeConverter<LocalDateTime, Long> {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    @Override
    public Long convertToDatabaseColumn(LocalDateTime attribute) {
        if (attribute == null) return null;
        return attribute.atZone(ZONE).toInstant().toEpochMilli();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long dbData) {
        if (dbData == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dbData), ZONE);
    }
}
