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

    // action to delete a project
    action deleteProject(projectId: Integer) returns Boolean;

    // action to assign a project manager
    action assignProjectManager(projectId: Integer, managerId: Integer) returns String;

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

    // action to delete an employee
    action deleteEmployee(empCode: Integer) returns Boolean;

    // action to generate a project report
    action generateProjectReport(
        status        : String,
        assignedToP   : Integer,
        startDate     : DateTime,
        endDate       : DateTime
    ) returns Array of String;

    // action to generate an employee report
    action generateEmployeeReport(
        employeeCode  : Integer,
        startDate     : DateTime,
        endDate       : DateTime
    ) returns Array of String;

    // Entities with restrictions for authorization
    @(restrict: [
            { grant: '*', to: 'Administrators' },
            { grant: '*', where: 'createdBy = $user.id' }
        ])
    entity Projects as projection on db.Projects;

    @(restrict: [
            { grant: '*', to: 'Administrators' },
            { grant: '*', where: 'createdBy = $user.id' }
        ])
    entity Employees as projection on db.Employees;

    @(restrict: [
            { grant: '*', to: 'Administrators' },
            { grant: '*', where: 'createdBy = $user.id' }
        ])
    entity SubProjects as projection on db.SubProjects;
}
