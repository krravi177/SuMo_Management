package com.sumo_management.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.RemoteService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;

import cds.gen.sap.capire.sumo_management.Projects;
import cds.gen.sap.capire.sumo_management.Employees;
import cds.gen.sap.capire.sumo_management.Projects_;
import cds.gen.sap.capire.sumo_management.Employees_;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ServiceName("ManagementService")
public class Service implements EventHandler {

    @Autowired
    PersistenceService db;

    @Autowired
    @Qualifier("sap.capire.sumo_management")
    RemoteService remote;

    @On
    public List<String> generateProjectReport(@RequestParam String status, 
                                              @RequestParam String assignedToP, 
                                              @RequestParam Instant startDate, 
                                              @RequestParam Instant endDate) {
        final CqnSelect select = Select.from(Projects_.class)
                .where(p -> p.status().eq(status)
                        .and(p.assignedToEmp().empName().eq(assignedToP))
                        .and(p.startDate().ge(startDate))
                        .and(p.endDate().le(endDate)));

        List<Projects> projectList = db.run(select).listOf(Projects.class);

        if (projectList.isEmpty()) {
            throw new ServiceException(ErrorStatuses.NOT_FOUND, "No projects found for the specified criteria.");
        }

        return projectList.stream()
                .map(this::formatProjectDetails)
                .collect(Collectors.toList());
    }

    private String formatProjectDetails(Projects project) {
        return "Project ID: " + project.getProjectId() +
                ", Project Name: " + project.getProjectName() +
                ", Status: " + project.getStatus() +
                ", Start Date: " + project.getStartDate() +
                ", End Date: " + project.getEndDate();
    }
    

    @On
    public List<String> generateEmployeeReport(@RequestParam Integer employeeCode, 
                                               @RequestParam Instant startDate, 
                                               @RequestParam Instant endDate) {
        final CqnSelect select = Select.from(Employees_.class)
                .where(e -> e.empCode().eq(employeeCode));

        List<Employees> employeeList = db.run(select).listOf(Employees.class);

        if (employeeList.isEmpty()) {
            throw new ServiceException(ErrorStatuses.NOT_FOUND, "No employees found for the specified criteria.");
        }

        return employeeList.stream()
                .map(this::formatEmployeeDetails)
                .collect(Collectors.toList());
    }

    private String formatEmployeeDetails(Employees employee) {
        return "Employee Code: " + employee.getEmpCode() +
                ", Employee Name: " + employee.getEmpName() +
                ", Rating: " + employee.getRating() +
                ", Time Taken: " + employee.getTimeTaken();
    }

    @Before
    public void validateProjectReportParams(@RequestParam String status, 
                                             @RequestParam String assignedToP, 
                                             @RequestParam Instant startDate, 
                                             @RequestParam Instant endDate) {
        if (status == null || assignedToP == null || startDate == null || endDate == null) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Status, assignedToP, startDate, and endDate are required.");
        }
    }

    @Before
    public void validateEmployeeReportParams(@RequestParam Integer employeeCode, 
                                              @RequestParam Instant startDate, 
                                              @RequestParam Instant endDate) {
        if (employeeCode == null) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Employee code is required.");
        }
    }

    @On
    public String createProject(@RequestParam String projectId,  
                                @RequestParam String projectName, 
                                @RequestParam Integer timeAssigned, 
                                @RequestParam Integer projectManagerId, 
                                @RequestParam Integer projectChanges,
                                @RequestParam String clientName, 
                                @RequestParam String status, 
                                @RequestParam String description, 
                                @RequestParam String urgency,
                                @RequestParam Integer createdBy, 
                                @RequestParam Integer modifiedBy) {
        return "Project created successfully!";
    }

    @On
    public String createEmployee(@RequestParam String empName, 
                                 @RequestParam Integer empCode, 
                                 @RequestParam Integer projectId, 
                                 @RequestParam Integer rating,
                                 @RequestParam Integer timeTaken, 
                                 @RequestParam Integer managerId, 
                                 @RequestParam String permissionToEdit, 
                                 @RequestParam String permissionToView, 
                                 @RequestParam String permissionToCreateP, 
                                 @RequestParam String permissionToCreateSP, 
                                 @RequestParam String permissionToCreateE, 
                                 @RequestParam Integer createdBy, 
                                 @RequestParam Integer modifiedBy) {
        return "Employee created successfully!";
    }

    @After
    public void logProjectCreation(@RequestParam String projectName) {
        System.out.println("Project '" + projectName + "' created successfully.");
    }

    @After
    public void logEmployeeCreation(@RequestParam String empName) {
        System.out.println("Employee '" + empName + "' created successfully.");
    }
}
