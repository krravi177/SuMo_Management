package com.sumo_management.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cds.ql.Insert;
import com.sap.cds.services.persistence.PersistenceService;
import cds.gen.sap.capire.sumo_management.Employees;
import cds.gen.sap.capire.sumo_management.Employees_; // Make sure this import is included

@RestController
@RequestMapping("/management")
public class EmployeeController {

    @Autowired
    private PersistenceService db;

    // Endpoint to create a new employee
    @PostMapping("/employees")
    public void createEmployee(@RequestBody Employees employee) {
        // Call the backend logic that handles the creation of the employee
        onCreateEmployeeBackendLogic(employee);
    }

    private void onCreateEmployeeBackendLogic(Employees employee) {
        // Custom logic to be executed when an Employee is created
        System.out.println("Creating a new Employee: " + employee.getEmpName());

        // Validation for required fields
        if (employee.getEmpName() == null || employee.getEmpCode() == null) {
            throw new RuntimeException("Employee Name and Employee ID are required!");
        }

        // Insert the new employee into the database
        db.run(Insert.into(Employees_.class).entry(employee)); // Use Employees_.class here

        // Log success (Optional)
        System.out.println("Employee created successfully: " + employee.getEmpName());
    }
}
