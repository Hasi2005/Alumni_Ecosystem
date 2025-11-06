package com.example.job_service.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaInitializer {

    private final JdbcTemplate jdbc;

    public SchemaInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureAutoIncrementIds() {
        // Ensure a simple sequence table for jobs exists
        try {
            jdbc.execute("CREATE TABLE IF NOT EXISTS job_sequence (seq_key TINYINT PRIMARY KEY, current BIGINT NOT NULL)");
            jdbc.update("INSERT IGNORE INTO job_sequence (seq_key, current) VALUES (1, 0)");
            // Reset sequence to match existing data precisely
            Long max = 0L;
            try { max = jdbc.queryForObject("SELECT COALESCE(MAX(id),0) FROM jobs", Long.class); } catch (Exception ignored2) {}
            if (max == null) max = 0L;
            jdbc.update("UPDATE job_sequence SET current = ? WHERE seq_key = 1", max);
        } catch (Exception ignored) {}

        // Keep applications auto-increment for their ids
        try { jdbc.execute("ALTER TABLE applications MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT"); } catch (Exception ignored) {}
    }
}
