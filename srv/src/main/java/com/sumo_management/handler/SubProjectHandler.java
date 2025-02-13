package com.sumo_management.handler;

import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

@Component
@ServiceName("ManagementService")
public class SubProjectHandler implements EventHandler{

    private final PersistenceService db;

    public SubProjectHandler(PersistenceService db) {
        this.db = db;
    }

}
