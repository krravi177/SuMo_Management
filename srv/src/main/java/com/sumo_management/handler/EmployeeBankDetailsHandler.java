package com.sumo_management.handler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.CdsData;
import com.sap.cds.Result;


import cds.gen.managementservice.EmployeeBankDetails;
import cds.gen.sumo_management.EmployeeBankDetails_;

@Component
@ServiceName("ManagementService")
public class EmployeeBankDetailsHandler implements EventHandler {

    private final PersistenceService db;

    public EmployeeBankDetailsHandler(PersistenceService db) {
        this.db = db;
    }

    // This is validateBankAccountUniqueness

    @Before(event = CqnService.EVENT_CREATE, entity = "ManagementService.EmployeeBankDetails")
    public void validateBankAccountUniqueness(List<EmployeeBankDetails> bankDetailsList) {
        try {
            System.out.println("Validating uniqueness for EmployeeBankDetails...");
    
            for (EmployeeBankDetails bankDetail : bankDetailsList) {
                String bankAcNo = bankDetail.getBankAcNo();
                Integer empCode = bankDetail.getEmpCode();
    
                if (bankAcNo == null || bankAcNo.trim().isEmpty()) {
                    throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Bank Account Number is required.");
                }
                if (empCode == null) {
                    throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Employee Code is required.");
                }
    
                CqnSelect checkDuplicate = Select.from(EmployeeBankDetails_.class)
                        .where(b -> b.BankAcNo().eq(bankAcNo)
                                .or(b.empCode().eq(empCode)));
    
                Optional<EmployeeBankDetails> existingRecord = db.run(checkDuplicate).first(EmployeeBankDetails.class);
    
                if (existingRecord.isPresent()) {
                    throw new ServiceException(ErrorStatuses.CONFLICT,
                            existingRecord.get().getBankAcNo().equals(bankAcNo) ?
                                    "Bank Account Number already exists: " + bankAcNo :
                                    "Employee already has a bank account: " + empCode);
                }
            }
            
            System.out.println("Uniqueness validation passed.");
    
        } catch (ServiceException se) {
            System.err.println("Validation error: " + se.getMessage());
            throw se;
        } catch (Exception e) {
            System.err.println("Unexpected error during validation: " + e.getMessage());
            throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Unexpected error during validation: " + e.getMessage());
        }
    }

    // This is updateEmployeeBankDetails
    
    @On(event = CqnService.EVENT_UPDATE, entity = "ManagementService.EmployeeBankDetails")
    public void updateEmployeeBankDetails(CdsUpdateEventContext context, CdsData bankDetailsData) {
        try {
            if (bankDetailsData == null || bankDetailsData.isEmpty()) {
                throw new RuntimeException("Bank details data is missing in the request.");
            }
    
            // Extract BankAcNo dynamically
            Object bankAcNoObj = bankDetailsData.get("BankAcNo");
            if (bankAcNoObj == null) {
                throw new RuntimeException("Bank Account Number (BankAcNo) is missing in the request.");
            }
    
            String bankAcNo = bankAcNoObj.toString().trim(); // Convert to String safely and remove spaces
            System.out.println("Updating Employee Bank Details for BankAcNo: " + bankAcNo);
    
            // Check if the record exists before updating
            Result existingDetails = db.run(Select.from("ManagementService.EmployeeBankDetails")
                .where(e -> e.get("BankAcNo").eq(bankAcNo)));
    
            System.out.println("Matching Bank Details for BankAcNo " + bankAcNo + ": " + existingDetails.list());
    
            if (existingDetails == null || existingDetails.list().isEmpty()) {
                throw new RuntimeException("No existing bank details found for BankAcNo: " + bankAcNo);
            }
    
            // Create Update statement
            CqnUpdate updateQuery = Update.entity("ManagementService.EmployeeBankDetails")
                .data(bankDetailsData)
                .where(e -> e.get("BankAcNo").eq(bankAcNo));
    
            // Execute update
            long updatedRows = db.run(updateQuery).rowCount();
    
            if (updatedRows == 0) {
                throw new RuntimeException("No records updated for BankAcNo: " + bankAcNo);
            }
    
            System.out.println("Employee Bank Details updated successfully for BankAcNo: " + bankAcNo);
    
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Employee Bank Details. Reason: " + e.getMessage(), e);
        }
    }
    
    
    // This is deleteEmployeeBankDetails

    @On(event = CqnService.EVENT_DELETE, entity = "ManagementService.EmployeeBankDetails")
    public void deleteEmployeeBankDetails(CdsDeleteEventContext context) {
        try {
            // Extract keys from the request
            CqnAnalyzer analyzer = CqnAnalyzer.create(context.getModel());
            Map<String, Object> keys = analyzer.analyze(context.getCqn()).targetKeys();
    
            System.out.println("Extracted Keys: " + keys); 
    
            if (!keys.containsKey("BankAcNo")) { 
                throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Bank Account Number is missing in the request.");
            }
    
            String bankAcNo = keys.get("BankAcNo").toString();
            System.out.println("Deleting Employee Bank Details for BankAcNo: " + bankAcNo);
    
            // Check if the record exists
            Result existingDetails = db.run(Select.from("ManagementService.EmployeeBankDetails")
                .where(e -> e.get("BankAcNo").eq(bankAcNo)));
    
            System.out.println("Existing Details Result: " + existingDetails.list()); 
    
            if (existingDetails == null || existingDetails.list().isEmpty()) {
                throw new ServiceException(ErrorStatuses.NOT_FOUND, "Bank details for Account No " + bankAcNo + " do not exist.");
            }
    
            // Perform the delete operation
            db.run(Delete.from("ManagementService.EmployeeBankDetails")
                .where(e -> e.get("BankAcNo").eq(bankAcNo)));
    
            System.out.println("Employee Bank Details deleted successfully for BankAcNo: " + bankAcNo);
    
            context.setResult(existingDetails);  
    
        } catch (Exception e) {
            throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Failed to delete Employee Bank Details. Reason: " + e.getMessage(), e);
        }
    }
    
    
}