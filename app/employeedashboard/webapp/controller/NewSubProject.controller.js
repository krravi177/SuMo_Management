sap.ui.define([
    "sap/ui/core/mvc/Controller"
], (Controller) => {
    "use strict";

    return Controller.extend("com.dash.employeedashboard.controller.NewSubProject", {
        onInit() {
            var oModel = this.getOwnerComponent().getModel("ODataV2"); 
        },
        onSubmitNewSubProjectForm:function(){
            var oView =this.getView();
            var oModel = this.getOwnerComponent().getModel("ODataV2");
            
            var nFModuleId=oView.byId("npModuleId").getValue();
            var nFSPProjectID=oView.byId("npSPProjectID").getValue();
            var nFAssignedToE=oView.byId("npAssignedToE").getValue();
            var nFprojectTimeAssigned=oView.byId("npTimeSPAssigned").getValue();
            var nFProjectManagerID=oView.byId("npProjectManagerID").getValue();
            var nFModuleStatus=oView.byId("npModuleStatus").getValue();
            var nFCreatedBy=oView.byId("npCreatedBy").getValue();
            var npModifiedBy=oView.byId("npModifiedBy").getValue();
            var nFprojectStartDate=oView.byId("npSPStatus").getValue();
            // var nFprojectStartDate=oView.byId("npSPStartDate").getValue();
            // var nFprojectEndDate=oView.byId("npSPEndDate").getValue();
            
            var formData={
                moduleId:nFModuleId,
            projectId:nFSPProjectID,
            assignedToE:nFAssignedToE,
            timeAssigned:nFprojectTimeAssigned,
            projectManagerId:nFProjectManagerID,
            moduleStatus:nFModuleStatus,
            createdBy:nFCreatedBy
            
            // createdBy:nFprojectStartDate,
            // modifiedBy:nFprojectEndDate,
            };
            var oModel = this.getOwnerComponent().getModel("ODataV2"); 
            oModel.create("/SubProjects",formData,{
              
                success: function() {
                    MessageToast.show("Registration successful!");
                    
                },
                error: function() {
                    MessageToast.show("Registration failed.");
                }
                
            });
            
            oView.close();
        },
        onCancelNewSubProjectForm:function(){
            oView.close();
        },
    });
});