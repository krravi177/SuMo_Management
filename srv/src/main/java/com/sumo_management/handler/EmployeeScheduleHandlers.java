package com.sumo_management.handler;

import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.stereotype.Component;
import cds.gen.managementservice.EmployeeSchedule;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.Result;

import java.util.List;
import java.util.Map;

@Component
@ServiceName("ManagementService")
public class EmployeeScheduleHandlers implements EventHandler {

    private final PersistenceService db;

    public EmployeeScheduleHandlers(PersistenceService db) {
        this.db = db;
    }

    /**
     * Create Employee Schedule Entry
     */
    @On(event = CqnService.EVENT_CREATE, entity = "ManagementService.EmployeeSchedule")
    public void createScheduleEntry(CdsCreateEventContext context, EmployeeSchedule schedule) {
        try {
            System.out.println("Received request to create Employee Schedule: " + schedule);
    
            // Insert into the database
            db.run(Insert.into("ManagementService.EmployeeSchedule").entry(schedule));
            System.out.println("Successfully inserted Employee Schedule with ID: " + schedule.getId());
    
            // Debugging: Check if ID exists
            if (schedule.getId() == null) {
                System.err.println("Schedule ID is null after insertion!");
            }
    
            // Fetch the inserted record
            CqnSelect select = Select.from("ManagementService.EmployeeSchedule").where(c -> c.get("ID").eq(schedule.getId()));
            System.out.println("Executing select query: " + select);
    
            List<EmployeeSchedule> insertedRecord = db.run(select).listOf(EmployeeSchedule.class);
            System.out.println("Fetched inserted record: " + insertedRecord);
    
            if (!insertedRecord.isEmpty()) {
                context.setResult(insertedRecord);
            } else {
                System.err.println("Inserted Employee Schedule not found in DB.");
                context.setResult(List.of());
            }
    
            context.setCompleted();
        } catch (Exception e) {
            System.err.println("Error creating Employee Schedule: " + e.getMessage());
            e.printStackTrace();
            context.setCompleted();
        }
    }
    

    /**
     * Get Employee Schedule by ID
     */
    @On(event = CqnService.EVENT_READ, entity = "ManagementService.EmployeeSchedule")
    public void getScheduleById(CdsReadEventContext context) {
        try {
            CqnSelect select = context.getCqn();
            List<EmployeeSchedule> result = db.run(select).listOf(EmployeeSchedule.class);
            System.out.println("Fetching schedule by ID: " + result);

            context.setResult(result);
            context.setCompleted();
        } catch (Exception e) {
            System.err.println("Error fetching Employee Schedule: " + e.getMessage());
            e.printStackTrace();
            context.setCompleted();
        }
    }

    /**
     * Update Employee Schedule
     */
    @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.EmployeeSchedule")
    public void updateSchedule(CdsUpdateEventContext context, EmployeeSchedule schedule) {
        try {
            System.out.println("Received request to update Employee Schedule: " + schedule);

            // Perform update in the database
            db.run(Update.entity("ManagementService.EmployeeSchedule").data(schedule).where(c -> c.get("ID").eq(schedule.getId())));
            System.out.println("Successfully updated Employee Schedule with ID: " + schedule.getId());

            // Fetch the updated record to return
            CqnSelect select = Select.from("ManagementService.EmployeeSchedule").where(c -> c.get("ID").eq(schedule.getId()));
            List<EmployeeSchedule> updatedRecord = db.run(select).listOf(EmployeeSchedule.class);

            if (!updatedRecord.isEmpty()) {
                System.out.println("Returning updated Employee Schedule: " + updatedRecord);
                context.setResult(updatedRecord);
            } else {
                System.out.println("Updated Employee Schedule not found in DB, returning empty result.");
                context.setResult(List.of());
            }

            context.setCompleted();
        } catch (Exception e) {
            System.err.println("Error updating Employee Schedule: " + e.getMessage());
            e.printStackTrace();
            context.setCompleted();
        }
    }

    /**
     * Delete Employee Schedule
     */

    @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.EmployeeSchedule")
        public void deleteSchedule(CdsDeleteEventContext context) {
            try {
                // Extract keys from the request
                CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
                Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();
    
                if (!keys.containsKey("ID")) {
                    throw new RuntimeException("Schedule ID is missing in the request.");
                }
    
                int scheduleId = Integer.parseInt(keys.get("ID").toString());
                System.out.println("Deleting Employee Schedule ID: " + scheduleId);
    
                // Check if the schedule exists before deletion
                Result existingSchedule = db.run(Select.from("ManagementService.EmployeeSchedule")
                    .where(e -> e.get("ID").eq(scheduleId)));
    
                if (existingSchedule == null || existingSchedule.list().isEmpty()) {
                    throw new RuntimeException("Employee Schedule with ID " + scheduleId + " does not exist.");
                }
    
                // Perform the delete operation
                CqnDelete cqnDelete = Delete.from("ManagementService.EmployeeSchedule")
                    .where(e -> e.get("ID").eq(scheduleId));
                System.out.println("Executing delete: " + cqnDelete);
    
                Result result = db.run(cqnDelete);
    
                if (result == null) {
                    throw new RuntimeException("Delete failed: No response received from the database.");
                }
    
                System.out.println("Employee Schedule deleted successfully: " + scheduleId);
    
                // Notify CAP that the delete operation was completed
                context.setResult(result);
    
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete Employee Schedule. Reason: " + e.getMessage(), e);
            }
        }
    
    
}
