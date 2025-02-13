package com.sumo_management.handler;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

@Component
@ServiceName("ManagementService")
public class EmployeeHandler implements EventHandler {

    private final PersistenceService db;

    public EmployeeHandler(PersistenceService db) {
        this.db = db;
    }

    @Before(event = CqnService.EVENT_CREATE, entity = "ManagementService.Employees")
    public void beforeCreateProject(CdsCreateEventContext context, CdsData projectData) {
        System.out.println("Before Create Handler executed!");

        try {
            int projectId = generateNewId("ManagementService.Employees", "projectId");
            projectData.put("projectId", projectId); 
            System.out.println("Generated project ID: " + projectId);
        } catch (Exception e) {
            System.err.println("Error generating project ID: " + e.getMessage());
            // e.printStackTrace();
            throw new RuntimeException("Error generating project ID", e);
        }
    }

    @On(event = CqnService.EVENT_CREATE,  entity ="ManagementService.Employees")
    public void createProject(CdsCreateEventContext context, CdsData projectData) {
        System.out.println("Handlers executed successfully!");

        try {
            System.out.println("Handler start");
            int projectId = generateNewId("ManagementService.Employee", "projectId");
            projectData.put("projectId", projectId); 

            db.run(Insert.into("ManagementService.Employee").entry(projectData));
            System.out.println("Project inserted successfully with ID: " + projectId);

            // Return the inserted project
            context.setResult(Collections.singletonList(projectData));

        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            // e.printStackTrace();
            throw new RuntimeException("Error creating project: " + e.getMessage(), e);
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
