package com.sumo_management.Report;

import org.springframework.stereotype.Component;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

@Component
@ServiceName("ManagementService")
public class ReportHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReportHandler.class);
    private final PersistenceService db;

    public ReportHandler(PersistenceService db) {
        this.db = db;
        logger.info("ReportHandler Initialized...");
    }

    @After(event = CqnService.EVENT_READ, entity = "ManagementService.Projects")
    public void generateProjectReport(CdsReadEventContext context) {
        try {
            logger.info("Fetching project report...");
            CqnSelect query = context.getCqn().asSelect();
            Result result = db.run(query);
            logger.info("Database Query Executed: Rows fetched = " + result.list().size());

            if (result.list().isEmpty()) {
                logger.warn("Projects Not Found.");
                context.setResult(Collections.emptyList());
            } else {
                logger.info("Project Report Generated Successfully");
                context.setResult(result);
            }
        } catch (Exception e) {
            logger.error("Failed to generate project report: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate project report: " + e.getMessage(), e);
        }
    }

    @After(event = CqnService.EVENT_READ, entity = "ManagementService.Employees")
    public void generateEmployeeReport(CdsReadEventContext context) {
        try {
            logger.info("Fetching employee report...");
            CqnSelect query = context.getCqn().asSelect();
            Result result = db.run(query);
            logger.info("Database Query Executed: Rows fetched = " + result.list().size());

            if (result.list().isEmpty()) {
                logger.warn("Employees Not Found.");
                context.setResult(Collections.emptyList());
            } else {
                logger.info("Employee Report Generated Successfully");
                context.setResult(result);
            }
        } catch (Exception e) {
            logger.error("Failed to generate employee report: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate employee report: " + e.getMessage(), e);
        }
    }

    @After(event = CqnService.EVENT_READ, entity = "ManagementService.SubProjects")
    public void generateSubprojectReport(CdsReadEventContext context) {
        try {
            logger.info("Fetching subproject report...");
            CqnSelect query = context.getCqn().asSelect();
            Result result = db.run(query);
            logger.info("Database Query Executed: Rows fetched = " + result.list().size());

            if (result.list().isEmpty()) {
                logger.warn("SubProjects Not Found.");
                context.setResult(Collections.emptyList());
            } else {
                logger.info("SubProject Report Generated Successfully");
                context.setResult(result);
            }
        } catch (Exception e) {
            logger.error("Failed to generate subproject report: " + e.getMessage(), e);
            throw new RuntimeException("Failed to generate subproject report: " + e.getMessage(), e);
        }
    }
}
