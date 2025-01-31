package com.sumo_management.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;


import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.reflect.CdsModel;
import java.time.Instant;
import java.util.List;
import cds.gen.sap.capire.sumo_management.Projects;
import cds.gen.sap.capire.sumo_management.Employees;
import cds.gen.sap.capire.sumo_management.Projects_;
import cds.gen.sap.capire.sumo_management.Employees_;

@Component
@ServiceName("ManagementService")
public class ManagementHandlers implements EventHandler {

    @Autowired
    PersistenceService db;

    private CqnAnalyzer analyzer;

    @Autowired
    public void Service(CdsModel model) {
        // Initialize CqnAnalyzer for analyzing queries
        this.analyzer = CqnAnalyzer.create(model);
    }

    /**
     * Handle project report generation with filtering and logic similar to ManageApplicationServiceHandler
     */
    @On(entity = Projects_.CDS_NAME)
    
    public void generateProjectReport(@RequestParam String status, 
                                      @RequestParam String assignedToP, 
                                      @RequestParam Instant startDate, 
                                      @RequestParam Instant endDate) {
        // Analyze the CQN to extract specific query details, e.g., project status, employee name
        CqnSelect select = Select.from(Projects_.class)
                .where(p -> p.status().eq(status)
                        .and(p.assignedToEmp().empName().eq(assignedToP))
                        .and(p.startDate().ge(startDate))
                        .and(p.endDate().le(endDate)));

        List<Projects> projectList = db.run(select).listOf(Projects.class);

        if (projectList.isEmpty()) {
            throw new ServiceException(ErrorStatuses.NOT_FOUND, "No projects found for the specified criteria.");
        }

        projectList.forEach(project -> {
            // Add any additional processing like formatting, etc.
            System.out.println(formatProjectDetails(project));
        });
    }

    private String formatProjectDetails(Projects project) {
        return "Project ID: " + project.getProjectId() +
                ", Project Name: " + project.getProjectName() +
                ", Status: " + project.getStatus() +
                ", Start Date: " + project.getStartDate() +
                ", End Date: " + project.getEndDate();
    }

    /**
     * Handle employee report generation with logic similar to ManageApplicationServiceHandler
     */
    @On(entity = Employees_.CDS_NAME)
    
    public void generateEmployeeReport(@RequestParam Integer employeeCode, 
                                       @RequestParam Instant startDate, 
                                       @RequestParam Instant endDate) {
        // Analyze the query to extract employee code or other relevant parameters
        CqnSelect select = Select.from(Employees_.class)
                .where(e -> e.empCode().eq(employeeCode));

        List<Employees> employeeList = db.run(select).listOf(Employees.class);

        if (employeeList.isEmpty()) {
            throw new ServiceException(ErrorStatuses.NOT_FOUND, "No employees found for the specified criteria.");
        }

        employeeList.forEach(employee -> {
            // Add any additional processing like formatting, etc.
            System.out.println(formatEmployeeDetails(employee));
        });
    }

    private String formatEmployeeDetails(Employees employee) {
        return "Employee Code: " + employee.getEmpCode() +
                ", Employee Name: " + employee.getEmpName() +
                ", Rating: " + employee.getRating() +
                ", Time Taken: " + employee.getTimeTaken();
    }

