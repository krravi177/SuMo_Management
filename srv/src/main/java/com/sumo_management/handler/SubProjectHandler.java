package com.sumo_management.handler;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
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
import com.sap.cds.Result;


@Component
@ServiceName("ManagementService")
public class SubProjectHandler implements EventHandler{

    private final PersistenceService db;

    public SubProjectHandler(PersistenceService db) {
        this.db = db;
    }

    //Create Method of SubSubProjects

    @Before(event = CqnService.EVENT_CREATE, entity = "ManagementService.SubSubProjects")
    public void beforeCreateProject(CdsCreateEventContext context, CdsData projectData) {
        System.out.println("Before Create Handler executed!");
        try {
            int projectId = generateNewId("ManagementService.SubSubProjects", "projectId");
            projectData.put("projectId", projectId);
            System.out.println("Generated project ID: " + projectId);
        } catch (Exception e) {
            System.err.println("Error generating project ID: " + e.getMessage());
            throw new RuntimeException("Error generating project ID", e);
        }
    }

    @On(event = CqnService.EVENT_CREATE, entity = "ManagementService.SubSubProjects")
    public void createProject(CdsCreateEventContext context, CdsData projectData) {
        System.out.println("Handlers executed successfully!");
        try {
            System.out.println("Handler start");
            int projectId = generateNewId("ManagementService.SubSubProjects", "projectId");
            projectData.put("projectId", projectId);

            db.run(Insert.into("ManagementService.SubSubProjects").entry(projectData));
            System.out.println("SubProject inserted successfully with ID: " + projectId);

            context.setResult(Collections.singletonList(projectData));
        } catch (Exception e) {
            System.err.println("Error creating SubSubProjects: " + e.getMessage());
            throw new RuntimeException("Error creating SubSubProjects: " + e.getMessage(), e);
        }
    }

    //Update method of SubProjects

    @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.SubProjects")
    public void updateSubProject(CdsUpdateEventContext context, CdsData projectData) {
        try {
            if (projectData == null || projectData.isEmpty()) {
                throw new RuntimeException("SubProject data is missing in the request.");
            }
    
            // Extract projectId dynamically
            Object projectIdObj = projectData.get("projectId");
            if (projectIdObj == null) {
                throw new RuntimeException("SubProject ID is missing in the request.");
            }
    
            String projectId = String.valueOf(projectIdObj); // Converts Integer/String safely
    
            // Creating an Update statement
            CqnUpdate updateQuery = Update.entity("ManagementService.SubProjects")
                                        .data(projectData)
                                        .where(p -> p.get("projectId").eq(projectId));
    
            // Running the update
            long updatedRows = db.run(updateQuery).rowCount();
    
            if (updatedRows == 0) {
                throw new RuntimeException("No records updated for SubProject ID: " + projectId);
            }
    
            System.out.println("SubProject updated successfully: " + projectId);
    
        } catch (Exception e) {
            throw new RuntimeException("Failed to update SubProject. Reason: " + e.getMessage(), e);
        }
    }
    
    
    //Delete Method of SubProjects

        @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.SubProjects")
        public void deleteSubProject(CdsDeleteEventContext context) {
        try {
            // Extract keys from the request
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();

            // Debugging logs
            System.out.println("Received delete request with keys: " + keys);

            // Use the correct key name from the request
            String subProjectKey = keys.containsKey("subProjectId") ? "subProjectId" :
                                keys.containsKey("moduleId") ? "moduleId" : null;

            if (subProjectKey == null) {
                throw new RuntimeException("SubProject ID is missing in the request.");
            }

            String subProjectId = String.valueOf(keys.get(subProjectKey));
            System.out.println("Deleting SubProject with ID: " + subProjectId);

            // Check if the SubProject exists before deletion
            Result existingSubProject = db.run(Select.from("ManagementService.SubProjects")
                .where(e -> e.get(subProjectKey).eq(subProjectId)));

            if (existingSubProject == null || existingSubProject.list().isEmpty()) {
                throw new RuntimeException("SubProject with ID " + subProjectId + " does not exist.");
            }

            // Perform the delete operation
            CqnDelete cqnDelete = Delete.from("ManagementService.SubProjects")
                .where(e -> e.get(subProjectKey).eq(subProjectId));

            System.out.println("Executing delete: " + cqnDelete);

            Result result = db.run(cqnDelete);

            if (result == null) {
                throw new RuntimeException("Delete failed: No response received from the database.");
            }

            System.out.println("SubProject deleted successfully: " + subProjectId);

            // Notify CAP that the delete operation was completed
            context.setResult(result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete SubProject. Reason: " + e.getMessage(), e);
        }
    }


    
    private int generateNewId(String tableName, String idColumn) {
        Optional<Integer> maxId = db.run(Select.from(tableName)
                .columns(idColumn)
                .orderBy(CQL.get(idColumn).desc()))
                .first()
                .map(row -> (Integer) row.get(idColumn));                
    
        return maxId.orElse(0) + 1;
    }

}
