package com.sumo_management.handler;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

@Component
@ServiceName("ManagementService")
public class EmployeesHandler implements EventHandler {

    private final PersistenceService db;

    public EmployeesHandler(PersistenceService db) {
        this.db = db;
    }

    // Before Create Employee: Generate a unique empCode
    // @Before(event = CqnService.EVENT_CREATE, entity = "ManagementService.Employees")
    // public void beforeCreateEmployee(CdsCreateEventContext context) {
    //     System.out.println("Before Create Handler executed!");

    //     try {
    //         int empCode = generateNewId("ManagementService.Employees", "empCode");
    //         context.getCqn().entries().forEach(entry -> entry.put("empCode", empCode));
    //         System.out.println("Generated Employee ID: " + empCode);
    //     } catch (Exception e) {
    //         System.err.println("Error generating Employee ID: " + e.getMessage());
    //         throw new RuntimeException("Error generating Employee ID", e);
    //     }
    // }

    // On Create Employee: Insert into DB
    @On(event = CqnService.EVENT_CREATE, entity = "ManagementService.Employees")
    public void createEmployee(CdsCreateEventContext context, CdsData employeeData) {
        System.out.println("Create Employee Handler executed!");

        try {
            int empCode = generateNewId("ManagementService.Employees", "empCode");
            employeeData.put("empCode", empCode);

            db.run(Insert.into("ManagementService.Employees").entry(employeeData));
            System.out.println("Employee inserted successfully with ID: " + empCode);

            // Return the inserted Employee
            context.setResult(Collections.singletonList(employeeData));

        } catch (Exception e) {
            System.err.println("Error creating Employee: " + e.getMessage());
            throw new RuntimeException("Error creating Employee: " + e.getMessage(), e);
        }
    }

    // On Update Employee: Handle Updates
    @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.Employees")
    public void updateEmployee(CdsUpdateEventContext context, CdsData employeeData) {
        try {
            if (employeeData == null || employeeData.isEmpty()) {
                throw new RuntimeException("Employee data is missing in the request.");
            }

            // Extract empCode dynamically
            Object empCodeObj = employeeData.get("empCode");
            if (empCodeObj == null) {
                throw new RuntimeException("Employee ID (empCode) is missing in the request.");
            }

            int empCode = Integer.parseInt(empCodeObj.toString()); // Converts safely

            // Creating an Update statement
            CqnUpdate updateQuery = Update.entity("ManagementService.Employees")
                                        .data(employeeData)
                                        .where(e -> e.get("empCode").eq(empCode)); // Use empCode, not projectId

            // Running the update
            long updatedRows = db.run(updateQuery).rowCount();

            if (updatedRows == 0) {
                throw new RuntimeException("No records updated for empCode: " + empCode);
            }

            System.out.println("Employee updated successfully: " + empCode);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update Employee. Reason: " + e.getMessage(), e);
        }
    }

    // On Delete Employee: Handle Deletions
    @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.Employees")
    public void deleteEmployee(CdsDeleteEventContext context) {
        try {
            // Extract keys from the request
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();

            if (!keys.containsKey("empCode")) {
                throw new RuntimeException("Employee ID is missing in the request.");
            }

            int empCode = Integer.parseInt(keys.get("empCode").toString());
            System.out.println("Deleting Employee: " + empCode);

            // Check if the Employee exists before deletion
            Result existingEmployee = db.run(Select.from("ManagementService.Employees")
                .where(e -> e.get("empCode").eq(empCode)));

            if (existingEmployee == null || existingEmployee.list().isEmpty()) {
                throw new RuntimeException("Employee with ID " + empCode + " does not exist.");
            }

            // Perform the delete operation
            CqnDelete cqnDelete = Delete.from("ManagementService.Employees")
                .where(e -> e.get("empCode").eq(empCode));            
            System.out.println("Executing delete: " + cqnDelete);

            Result result = db.run(cqnDelete);

            if (result == null) {
                throw new RuntimeException("Delete failed: No response received from the database.");
            }

            System.out.println("Employee deleted successfully: " + empCode);

            // Notify CAP that the delete operation was completed
            context.setResult(result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Employee. Reason: " + e.getMessage(), e);
        }
    }

    // Generate New ID for Employees
    private int generateNewId(String tableName, String idColumn) {
        Optional<Integer> maxId = db.run(Select.from(tableName)
                .columns(idColumn)
                .orderBy(CQL.get(idColumn).desc()))
                .first()
                .map(row -> (Integer) row.get(idColumn));

        return maxId.orElse(0) + 1;
    }
}
