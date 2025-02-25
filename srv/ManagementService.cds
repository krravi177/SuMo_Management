using { sumo_management as db } from '../db/schema';

service ManagementService {
    // Project Actions
    action createProject(
        projectName        : String,
        timeAssigned       : Integer,
        projectManagerId   : Integer,
        projectChanges     : Integer,
        clientName         : String,
        status             : String(20),
        description        : String(255),
        urgency            : String(10),
        createdBy          : Integer,
        modifiedBy         : Integer
    ) returns Integer;

    action updateProject(
        projectId          : Integer,
        projectName        : String,
        timeAssigned       : Integer,
        projectManagerId   : Integer,
        projectChanges     : Integer,
        clientName         : String,
        status             : String(20),
        description        : String(255),
        urgency            : String(10),
        modifiedBy         : Integer
    ) returns Boolean;

    action deleteProject(projectId: Integer) returns Boolean;
    action assignProjectManager(projectId: Integer, managerId: Integer) returns Boolean;
    action calculateProjectCost(projectId: Integer) returns Decimal;

    // Employee Actions
    action createEmployee(
        empName            : String,
        empId              : Integer,
        projectId          : Integer,
        timeTaken          : Integer,
        managerId          : Integer,
        createdBy          : Integer,
        modifiedBy         : Integer
    ) returns Integer;

    action updateEmployee(
        empId              : Integer,
        empName            : String,
        projectId          : Integer,
        timeTaken          : Integer,
        managerId          : Integer,
        modifiedBy         : Integer
    ) returns Boolean;

    action deleteEmployee(empId: Integer) returns Boolean;

    // Sub-Project Actions
    action createSubProject(
        projectId          : Integer,
        moduleName         : String,
        description        : String(255),
        status             : String(20),
        startDate          : Timestamp,
        endDate            : Timestamp,
        assignedTo         : Integer,
        createdBy          : Integer,
        modifiedBy         : Integer
    ) returns Integer;

    action updateSubProject(
        moduleId           : Integer,
        projectId          : Integer,
        moduleName         : String,
        description        : String(255),
        status             : String(20),
        startDate          : Timestamp,
        endDate            : Timestamp,
        assignedTo         : Integer,
        modifiedBy         : Integer
    ) returns Boolean;

    action deleteSubProject(moduleId: Integer) returns Boolean;
    action updateSubProjectStatus(moduleId: Integer, newStatus: String(20)) returns Boolean;

    // Reports
    action generateProjectReport(status: String(20), assignedToP: Integer, startDate: Timestamp, endDate: Timestamp) returns Array of String;
    action generateEmployeeReport(empId: Integer, startDate: Timestamp, endDate: Timestamp) returns Array of String;

    // Employee Schedule
    action createEmployeeSchedule(
        empId            : Integer,
        date             : Date,
        startDateTime    : Timestamp,
        endDateTime      : Timestamp,
        type             : String(20),
        description      : String(255)
    ) returns Integer;

    action updateEmployeeSchedule(
        scheduleId       : Integer,
        empId            : Integer,
        date             : Date,
        startDateTime    : Timestamp,
        endDateTime      : Timestamp,
        type             : String(20),
        description      : String(255)
    ) returns Boolean;

    action deleteEmployeeSchedule(scheduleId: Integer) returns Boolean;

    // Team Schedule
    action createTeamSchedule(
        projectId        : Integer,
        empId            : Integer,
        startDateTime    : Timestamp,
        endDateTime      : Timestamp,
        type             : String(20),
        remarks          : String(255)
    ) returns Integer;

    action updateTeamSchedule(
        scheduleId       : Integer,
        projectId        : Integer,
        empId            : Integer,
        startDateTime    : Timestamp,
        endDateTime      : Timestamp,
        type             : String(20),
        remarks          : String(255)
    ) returns Boolean;

    action deleteTeamSchedule(scheduleId: Integer) returns Boolean;

    // Leave Requests
    action applyLeave(
        empId            : Integer,
        leaveType        : String(50),
        startDate        : Timestamp,
        endDate          : Timestamp,
        reason           : String(255)
    ) returns Integer;

    action approveLeave(
        leaveRequestId   : Integer,
        approvedBy       : Integer,
        status           : String(20)
    ) returns Boolean;

    action deleteLeaveRequest(leaveRequestId: Integer) returns Boolean;

    // Approval Inbox
    action createApprovalInboxEntry(
        leaveRequestId   : Integer,
        managerEmpId     : Integer,
        requestStatus    : String(20),
        requestDate      : Timestamp,
        responseDate     : Timestamp,
        response         : String(255)
    ) returns Integer;

    action updateApprovalInboxEntry(
        inboxEntryId     : Integer,
        requestStatus    : String(20),
        responseDate     : Timestamp,
        response         : String(255)
    ) returns Boolean;

    action deleteApprovalInboxEntry(inboxEntryId: Integer) returns Boolean;
    

    // Entity Projections
    entity Projects as projection on db.Projects;
    entity Employees as projection on db.Employees;
    entity SubProjects as projection on db.SubProjects;
    entity EmployeeSchedule as projection on db.EmployeeSchedule;
    entity TeamSchedule as projection on db.TeamSchedule;
    entity LeaveRequest as projection on db.LeaveRequest;
    entity ApprovalInbox as projection on db.ApprovalInbox;
    entity EmployeeBankDetails as projection on db.EmployeeBankDetails;
    entity EmployeesAuthentication as projection on db.EmployeesAuthentication;
    entity EmployeesPersonal as projection on db.EmployeesPersonal;
}
