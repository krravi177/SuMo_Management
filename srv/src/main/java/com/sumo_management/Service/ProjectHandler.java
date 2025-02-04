package com.sumo_management.Service;
 
import com.sap.cds.services.cds.CdsCreateEventContext;
 
import org.springframework.stereotype.Component;
 
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.managementservice.Employees_;
import cds.gen.managementservice.ManagementService_;

 
@Component
@ServiceName(ManagementService_.CDS_NAME)
public class ProjectHandler implements EventHandler{
 
    @After(entity = Employees_.CDS_NAME)
    public void onCreateProject(final CdsCreateEventContext context) {
        System.out.println("Message printed successfully");
    }
}
 