    /**
     * Before the project report generation, validate parameters
     */
    @Before(entity = Projects_.CDS_NAME)
    public void validateProjectReportParams(@RequestParam String status, 
                                             @RequestParam String assignedToP, 
                                             @RequestParam Instant startDate, 
                                             @RequestParam Instant endDate) {
        if (status == null || assignedToP == null || startDate == null || endDate == null) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Status, assignedToP, startDate, and endDate are required.");
        }
    }

    /**
     * Before employee report generation, validate parameters
     */
    @Before(entity = Employees_.CDS_NAME)
    public void validateEmployeeReportParams(@RequestParam Integer employeeCode, 
                                              @RequestParam Instant startDate, 
                                              @RequestParam Instant endDate) {
        if (employeeCode == null) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Employee code is required.");
        }
    }

    /**
     * Create a new project, similar to managing applications (if accepted/rejected logic)
     */
     
         @On(event = CqnService.EVENT_CREATE, entity = "Projects")
         public void createProject(Projects project) {
             // Check required fields (this is just an example)
             if (project.getProjectId() == null || project.getProjectName() == null) {
                 throw new RuntimeException("Project ID and Project Name are required!");
             }
         
             // Set default values for optional fields
             if (project.getStatus() == null) {
                 project.setStatus("Active");
             }
             if (project.getUrgency() == null) {
                 project.setUrgency("Normal");
             }
         
             // Insert the new project into the database using the updated line
             db.run(Insert.into(Projects_.class).entry(project));
         
             // Log success (Optional)
             System.out.println("Project created successfully: " + project.getProjectName());
         }         
    
     
    // @On(event = CqnService.EVENT_CREATE, entity = "Projects")
    // public void createProject(@RequestParam String projectId,  
    //                           @RequestParam String projectName, 
    //                           @RequestParam Integer timeAssigned, 
    //                           @RequestParam Integer projectManagerId, 
    //                           @RequestParam Integer projectChanges,
    //                           @RequestParam String clientName, 
    //                           @RequestParam String status, 
    //                           @RequestParam String description, 
    //                           @RequestParam String urgency,
    //                           @RequestParam Integer createdBy, 
    //                           @RequestParam Integer modifiedBy) {
    //     // Handle project creation logic using CqnAnalyzer
    //     Projects newProject = Projects.create();
    //     newProject.setProjectId(projectId);
    //     newProject.setProjectName(projectName);
    //     newProject.setTimeAssigned(timeAssigned);
    //     newProject.setProjectId(projectManagerId);
    //     newProject.setProjectChanges(projectChanges);
    //     newProject.setClientName(clientName);
    //     newProject.setStatus(status);
    //     newProject.setDescription(description);
    //     newProject.setUrgency(urgency);
    //     newProject.setCreatedBy(createdBy);
    //     newProject.setModifiedBy(modifiedBy);

    //     db.run(Insert.into(Projects_.class).entry(newProject));
    //     System.out.println(" created successfully!");
    // }

    /**
     * Create a new employee, similar to the create project logic
     */




     @On(event = CqnService.EVENT_CREATE, entity = "Employees")
     public void onCreateEmployee(Employees employee) {
         // Custom logic to be executed when an Employee is created
 
         // For example, log the employee's details or perform validations
         System.out.println("Creating a new Employee: " + employee.getEmpName());
         
         // You can also add custom validation or transformations
         if (employee.getEmpName() == null || employee.getEmpCode() == null) {
             throw new RuntimeException("Employee Name and Employee ID are required!");
         }
        }


    // @On(event = CqnService.EVENT_CREATE, entity = "Employees")
    // public void createEmployee(@RequestParam String empName, 
    //                            @RequestParam Integer empCode, 
    //                            @RequestParam Integer projectId, 
    //                            @RequestParam Integer rating,
    //                            @RequestParam Integer timeTaken, 
    //                            @RequestParam Integer managerId, 
    //                            @RequestParam String permissionToEdit, 
    //                            @RequestParam String permissionToView, 
    //                            @RequestParam String permissionToCreateP, 
    //                            @RequestParam String permissionToCreateSP, 
    //                            @RequestParam String permissionToCreateE, 
    //                            @RequestParam Integer createdBy, 
    //                            @RequestParam Integer modifiedBy) {
    //     // Handle employee creation logic using CqnAnalyzer
    //     Employees newEmployee = Employees.create();
    //     newEmployee.setEmpName(empName);
    //     newEmployee.setEmpCode(empCode);
    //     newEmployee.setProjectId(projectId);
    //     newEmployee.setRating(rating);
    //     newEmployee.setTimeTaken(timeTaken);
    //     newEmployee.setManagerId(managerId);
    //     newEmployee.setPermissionToEdit(permissionToEdit);
    //     newEmployee.setPermissionToView(permissionToView);
    //     newEmployee.setPermissionToCreateP(permissionToCreateP);
    //     newEmployee.setPermissionToCreateSP(permissionToCreateSP);
    //     newEmployee.setPermissionToCreateE(permissionToCreateE);
    //     newEmployee.setCreatedBy(createdBy);
    //     newEmployee.setModifiedBy(modifiedBy);

    //     db.run(Insert.into(Employees_.class).entry(newEmployee));
    //     System.out.println("Employee created successfully!");
    // }

    /**
     * After a project is created, log the success message
     */
    @After(entity = Projects_.CDS_NAME)
    public void logProjectCreation(@RequestParam String projectName) {
        System.out.println("Project '" + projectName + "' created successfully.");
    }

    /**
     * After an employee is created, log the success message
     */
    @After(entity = Employees_.CDS_NAME)
    public void logEmployeeCreation(@RequestParam String empName) {
        System.out.println("Employee '" + empName + "' created successfully.");
    }


    public PersistenceService getDb() {
        return db;
    }

    public void setDb(PersistenceService db) {
        this.db = db;
    }

    public CqnAnalyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(CqnAnalyzer analyzer) {
        this.analyzer = analyzer;
    }
}
