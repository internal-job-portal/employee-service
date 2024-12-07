package com.ukg.employee_service.util;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        if (isEmployeeTableEmpty()) {
            insertEmployeeData();
        }
    }

    private boolean isEmployeeTableEmpty() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employee", Integer.class);
        return count != null && count == 0;
    }

    private void insertEmployeeData() {
        String sql = "INSERT INTO employee (employee_id, first_name, last_name, email, password, date_of_birth) VALUES (?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql,
            List.of(
                new Object[]{1L, "Alice", "Johnson", "alice.johnson@ukg.com", "$2a$12$K0GUNeePaYEoegi.utr8ou0.8tzgVHWpKccAop6m2LpvNFnbeyC8q", LocalDate.of(1985, 3, 15)},
                new Object[]{2L, "Bob", "Smith", "bob.smith@ukg.com", "$2a$12$xShP9mVkJtJ5IoU2/EVAje/9A22NZ9JIdvRnHpOdAUUz954/XmY.i", LocalDate.of(1990, 7, 22)}
            )
        );
    }
}
