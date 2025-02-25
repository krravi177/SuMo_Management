package com.sumo_management.handler;

import org.springframework.stereotype.Component;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.managementservice.ApprovalInbox;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import java.util.List;
import java.util.Map;

@Component
@ServiceName("ManagementService")
public class ApprovalInboxHandler implements EventHandler {

    private final PersistenceService db;

    public ApprovalInboxHandler(PersistenceService db) {
        this.db = db;
    }

    @On(event = CqnService.EVENT_CREATE, entity = "ManagementService.ApprovalInbox")
    public void createApprovalInbox(CdsCreateEventContext context, ApprovalInbox inboxEntry) {
        try {
            // System.out.println("Received request to create Approval Inbox Entry: " + inboxEntry);
            db.run(Insert.into("ManagementService.ApprovalInbox").entry(inboxEntry));
            System.out.println("Successfully inserted Approval Inbox Entry with ID: " + inboxEntry.getInboxEntryId());

            CqnSelect select = Select.from("ManagementService.ApprovalInbox").where(c -> c.get("inboxEntryId").eq(inboxEntry.getInboxEntryId()));
            List<ApprovalInbox> insertedRecord = db.run(select).listOf(ApprovalInbox.class);
            if (!insertedRecord.isEmpty()) {
                context.setResult(insertedRecord);
            } else {
                System.err.println("Inserted Approval Inbox Entry not found in DB.");
                context.setResult(List.of());
            }
            context.setCompleted();
        } catch (Exception e) {
            System.err.println("Error creating Approval Inbox Entry: " + e.getMessage());
            e.printStackTrace();
            context.setCompleted();
        }
    }

    @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.ApprovalInbox")
    public void deleteApprovalInbox(CdsDeleteEventContext context) {
        try {
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();

            if (!keys.containsKey("inboxEntryId")) {
                throw new RuntimeException("Approval Inbox Entry ID is missing in the request.");
            }

            int inboxEntryId = Integer.parseInt(keys.get("inboxEntryId").toString());
            System.out.println("Deleting Approval Inbox Entry ID: " + inboxEntryId);

            Result existingEntry = db.run(Select.from("ManagementService.ApprovalInbox")
                .where(e -> e.get("inboxEntryId").eq(inboxEntryId)));

            if (existingEntry == null || existingEntry.list().isEmpty()) {
                throw new RuntimeException("Approval Inbox Entry with ID " + inboxEntryId + " does not exist.");
            }

            CqnDelete cqnDelete = Delete.from("ManagementService.ApprovalInbox")
                .where(e -> e.get("inboxEntryId").eq(inboxEntryId));
            // System.out.println("Executing delete: " + cqnDelete);

            Result result = db.run(cqnDelete);

            if (result == null) {
                throw new RuntimeException("Delete failed: No response received from the database.");
            }

            System.out.println("Approval Inbox Entry deleted successfully: " + inboxEntryId);
            context.setResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Approval Inbox Entry. Reason: " + e.getMessage(), e);
        }
    }
}
