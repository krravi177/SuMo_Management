package com.sumo_management.Report;

import org.springframework.stereotype.Component;

import com.sap.cds.Result;

import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

@Component
@ServiceName("ManagementService")
public class ReportHandler implements EventHandler {

    private final PersistenceService db;

    public ReportHandler(PersistenceService db) {
        this.db = db;
    }

    @On(event = CqnService.EVENT_READ, entity = "ManagementService.Projects")
    public void generateProjectReport(CdsReadEventContext context) {
        try {
            System.out.println("ProjectReport Generated Sucessfully");
            CqnSelect query = context.getCqn().asSelect();
            Result result = db.run(query);
            context.setResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate project report: " + e.getMessage(), e);
        }
    }

    @On(event = CqnService.EVENT_READ, entity = "ManagementService.Employees")
    public void generateEmployeeReport(CdsReadEventContext context) {
        try {
            System.out.println("EmployeeReport Generated Sucessfully");
            CqnSelect query = context.getCqn().asSelect();
            Result result = db.run(query);
            context.setResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate employee report: " + e.getMessage(), e);
        }
    }

    @On(event = CqnService.EVENT_READ, entity = "ManagementService.SubProjects")
    public void generateEmployeeSubprojectReport(CdsReadEventContext context) {
        try {
            System.out.println("SubProjectReport Generated Sucessfully");

            CqnSelect query = context.getCqn().asSelect();
            Result result = db.run(query);
            context.setResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate employee subproject report: " + e.getMessage(), e);
        }
    }
}
