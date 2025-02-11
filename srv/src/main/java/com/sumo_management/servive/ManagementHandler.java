package com.sumo_management.servive;


import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Update;
import com.sap.cds.reflect.CdsService;
import com.sap.cds.ql.Delete;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.Result;
import com.sap.cds.Row;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;


@Component
@ServiceName("ManagementService.CDS_NAME")

public class ManagementHandler implements EventHandler {

    private final PersistenceService db;

    public ManagementHandler(PersistenceService db) {
        this.db = db;
        System.out.println("ManagementHandler Initialized");

    }

    // Project Operations

    @On( entity = "SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS")
    @Transactional
    public String createProject(Map<String, Object> projectData) {
        System.out.println("Inside createProject method");

        try {
            int projectId = generateNewId("SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS", "projectId");
            projectData.put("projectId", projectId);
            db.run(Insert.into("SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS").entries(List.of(projectData)));
            System.out.println(" Project inserted successfully with ID: " + projectId);

            return "Project created successfully with ID: " + projectId;
        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());

            throw new RuntimeException("Error creating project: " + e.getMessage(), e);
        }
    }

    @On(event = "updateProject")
    @Transactional
    public String updateProject(Map<String, Object> projectData) {
        try {
            db.run(Update.entity("SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS").data(projectData));
            return "Project updated successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Error updating project: " + e.getMessage(), e);
        }
    }

    @On(event = "deleteProject")
    @Transactional
    public Boolean deleteProject(Map<String, Object> request) {
        try {
            int projectId = (Integer) request.get("projectId");
            db.run(Delete.from("SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS").where(p -> p.get("projectId").eq(projectId)));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting project: " + e.getMessage(), e);
        }
    }


    @SuppressWarnings("unchecked")
    @On(event = "createProjectReport")
    @Transactional
    public List<Map<String, Object>> createProjectReport(Map<String, Object> request) {
    try {
        String status = (String) request.get("status");
        int assignedToP = (Integer) request.get("assignedToP");
        String startDate = (String) request.get("startDate");
        String endDate = (String) request.get("endDate");

        List<Map<String, Object>> result = db.run(Select.from("SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS")
                .where(p -> p.get("status").eq(status)
                        .and(p.get("projectManagerId").eq(assignedToP))
                        .and(p.get("createdAt").between(startDate, endDate))))
                .listOf(HashMap.class)
                .stream().map(map -> (Map<String, Object>) map)
                .toList();

        return result;
    } catch (Exception e) {
        throw new RuntimeException("Error generating project report: " + e.getMessage(), e);
    }
    }



    // Employee Operations


    @On(event = "createEmployee")
    @Transactional
    public String createEmployee(Map<String, Object> employeeData) {
        try {
            int empCode = generateNewId("SAP_CAPIRE_SUMO_MANAGEMENT_EMPLOYEES", "empCode");
            employeeData.put("empCode", empCode);
            db.run(Insert.into("SAP_CAPIRE_SUMO_MANAGEMENT_EMPLOYEES").entries(List.of(employeeData)));
            return "Employee created successfully with ID: " + empCode;
        } catch (Exception e) {
            throw new RuntimeException("Error creating employee: " + e.getMessage(), e);
        }
    }

    @On(event = "updateEmployee")
    @Transactional
    public String updateEmployee(Map<String, Object> employeeData) {
        try {
            db.run(Update.entity("SAP_CAPIRE_SUMO_MANAGEMENT_EMPLOYEES").data(employeeData));
            return "Employee updated successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Error updating employee: " + e.getMessage(), e);
        }
    }

    @On(event = "deleteEmployee")
    @Transactional
    public Boolean deleteEmployee(Map<String, Object> request) {
        try {
            int empCode = (Integer) request.get("empCode");
            db.run(Delete.from("SAP_CAPIRE_SUMO_MANAGEMENT_EMPLOYEES").where(e -> e.get("empCode").eq(empCode)));            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting employee: " + e.getMessage(), e);
        }
    }



    @SuppressWarnings("unchecked")
    @On(event = "generateEmployeeReport")
    @Transactional
    public List<Map<String, Object>> generateEmployeeReport(Map<String, Object> request) {
    try {
        int employeeCode = (Integer) request.get("employeeCode");
        String startDate = (String) request.get("startDate");
        String endDate = (String) request.get("endDate");

        List<Map<String, Object>> result = db.run(Select.from("SAP_CAPIRE_SUMO_MANAGEMENT_EMPLOYEES")
                .where(e -> e.get("empCode").eq(employeeCode)
                        .and(e.get("createdAt").between(startDate, endDate))))
                .listOf(HashMap.class)
                .stream().map(map -> (Map<String, Object>) map)
                .toList();

        return result;
    } catch (Exception e) {
        throw new RuntimeException("Error generating employee report: " + e.getMessage(), e);
    }
    }


    // SubProject Operations
    
    @On(event = "createSubProject")
    @Transactional
    public String createSubProject(Map<String, Object> subProjectData) {
        try {
            int moduleId = generateNewId("SAP_CAPIRE_SUMO_MANAGEMENT_SUBPROJECTS", "moduleId");
            subProjectData.put("moduleId", moduleId);
            db.run(Insert.into("SAP_CAPIRE_SUMO_MANAGEMENT_SUBPROJECTS").entries(List.of(subProjectData)));
            return "SubProject created successfully with ID: " + moduleId;
        } catch (Exception e) {
            throw new RuntimeException("Error creating sub-project: " + e.getMessage(), e);
        }
    }

    @On(event = "updateSubProject")
    @Transactional
    public String updateSubProject(Map<String, Object> subProjectData) {
        try {
            db.run(Update.entity("SAP_CAPIRE_SUMO_MANAGEMENT_SUBPROJECTS").data(subProjectData));
            return "SubProject updated successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Error updating sub-project: " + e.getMessage(), e);
        }
    }

    @On(event = "deleteSubProject")
    @Transactional
    public Boolean deleteSubProject(Map<String, Object> request) {
        try {
            int moduleId = (Integer) request.get("moduleId");
            db.run(Delete.from("SAP_CAPIRE_SUMO_MANAGEMENT_SUBPROJECTS").where(s -> s.get("moduleId").eq(moduleId)));            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting sub-project: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @On(event = "generateSubProjectReport")
    @Transactional
    public List<Map<String, Object>> generateSubProjectReport(Map<String, Object> request) {
    try {
        int projectId = (Integer) request.get("projectId");
        String status = (String) request.get("status");
        String startDate = (String) request.get("startDate");
        String endDate = (String) request.get("endDate");

        List<Map<String, Object>> result = db.run(Select.from("SAP_CAPIRE_SUMO_MANAGEMENT_SUBPROJECTS")
                .where(sp -> sp.get("projectId").eq(projectId)
                        .and(sp.get("status").eq(status))
                        .and(sp.get("startDate").between(startDate, endDate))))
                .listOf(HashMap.class)
                .stream().map(map -> (Map<String, Object>) map)
                .toList();

        return result;
    } catch (Exception e) {
        throw new RuntimeException("Error generating sub-project report: " + e.getMessage(), e);
    }
    }


    // ------------------ Utility Methods ------------------
    private int generateNewId(String tableName, String columnName) {
        Result result = db.run(Select.from(tableName).columns("MAX(" + columnName + ") AS maxId"));
        Optional<Row> row = result.first();
        return row.map(r -> r.get(columnName)).map(Object::toString).map(Integer::parseInt).orElse(0) + 1;
    }
}
