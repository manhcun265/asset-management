package com.bank.asset_management.config.datetime;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Formatting configuration for date and time in the application
 * This configuration uses ISO format for date and time serialization/deserialization
 */
@Configuration
public class ConfigFormatDateTime implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();

        // Formatter cho LocalDateTime, LocalDate, LocalTime với múi giờ Việt Nam
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        registrar.setDateTimeFormatter(dateTimeFormatter); // Áp dụng cho LocalDateTime
        registrar.setDateFormatter(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm:ss"));

        registrar.registerFormatters(registry);
    }
}

