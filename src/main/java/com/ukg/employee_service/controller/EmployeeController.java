package com.ukg.employee_service.controller;

import com.ukg.employee_service.dto.LoginRequest;
import com.ukg.employee_service.dto.UserDetailsDTO;
import com.ukg.employee_service.model.Employee;
import com.ukg.employee_service.repository.EmployeeRepository;
import com.ukg.employee_service.service.EmployeeService;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmployeeId(employeeId));
    }

    @PostMapping("/verify")
    public ResponseEntity<UserDetailsDTO> verifyCredentials(@RequestBody LoginRequest loginRequest) {
        try {
            UserDetails userDetails = employeeService.loadUserByUsername(loginRequest.getEmail());
            boolean isValid = passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword());
            
            if (isValid) {
                List<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

                Employee employee = employeeRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("HR not found with email: " + loginRequest.getEmail()));
                
                UserDetailsDTO dto = new UserDetailsDTO(employee.getEmployeeId(), loginRequest.getEmail(), true, authorities);
                return ResponseEntity.ok(dto);
            } else {
                UserDetailsDTO dto = new UserDetailsDTO(null, loginRequest.getEmail(), false, Collections.emptyList());
                return ResponseEntity.ok(dto);
            }
        } catch (UsernameNotFoundException e) {
            UserDetailsDTO dto = new UserDetailsDTO(null, loginRequest.getEmail(), false, Collections.emptyList());
            return ResponseEntity.ok(dto);
        }
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody Employee employeeDetails) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, employeeDetails));
    }
}
