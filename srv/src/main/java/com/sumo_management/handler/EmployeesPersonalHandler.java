package com.sumo_management.handler;

import org.springframework.stereotype.Component;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.managementservice.EmployeesPersonal;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ServiceName("ManagementService")
public class EmployeesPersonalHandler implements EventHandler {

    private final PersistenceService db;

    public EmployeesPersonalHandler(PersistenceService db) {
        this.db = db;
    }

    /** CREATE Employee */
    @On(event = CqnService.EVENT_CREATE, entity = "ManagementService.EmployeesPersonal")
    public void createEmployee(CdsCreateEventContext context, EmployeesPersonal employee) {
        try {
            db.run(Insert.into("ManagementService.EmployeesPersonal").entry(employee));
            System.out.println("Employee record inserted successfully with Email: " + employee.getEmail());

            // Retrieve inserted record to return as response
            CqnSelect select = Select.from("ManagementService.EmployeesPersonal").where(e -> e.get("email").eq(employee.getEmail()));
            List<EmployeesPersonal> insertedRecord = db.run(select).listOf(EmployeesPersonal.class);
            
            if (!insertedRecord.isEmpty()) {
                context.setResult(insertedRecord);
            } else {
                System.err.println("Inserted Employee record not found in DB.");
                context.setResult(List.of());
            }
            context.setCompleted();
        } catch (Exception e) {
            System.err.println("Error creating Employee record: " + e.getMessage());
            e.printStackTrace();
            context.setCompleted();
        }
    }

    /** READ Employee */
    @On(event = CqnService.EVENT_READ, entity = "ManagementService.EmployeesPersonal")
    public void readEmployees(CdsReadEventContext context) {
        try {
            System.out.println("Fetch Sucessfully");
            Result result = db.run(Select.from("ManagementService.EmployeesPersonal"));
            context.setResult(result);
        } catch (Exception e) {
            System.err.println("Error reading Employee records: " + e.getMessage());
            e.printStackTrace();
            context.setResult(List.of());
        }
    }

    /** UPDATE Employee */
    
    @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.EmployeesPersonal")
    public void updateEmployeePersonal(CdsUpdateEventContext context, CdsData employeeData) {
        try {
            if (employeeData == null || employeeData.isEmpty()) {
                throw new RuntimeException("Update data is missing in the request.");
            }
    
            // Extract Email (Primary Key)
            Object emailObj = employeeData.get("email");
            if (emailObj == null) {
                throw new RuntimeException("Employee email is missing in the request.");
            }
    
            String email = emailObj.toString().trim();
            System.out.println("Updating Employee with email: " + email);
    
            // Fetch existing employee record
            Result existingEntry = db.run(Select.from("ManagementService.EmployeesPersonal")
                    .where(e -> e.get("email").eq(email)));
    
            if (existingEntry == null || existingEntry.list().isEmpty()) {
                throw new RuntimeException("No employee found with email: " + email);
            }
    
            // Get only the fields that need to be updated
            Map<String, Object> updatedData = new HashMap<>();
    
            if (employeeData.get("mobileNo") != null) {
                updatedData.put("mobileNo", employeeData.get("mobileNo"));
            }
            if (employeeData.get("designation") != null) {
                updatedData.put("designation", employeeData.get("designation"));
            }
    
            // Ensure we have data to update
            if (updatedData.isEmpty()) {
                throw new RuntimeException("No valid fields provided for update.");
            }
    
            // Create Update Query with Selected Fields
            CqnUpdate updateQuery = Update.entity("ManagementService.EmployeesPersonal")
                    .data(updatedData)
                    .where(e -> e.get("email").eq(email));
    
            // Execute Update
            long updatedRows = db.run(updateQuery).rowCount();
    
            if (updatedRows == 0) {
                throw new RuntimeException("No records updated for email: " + email);
            }
    
            System.out.println("Employee record updated successfully: " + email);
    
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Employee record. Reason: " + e.getMessage(), e);
        }
    }
    
    
    /** DELETE Employee */

    @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.EmployeesPersonal")
    public void deleteEmployee(CdsDeleteEventContext context) {
        try {
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();

            if (!keys.containsKey("email")) {
                throw new RuntimeException("Employee Email is missing in the request.");
            }

            String email = keys.get("email").toString();
            System.out.println("Deleting Employee with Email: " + email);

            // Check if the record exists before deletion
            Result existingEntry = db.run(Select.from("ManagementService.EmployeesPersonal")
                .where(e -> e.get("email").eq(email)));

            if (existingEntry == null || existingEntry.list().isEmpty()) {
                throw new RuntimeException("Employee record with Email " + email + " does not exist.");
            }

            CqnDelete cqnDelete = Delete.from("ManagementService.EmployeesPersonal")
                .where(e -> e.get("email").eq(email));

            Result result = db.run(cqnDelete);

            if (result == null) {
                throw new RuntimeException("Delete failed: No response received from the database.");
            }

            System.out.println("Employee record deleted successfully: " + email);
            context.setResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Employee record. Reason: " + e.getMessage(), e);
        }
    }
}
