package com.sumo_management.handler;

import org.springframework.stereotype.Component;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnPredicate;
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
import com.sap.cds.impl.builder.model.ComparisonPredicate;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@ServiceName("ManagementService")
public class ProjectHandler implements EventHandler {

    private final PersistenceService db;

    public ProjectHandler(PersistenceService db) {
        this.db = db;
    }

    @Before(event = CqnService.EVENT_CREATE, entity = "ManagementService.Projects")
    public void beforeCreateProject(CdsCreateEventContext context, CdsData projectData) {
        System.out.println("Before Create Handler executed!");

        try {
            int projectId = generateNewId("ManagementService.Projects", "projectId");
            projectData.put("projectId", projectId); 
            System.out.println("Generated project ID: " + projectId);
        } catch (Exception e) {
            System.err.println("Error generating project ID: " + e.getMessage());
            // e.printStackTrace();
            throw new RuntimeException("Error generating project ID", e);
        }
    }
    
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
            throw new RuntimeException("Error creating project: " + e.getMessage(), e);
        }
    }

    // // Update Method

    //     @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.Projects")
    //     public void updateProject(CdsUpdateEventContext context, CdsData projectData) {
    //     System.out.println("Update handler executed successfully!");

    //     try {
    //         System.out.println("Handler start");

    //         // Extract projectId from the request
    //         Integer projectId = (Integer) projectData.get("projectId");
    //         if (projectId == null) {
    //             throw new IllegalArgumentException("Project ID is required for update.");
    //         }

    //         // Execute the update query
    //         long updatedRows = db.run(Update.entity("ManagementService.Projects")
    //                 .data(projectData)
    //                 .where(c -> c.eq("projectId", projectId)));

    //         if (updatedRows > 0) {
    //             System.out.println("Project updated successfully with ID: " + projectId);
    //             context.setResult(Collections.singletonList(projectData));
    //         } else {
    //             throw new RuntimeException("Update failed. Project with ID " + projectId + " not found.");
    //         }

    //     } catch (Exception e) {
    //         System.err.println("Error updating project: " + e.getMessage());
    //         throw new RuntimeException("Error updating project: " + e.getMessage(), e);
    //     }
    // }

    // // Delete Method

    //     @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.Projects")
    //     public void deleteProject(CdsDeleteEventContext context, CdsData projectData) {
    //     System.out.println("Delete handler executed successfully!");
        
    //     try {
    //         System.out.println("Delete handler start");
    //         Integer projectId = (Integer) projectData.get("projectId");
            
    //         if (projectId == null) {
    //             throw new IllegalArgumentException("Project ID is required for deletion");
    //         }
            
    //         long deletedRows = db.run(Delete.from("ManagementService.Projects")
    //                             .matching(Conditions.eq("projectId", projectId)))
    //                         .rowCount();
            
    //         if (deletedRows > 0) {
    //             System.out.println("Project deleted successfully with ID: " + projectId);
    //         } else {
    //             System.out.println("No project found with ID: " + projectId);
    //             throw new RuntimeException("No project found with ID: " + projectId);
    //         }
            
    //     } catch (Exception e) {
    //         System.err.println("Error deleting project: " + e.getMessage());
    //         throw new RuntimeException("Error deleting project: " + e.getMessage(), e);
    //     }
    // }
    
    private int generateNewId(String tableName, String idColumn) {
        Optional<Integer> maxId = db.run(Select.from(tableName)
                .columns(idColumn)
                .orderBy(CQL.get(idColumn).desc()))
                .first()
                .map(row -> (Integer) row.get(idColumn));                
    
        return maxId.orElse(0) + 1;
    }
}
