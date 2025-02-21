package com.sumo_management.handler;

import org.springframework.stereotype.Component;
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
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.CdsData;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.Result;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
@ServiceName("ManagementService")
public class ProjectHandler implements EventHandler {
    
    private final PersistenceService db;

    public ProjectHandler(PersistenceService db) {
        this.db = db;
    }

    // @Before(event = CqnService.EVENT_CREATE, entity = "ManagementService.Projects")
    // public void beforeCreateProject(CdsCreateEventContext context, CdsData projectData) {
    //     System.out.println("Before Create Handler executed!");

    //     try {
    //         int projectId = generateNewId("ManagementService.Projects", "projectId");
    //         projectData.put("projectId", projectId); 
    //         System.out.println("Generated project ID: " + projectId);
    //     } catch (Exception e) {
    //         System.err.println("Error generating project ID: " + e.getMessage());
    //         // e.printStackTrace();
    //         throw new RuntimeException("Error generating project ID", e);
    //     }
    // }
    
    @On(event = CqnService.EVENT_CREATE,  entity ="ManagementService.Projects")
    public void createProject(CdsCreateEventContext context, CdsData projectData) {
        System.out.println("Handlers executed successfully!");

        try {
            System.out.println("Handler start");
            int projectId = generateNewId("ManagementService.Projects", "projectId");
            projectData.put("projectId", projectId); 

            db.run(Insert.into("ManagementService.Projects").entry(projectData));
            System.out.println("Project inserted successfully with ID: " + projectId);

            // Return the inserted project
            context.setResult(Collections.singletonList(projectData));

        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            // e.printStackTrace();
            // throw new RuntimeException("Error creating project: " + e.getMessage(), e);
            System.out.println("Project Not Found");
        }
    }

    // Update Method of Project
    @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.Projects")
    public void updateProject(CdsUpdateEventContext context, CdsData projectData) {
        try {
            if (projectData == null || projectData.isEmpty()) {
                throw new RuntimeException("Project data is missing in the request.");
            }
    
            // Extract projectId dynamically
            Object projectIdObj = projectData.get("projectId");
            if (projectIdObj == null) {
                throw new RuntimeException("Project ID is missing in the request.");
            }
    
            String projectId = String.valueOf(projectIdObj); // Converts Integer/String safely
    
            // Creating an Update statement
            CqnUpdate updateQuery = Update.entity("ManagementService.Projects")
                                        .data(projectData)
                                        .where(p -> p.get("projectId").eq(projectId));
    
            // Running the update
            long updatedRows = db.run(updateQuery).rowCount();
    
            if (updatedRows == 0) {
                throw new RuntimeException("No records updated for projectId: " + projectId);
            }
    
            System.out.println("Project updated successfully: " + projectId);
    
        } catch (Exception e) {
            throw new RuntimeException("Failed to update project. Reason: " + e.getMessage(), e);
        }
    }
    
    
    // Delete method of Project

        @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.Projects")
        public void deleteProject(CdsDeleteEventContext context) {
        try {
            // Extract keys from the request
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();

            if (!keys.containsKey("projectId")) {
                throw new RuntimeException("Project ID is missing in the request.");
            }

            String projectId = String.valueOf(keys.get("projectId"));
            System.out.println("Delete Project Suscessfully: " + projectId);

            // Check if the project exists before deletion
            Result existingProject = db.run(Select.from("ManagementService.Projects").where(p -> p.get("projectId").eq(projectId)));
            if (existingProject == null || existingProject.list().isEmpty()) {
                throw new RuntimeException("Project with ID " + projectId + " does not exist.");
            }

            // Perform the delete operation
            CqnDelete cqnDelete = Delete.from("ManagementService.Projects").where(p -> p.get("projectId").eq(projectId));
            System.out.println("Executing delete: " + cqnDelete);

            Result result = db.run(cqnDelete);

            if (result == null) {
                throw new RuntimeException("Delete failed: No response received from the database.");
            }

            System.out.println("Project deleted successfully: " + projectId);

            // Instead of `setCompleted()`, return the result so CAP knows the delete was handled
            context.setResult(result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete project. Reason: " + e.getMessage(), e);
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
