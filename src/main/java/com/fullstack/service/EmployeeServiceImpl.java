package com.fullstack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fullstack.model.Employee;
import com.fullstack.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements IEmployeeService{

    @Autowired
    private EmployeeRepository enEmployeeRepository;

    @Override
    public Employee save(Employee employee){
        return enEmployeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> findById(int empId){
        return enEmployeeRepository.findById(empId);
    }

    @Override
    public List<Employee> findAll(){
        return enEmployeeRepository.findAll();
    }

    @Override
    public Employee update(Employee employee){
        return enEmployeeRepository.save(employee);
    }

    @Override
    public void deleteById(int empId){
        enEmployeeRepository.deleteById(empId);
    }
}
