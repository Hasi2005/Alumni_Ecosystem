package com.example.job_service.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobIdGenerator {
    private final JdbcTemplate jdbc;

    public JobIdGenerator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long nextId() {
        // Ensure row exists
        try { jdbc.update("INSERT IGNORE INTO job_sequence (seq_key, current) VALUES (1, 0)"); } catch (Exception ignored) {}
        int rows = 0;
        try {
            rows = jdbc.update("UPDATE job_sequence SET current = LAST_INSERT_ID(current + 1) WHERE seq_key = 1");
        } catch (Exception ignored) {}
        if (rows == 0) {
            // Attempt to (re)create and update again
            try { jdbc.update("INSERT IGNORE INTO job_sequence (seq_key, current) VALUES (1, 0)"); } catch (Exception ignored) {}
            try { rows = jdbc.update("UPDATE job_sequence SET current = LAST_INSERT_ID(current + 1) WHERE seq_key = 1"); } catch (Exception ignored) {}
        }
        Long val = 1L;
        try { val = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class); } catch (Exception ignored) {}
        if (val == null || val <= 0) val = 1L;
        try { jdbc.update("UPDATE job_sequence SET current = GREATEST(current, ?) WHERE seq_key = 1", val); } catch (Exception ignored) {}
        return val;
    }
}
