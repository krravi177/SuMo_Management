package com.sumo_management.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;

import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.CdsData;

@Component
@ServiceName("ManagementService")
public class LeaveRequestHandler implements EventHandler {

    private final PersistenceService db;

    // Allowed Leave Types
    private static final List<String> VALID_LEAVE_TYPES = Arrays.asList("Sick", "Vacation", "Personal");
    private static final List<String> VALID_STATUSES = Arrays.asList("Pending", "Approved", "Rejected");

    public LeaveRequestHandler(PersistenceService db) {
        this.db = db;
    }

    @On(event = CqnService.EVENT_CREATE, entity = "ManagementService.LeaveRequest")
    public void createLeaveRequest(CdsCreateEventContext context, CdsData leaveRequestData) {
        try {
            // Auto-generate LeaveRequestId as a String
            String newLeaveRequestId = generateLeaveRequestId();
            System.out.println("Generated LeaveRequestId: " + newLeaveRequestId);

            leaveRequestData.put("leaveRequestId", newLeaveRequestId);

            // Validate Employee Existence
            Object empCodeObj = leaveRequestData.get("empCode");
            if (empCodeObj == null) {
                throw new RuntimeException("Employee Code (empCode) is required.");
            }
            int empCode = Integer.parseInt(empCodeObj.toString());

            Result empResult = db.run(Select.from("ManagementService.Employees")
                .where(e -> e.get("empCode").eq(empCode)));

            if (empResult == null || empResult.list().isEmpty()) {
                throw new RuntimeException("Invalid Employee Code: Employee does not exist.");
            }

            // Validate Leave Type
            Object leaveTypeObj = leaveRequestData.get("leaveType");
            if (leaveTypeObj == null || !VALID_LEAVE_TYPES.contains(leaveTypeObj.toString())) {
                throw new RuntimeException("Invalid Leave Type. Allowed values: " + VALID_LEAVE_TYPES);
            }

            // Validate Status (Default to "Pending" if not provided)
            Object statusObj = leaveRequestData.get("status");
            String status = (statusObj != null && VALID_STATUSES.contains(statusObj.toString())) ? statusObj.toString() : "Pending";
            leaveRequestData.put("status", status);

            // Insert the Leave Request
            db.run(Insert.into("ManagementService.LeaveRequest").entry(leaveRequestData));

            // Return the inserted record
            context.setResult(Collections.singletonList(leaveRequestData));

        } catch (Exception e) {
            System.err.println("Error creating Leave Request: " + e.getMessage());
            throw new RuntimeException("Error creating Leave Request: " + e.getMessage(), e);
        }
    }

    /**
     * DELETE Leave Request
     */
    @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.LeaveRequest")
    public void deleteLeaveRequest(CdsDeleteEventContext context) {
        try {
            // Extract LeaveRequestId as a String
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();
    
            if (!keys.containsKey("leaveRequestId")) {
                throw new RuntimeException("LeaveRequestId is missing in the request.");
            }
    
            String leaveRequestId = keys.get("leaveRequestId").toString();
            System.out.println("Deleting LeaveRequestId: " + leaveRequestId);
    
            // Validate if the Leave Request exists
            Result existingRequest = db.run(Select.from("ManagementService.LeaveRequest")
                .where(e -> e.get("leaveRequestId").eq(leaveRequestId)));
    
            if (existingRequest == null || existingRequest.list().isEmpty()) {
                throw new RuntimeException("Leave Request with ID " + leaveRequestId + " does not exist.");
            }
    
            // Perform the delete operation with explicit String matching
            Result result = db.run(Delete.from("ManagementService.LeaveRequest")
                .where(e -> e.get("leaveRequestId").eq(leaveRequestId)));
    
            // Check if deletion was actually performed
            if (result == null || result.rowCount() == 0) {
                throw new RuntimeException("Delete operation did not affect any rows. Possible data mismatch.");
            }
    
            System.out.println("Leave Request deleted successfully: " + leaveRequestId);
    
            // Notify CAP framework that delete was successful
            context.setResult(result);
    
        } catch (Exception e) {
            System.err.println("Error deleting Leave Request: " + e.getMessage());
            throw new RuntimeException("Failed to delete Leave Request. Reason: " + e.getMessage(), e);
        }
    }
    
    
    // AutoGenerating LeaveRequestId

    private String generateLeaveRequestId() {
        Result result = db.run(Select.from("ManagementService.LeaveRequest")
                .columns("leaveRequestId")
                .orderBy(CQL.get("leaveRequestId").desc())
                .limit(1));  // Get the last generated ID

        Optional<String> maxId = result.first().map(row -> row.get("leaveRequestId").toString());

        int nextId = 1;
        if (maxId.isPresent()) {
            String lastId = maxId.get();
            if (lastId.startsWith("LR-")) {
                try {
                    nextId = Integer.parseInt(lastId.substring(3)) + 1;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid LeaveRequestId format: " + lastId);
                }
            }
        }

        return "LR-" + nextId;
    }
}