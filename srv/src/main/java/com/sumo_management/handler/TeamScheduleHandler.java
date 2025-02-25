package com.sumo_management.handler;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.Delete;
import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.stereotype.Component;
import cds.gen.managementservice.TeamSchedule_;
import java.util.Collections;
import java.util.Map;

@Component
@ServiceName("ManagementService")
public class TeamScheduleHandler implements EventHandler {

    private final PersistenceService db;

    public TeamScheduleHandler(PersistenceService db) {
        this.db = db;
    }

    @On(event = CqnService.EVENT_CREATE, entity = "ManagementService.TeamSchedule")
    public void createTeamSchedule(CdsCreateEventContext context, CdsData teamScheduleData) {
        try {
            System.out.println("Create method Executing");
            db.run(Insert.into(TeamSchedule_.class).entry(teamScheduleData));
            context.setResult(Collections.singletonList(teamScheduleData));
        } catch (Exception e) {
            throw new RuntimeException("Error creating Team Schedule: " + e.getMessage(), e);
        }
    }

    @On(event = CqnService.EVENT_READ, entity = "ManagementService.TeamSchedule")
    public void getTeamSchedule(CdsReadEventContext context) {
        try {
            System.out.println("Data fetch Sucessfully");
    
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();
    
            Result result;
            if (keys.containsKey("ID")) {
                Integer id = (Integer) keys.get("ID");
                result = db.run(Select.from(TeamSchedule_.class).where(t -> t.ID().eq(id)));
            } else {
                result = db.run(Select.from(TeamSchedule_.class)); // Fetch all records
            }
    
            context.setResult(result.list());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching Team Schedule: " + e.getMessage(), e);
        }
    }
    

    @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.TeamSchedule")
    public void updateTeamSchedule(CdsUpdateEventContext context, CdsData teamScheduleData) {
        try {
            System.out.println("Update Scessfully");
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();
            
            db.run(Update.entity(TeamSchedule_.class).data(teamScheduleData));
            context.setResult(Collections.singletonList(teamScheduleData));
        } catch (Exception e) {
            throw new RuntimeException("Error updating Team Schedule: " + e.getMessage(), e);
        }
    }

    @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.TeamSchedule")
    public void deleteTeamSchedule(CdsDeleteEventContext context) {
        try {
            // Extract keys from the request
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();
    
            // System.out.println("Extracted Keys: " + keys);
    
            if (!keys.containsKey("ID")) {
                throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Team Schedule ID is missing in the request.");
            }
    
            Integer id;
            Object idObj = keys.get("ID");
            if (idObj instanceof Integer) {
                id = (Integer) idObj;
            } else {
                id = Integer.parseInt(idObj.toString());
            }
    
            // System.out.println("Deleting Team Schedule for ID: " + id);
    
            // Check if the record exists
            Result existingDetails = db.run(Select.from("ManagementService.TeamSchedule").where(t -> t.get("ID").eq(id)));
    
            // System.out.println("Existing Details Result: " + existingDetails.list());
    
            if (existingDetails == null || existingDetails.list().isEmpty()) {
                throw new ServiceException(ErrorStatuses.NOT_FOUND, "Team Schedule with ID " + id + " does not exist.");
            }
    
            // Perform the delete operation
            db.run(Delete.from("ManagementService.TeamSchedule").where(t -> t.get("ID").eq(id)));
    
            System.out.println("Team Schedule deleted successfully for ID: " + id);
    
            context.setResult(existingDetails);
    
        } catch (Exception e) {
            throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Failed to delete Team Schedule. Reason: " + e.getMessage(), e);
        }
    }
    
    
    
}