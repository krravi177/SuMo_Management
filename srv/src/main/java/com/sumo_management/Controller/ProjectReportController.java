package com.sumo_management.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import cds.gen.managementservice.Projects;
import cds.gen.sap.capire.sumo_management.Projects_;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/management-service")
public class ProjectReportController {

    @Autowired
    private PersistenceService db;

    // Endpoint to generate a project report based on criteria
    @PostMapping("/generateProjectReport")
    public void generateProjectReport(@RequestBody ReportRequest request) {
        // Extract the criteria from the request
        String status = request.getStatus();
        String assignedToP = request.getAssignedToP();
        Instant startDate = request.getStartDate();
        Instant endDate = request.getEndDate();

        // Call the backend logic to fetch and filter the projects
        generateProjectReportBackendLogic(status, assignedToP, startDate, endDate);
    }

    private void generateProjectReportBackendLogic(String status, 
                                                   String assignedToP, 
                                                   Instant startDate, 
                                                   Instant endDate) {
        // Create query to fetch the project data
        CqnSelect select = Select.from(Projects_.class)
                .where(p -> p.status().eq(status)
                        .and(p.assignedToEmp().empCode().eq(Integer.parseInt(assignedToP)))
                        .and(p.startDate().ge(startDate))
                        .and(p.endDate().le(endDate)));

        List<Projects> projectList = db.run(select).listOf(Projects.class);

        if (projectList.isEmpty()) {
            throw new RuntimeException("No projects found for the specified criteria.");
        }

        projectList.forEach(project -> {
            System.out.println("Project ID: " + project.getProjectId() +
                    ", Project Name: " + project.getProjectName() +
                    ", Status: " + project.getStatus());
        });
    }

    // ReportRequest class to encapsulate the request body for the report generation
    public static class ReportRequest {
        private String status;
        private String assignedToP;
        private Instant startDate;
        private Instant endDate;
    
        // Getter and Setter for status
        public String getStatus() {
            return status;
        }
    
        public void setStatus(String status) {
            this.status = status;
        }
    
        // Getter and Setter for assignedToP
        public String getAssignedToP() {
            return assignedToP;
        }
    
        public void setAssignedToP(String assignedToP) {
            this.assignedToP = assignedToP;
        }
    
        // Getter and Setter for startDate
        public Instant getStartDate() {
            return startDate;
        }
    
        public void setStartDate(Instant startDate) {
            this.startDate = startDate;
        }
    
        // Getter and Setter for endDate
        public Instant getEndDate() {
            return endDate;
        }
    
        public void setEndDate(Instant endDate) {
            this.endDate = endDate;
        }
    }
    
}
