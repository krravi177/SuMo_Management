namespace sumo_management;
using { managed } from '@sap/cds/common';
 
// Projects Entity
entity Projects {
    key projectId      : Integer;
    projectName        : String;
    assignedToP        : Integer;
    timeAssigned       : Integer;
    projectChanges     : Integer;
    clientName         : String;
    description        : String;
    urgency            : String @assert.range enum { HIGH; MEDIUM; LOW;};
    createdBy          : Integer;
    modifiedBy         : Integer;
    startDate          : DateTime;  
    endDate            : DateTime;
    status             : String @assert.range enum { Not_Started_Yet @title: 'Not Started Yet'; Running @title: 'Running'; Completed @title: 'Completed'; Late_Running @title: 'Late Running'; };
    createdByEmp       : Association to Employees on createdByEmp.empCode = createdBy;
    modifiedByEmp      : Association to Employees on modifiedByEmp.empCode = modifiedBy;
    subProjects        : Association to many SubProjects on subProjects.projectId = projectId;
    assignedToEmp      : Association to Employees on assignedToEmp.empCode = assignedToP;  
}
 
// Employees Entity
entity Employees {
    key empCode       : Integer;
    empName           : String;
    projectId         : Integer;
    managerId         : Integer;
    timeTaken         : Integer;
    role              : String @assert.range enum { Admin ; projectManager @title: 'Project Manager'; Employee ; };
    projects          : Association to many Projects on projects.assignedToP = empCode;  
    empManager        : Association to Employees on empManager.empCode = managerId;
    subProjects       : Association to many SubProjects on subProjects.assignedToE = empCode;  
}
 
// SubProjects Entity
entity SubProjects {
    key moduleId       : Integer;
    projectId          : Integer;
    moduleName         : String;
    assignedToE        : Integer;  
    projectManagerId   : Integer;
    timeAssigned       : Integer;
    moduleStatus       : String @assert.range enum { Not_Started_Yet @title: 'Not Started Yet'; Running @title: 'Running'; Completed @title: 'Completed'; Late_Running @title: 'Late Running'; };
    createdBy          : Integer;
    modifiedBy         : Integer;
    startDate          : DateTime;  
    endDate            : DateTime;  
    project            : Association to Projects on project.projectId = projectId;
    createdByEmp       : Association to Employees on createdByEmp.empCode = createdBy;
    modifiedByEmp      : Association to Employees on modifiedByEmp.empCode = modifiedBy;
    assignedToEmp      : Association to Employees on assignedToEmp.empCode = assignedToE;  
}
 
// Employee Bank Details Entity
entity EmployeeBankDetails {
    key BankAcNo       : String;
    ifscCode           : String;
    bankName           : String;
    accountType        : String @assert.range enum { Saving; Current};
    empCode            : Integer;
    bankAcName         : String;
    empAssoc           : Association to Employees on empAssoc.empCode = empCode;
}
 
// Employee Schedule (Personal Events)
entity EmployeeSchedule : managed {
    key ID            : Integer; // Integer ID to uniquely identify each schedule event
    empCode           : Integer;
    date              : Date;
    startDateTime     : DateTime;
    endDateTime       : DateTime;
    type              : String(20) @assert.range enum { Meeting; Work; Break; };  
    description       : String(255);
    empAssoc          : Association to Employees on empAssoc.empCode = empCode;
}
 
// Team Schedule (All Employees' Events)
entity TeamSchedule : managed {
    key ID             : Integer;
    projectId         : Integer;
    empCode             : Integer;
    startDateTime     : DateTime;
    endDateTime       : DateTime;
    type              : String(20) @assert.range enum { Meeting; Deadline; Work; }; 
    remarks           : String(255);
    projectAssoc      : Association to Projects on projectAssoc.projectId = projectId;
    empAssoc          : Association to Employees on empAssoc.empCode = empCode;
}
 
// LeaveRequest Entity
entity LeaveRequest {
    key leaveRequestId  : String;    
    empCode             : Integer;    
    leaveType           : String(50) @assert.range enum { Sick; Vacation; Personal; };  // Type of leave (e.g., "Sick", "Vacation", "Personal")
    startDate           : DateTime;   
    endDate             : DateTime;   
    reason              : String(255); 
    status              : String(20) @assert.range enum { Pending; Approved; Rejected; };  // Leave status (e.g., "Pending", "Approved", "Rejected")
    appliedAt           : DateTime;   
    approvedBy          : Integer;    
    approvalDate        : DateTime;   
    emp                 : Association to Employees on emp.empCode = empCode;
    approvedByEmp       : Association to Employees on approvedByEmp.empCode = approvedBy;
}
 
// ApprovalInbox Entity
entity ApprovalInbox {
    key inboxEntryId     : Integer;   
    leaveRequestId       : Integer;   
    managerEmpCode       : Integer;   
    requestStatus        : String(20) @assert.range enum { Pending; Approved; Rejected}; 
    requestDate          : DateTime;  
    responseDate         : DateTime;  
    response             : String(255) @assert.range enum { Approved ; Rejected}; 
    leaveRequest         : Association to LeaveRequest on leaveRequest.leaveRequestId = leaveRequestId;
    managerEmp           : Association to Employees on managerEmp.empCode = managerEmpCode;
}
 
entity EmployeesAuthentication {
    key id           : String;
    password        : String;
    empCode        : Integer;
    
    empCodeAssoc   : Association to Employees on empCodeAssoc.empCode = empCode;
}

entity EmployeesPersonal {
    key email         : String;
    empCode         : Integer;
    mobileNo       : String(10);
    DOB            : DateTime;
    gender         : String(10) @assert.range enum{Male; Female}; 
    baseLocation   : String;
    designation    : String;
    DOJ            : DateTime;
    fatherName     : String;
    Address        : String;
    UANno          : Integer;
    PANno          : String;
    empAssoc       : Association to Employees on empAssoc.empCode = empCode;
}

 