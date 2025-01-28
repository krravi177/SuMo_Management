namespace sap.capire.sumo_management;
// using { Currency, cuid, managed } from '@sap/cds/common';

// For Projects entity
@cds.persistence.name: 'SAP_CAPIRE_SUMO_MANAGEMENT_PROJECTS'   // Explicitly setting the table name to 'PROJECTS'
entity Projects {
    key projectId    : Integer;
    projectName      : String;
    assignedToP      : Integer;    
    timeAssigned     : Integer;
    projectChanges   : Integer;
    clientName       : String;
    description      : String;
    urgency          : String;
    createdBy        : Integer;
    modifiedBy       : Integer;
    // cost             : Decimal; 
    startDate        : DateTime;  
    endDate          : DateTime;
    status           : String;
    createdByEmp     : Association to Employees on createdByEmp.empCode = createdBy;
    modifiedByEmp    : Association to Employees on modifiedByEmp.empCode = modifiedBy;
    subProjects      : Association to many SubProjects on subProjects.projectId = projectId;
    assignedToEmp    : Association to Employees on assignedToEmp.empCode = assignedToP;  
}

// For Employees entity
@cds.persistence.name: 'SAP_CAPIRE_SUMO_MANAGEMENT_SUBPROJECTS'      // Explicitly setting the table name to 'EMPLOYEES'
entity Employees {
    key empCode      : Integer;
    empName          : String;
    projectId        : Integer;
    rating           : Integer;
    managerId        : Integer;
    timeTaken        : Integer;
    permissionToEdit : String;
    permissionToView : String;
    permissionToCreateP : String;
    permissionToCreateSP : String;
    permissionToCreateE : String;
    projects         : Association to many Projects on projects.assignedToP = empCode;  
    empManager : Association to Employees on empManager.empCode = managerId;
    subProjects      : Association to many SubProjects on subProjects.assignedToE = empCode;  
}

// For SubProjects entity
@cds.persistence.name: 'SUBPROJECTS' // Explicitly setting the table name to 'SUBPROJECTS'
entity SubProjects {
    key moduleId     : Integer;
    projectId        : Integer;
    moduleName       : String;
    assignedToE      : Integer;   
    projectManagerId : Integer;
    timeAssigned     : Integer;
    moduleStatus     : String;
    createdBy        : Integer;
    modifiedBy       : Integer;
    startDate        : DateTime;  
    endDate          : DateTime;  
    status           : String;    
    project          : Association to Projects on project.projectId = projectId;
    createdByEmp     : Association to Employees on createdByEmp.empCode = createdBy;
    modifiedByEmp    : Association to Employees on modifiedByEmp.empCode = modifiedBy;
    assignedToEmp    : Association to Employees on assignedToEmp.empCode = assignedToE;  
}