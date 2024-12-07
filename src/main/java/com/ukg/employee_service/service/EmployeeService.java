package com.ukg.employee_service.service;

import com.ukg.employee_service.model.Employee;
import com.ukg.employee_service.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService implements UserDetailsService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Employee getEmployeeByEmployeeId(Long employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + email));
        return new User(employee.getEmail(), employee.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long employeeId, Employee employeeDetails) {
        Employee employee = getEmployeeByEmployeeId(employeeId);
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        return employeeRepository.save(employee);
    }
}
