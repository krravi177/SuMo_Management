package com.sumo_management.servive;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Insert;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.Result;
import com.sap.cds.Row;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
@ServiceName("ManagementService")
public class ProjectHandler implements EventHandler {

    private final PersistenceService db;

    public ProjectHandler(PersistenceService db) {
        this.db = db;
    }

    @On(event = "createProject")
    @Transactional
    public String createProject(Map<String, Object> projectData) {
        try {
            // Extract data from request payload
            String projectName = (String) projectData.get("projectName");
            Integer timeAssigned = (Integer) projectData.get("timeAssigned");
            Integer projectManagerId = (Integer) projectData.get("projectManagerId");
            Integer projectChanges = (Integer) projectData.get("projectChanges");
            String clientName = (String) projectData.get("clientName");
            String status = (String) projectData.get("status");
            String description = (String) projectData.get("description");
            String urgency = (String) projectData.get("urgency");
            Integer createdBy = (Integer) projectData.get("createdBy");
            Integer modifiedBy = (Integer) projectData.get("modifiedBy");

            // Generate a new project ID
            int projectId = generateNewProjectId();

            // Prepare the data for insertion
            Map<String, Object> newProject = new HashMap<>();
            newProject.put("projectId", projectId);
            newProject.put("projectName", projectName);
            newProject.put("timeAssigned", timeAssigned);
            newProject.put("projectManagerId", projectManagerId);
            newProject.put("projectChanges", projectChanges);
            newProject.put("clientName", clientName);
            newProject.put("status", status);
            newProject.put("description", description);
            newProject.put("urgency", urgency);
            newProject.put("createdBy", createdBy);
            newProject.put("modifiedBy", modifiedBy);

            // Insert data into database (Fix: Wrap in List.of())
            db.run(Insert.into("SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS").entries(List.of(newProject)));

            // Print message to console
            System.out.println("Project created successfully with ID: " + projectId);

            return "Project created successfully with ID: " + projectId;
        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage()); // Print error in console
            throw new RuntimeException("Error creating project: " + e.getMessage(), e);
        }
    }

    private int generateNewProjectId() {
        Result result = db.run(Select.from("SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS").columns("MAX(projectId) AS maxId"));
        Row row = result.first().orElse(null);
        
        // Fix: Use Object type casting
        Object maxIdObj = (row != null) ? row.get("maxId") : null;
        Integer maxId = (maxIdObj instanceof Integer) ? (Integer) maxIdObj : null;
        
        return (maxId != null) ? maxId + 1 : 1;
    }
}
