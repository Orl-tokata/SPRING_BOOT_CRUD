package com.fullstack.service;

import java.util.List;
import java.util.Optional;

import com.fullstack.model.Employee;

public interface IEmployeeService {

    Employee save(Employee employee);

    Optional<Employee> findById(int empId);

    List<Employee> findAll();

    Employee update(Employee employee);

    void deleteById(int empId);
}
