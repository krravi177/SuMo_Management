package com.sumo_management.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import cds.gen.sap.capire.sumo_management.Employees;
import cds.gen.sap.capire.sumo_management.Employees_;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/management-service")
public class EmployeeReportController {

    @Autowired
    private PersistenceService db;

    // Endpoint to generate employee reports
    @GetMapping("/employees/report")
    public void generateEmployeeReport(@RequestParam Integer employeeCode, 
                                       @RequestParam Instant startDate, 
                                       @RequestParam Instant endDate) {
        // Logic to generate employee report
        generateEmployeeReportBackendLogic(employeeCode, startDate, endDate);
    }

    private void generateEmployeeReportBackendLogic(Integer employeeCode, 
                                                    Instant startDate, 
                                                    Instant endDate) {
        // Correct the field access using the field descriptors from Employees_
        CqnSelect select = Select.from(Employees_.CDS_NAME) // Correct entity name
                .where(e -> e.get(Employees_.EMP_CODE).eq(employeeCode)); // Correct field access

        List<Employees> employeeList = db.run(select).listOf(Employees.class);

        if (employeeList.isEmpty()) {
            throw new RuntimeException("No employees found for the specified criteria.");
        }

        employeeList.forEach(employee -> {
            System.out.println("Employee Code: " + employee.getEmpCode() +
                    ", Employee Name: " + employee.getEmpName() +
                    ", Rating: " + employee.getRating());
        });
    }
}
