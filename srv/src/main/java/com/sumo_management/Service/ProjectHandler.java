package com.sumo_management.Service;

import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CqnService;
import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.annotations.On;

@Component
public class ProjectHandler {

    @On(event = CqnService.EVENT_CREATE , entity = "Service.Projects")
    public void onCreateProject(CdsCreateEventContext context) {
        System.out.println("Message printed successfully");

        // You can access request data like this:
        // Map<String, Object> data = context.getCqn().entries().get(0);
        // System.out.println("Received data: " + data);
    }
}
