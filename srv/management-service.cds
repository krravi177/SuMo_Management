using { sap.capire.sumo_management as db } from '../db/schema';
 
service ManagementService {
 
    // action to create a new project
    action createProject(
        projectName        : String,
        timeAssigned       : Integer,
        projectManagerId   : String,
        projectChanges     : Integer,
        clientName         : String,
        status             : String,
        description        : String,
        urgency            : String,
        createdBy          : Integer,
        modifiedBy         : Integer
    ) returns String;
 
    // action to update an existing project
    action updateProject(
        projectId          : Integer,
        projectName        : String,
        timeAssigned       : Integer,
        projectManagerId   : Integer,
        projectChanges     : Integer,
        clientName         : String,
        status             : String,
        description        : String,
        urgency            : String,
        createdBy          : Integer,
        modifiedBy         : Integer
    ) returns String;
 
    action deleteProject(projectId: Integer) returns Boolean;
 
    action assignProjectManager(projectId: Integer, managerId: Integer) returns String;
    action calculateProjectCost(projectId: Integer) returns Decimal;

 
 
    // action to create a new employee
    action createEmployee(
        empName            : String,
        empCode            : Integer,
        projectId          : Integer,
        rating             : Integer,
        timeTaken          : Integer,
        managerId          : Integer,
        permissionToEdit   : String,
        permissionToView   : String,
        permissionToCreateP: String,
        permissionToCreateSP: String,
        permissionToCreateE: String,
        createdBy          : Integer,
        modifiedBy         : Integer
    ) returns String;
 
    // action to update employee details
    action updateEmployee(
        empCode            : Integer,
        empName            : String,
        projectId          : Integer,
        rating             : Integer,
        timeTaken          : Integer,
        managerId          : Integer,
        permissionToEdit   : String,
        permissionToView   : String,
        permissionToCreateP: String,
        permissionToCreateSP: String,
        permissionToCreateE: String,
        createdAt          : Timestamp,
        createdBy          : Integer,
        modifiedAt         : Timestamp,
        modifiedBy         : Integer
    ) returns String;
 
    action deleteEmployee(empCode: Integer) returns Boolean;
 

 
 
    // action to create a new sub-project
    action createSubProject(
        projectId          : Integer,
        moduleId           : Integer,
        moduleName         : String,
        description        : String,
        status             : String,
        startDate          : DateTime,
        endDate            : DateTime,
        assignedTo         : Integer,  
        createdBy          : Integer,
        modifiedBy         : Integer
    ) returns String;
 
    // action to update an existing sub-project
    action updateSubProject(
        moduleId           : Integer,
        projectId          : Integer,
        moduleName         : String,
        description        : String,
        status             : String,
        startDate          : DateTime,
        endDate            : DateTime,
        assignedTo         : Integer,  
        createdAt          : Timestamp,
        createdBy          : Integer,
        modifiedAt         : Timestamp,
        modifiedBy         : Integer
    ) returns String;
 
    action deleteSubProject(moduleId: Integer) returns Boolean;
    action updateSubProjectStatus(
        moduleId           : Integer,
        newStatus          : String
    ) returns String;

 

    action generateProjectReport(
        status        : String,
        assignedToP   : Integer,
        startDate     : DateTime,
        endDate       : DateTime
    ) returns Array of String;
 
    action generateEmployeeReport(
        employeeCode  : Integer,
        startDate     : DateTime,
        endDate       : DateTime
    ) returns Array of String;
    entity Projects as projection on db.Projects;
    entity Employees as projection on db.Employees;
    entity SubProjects as projection on db.SubProjects;
}
 
 
 