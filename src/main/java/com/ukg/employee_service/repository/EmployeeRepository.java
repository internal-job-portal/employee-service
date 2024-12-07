package com.ukg.employee_service.repository;

import com.ukg.employee_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeId(Long employeeId);
    Optional<Employee> findByEmail(String email);
}
