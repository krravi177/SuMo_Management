sap.ui.define([
    "sap/ui/core/mvc/Controller"
  ], (Controller) => {
    "use strict";
  
    return Controller.extend("com.dash.employeedashboard.controller.SubProjectForm", {
        onInit() {
            
                var oModel = this.getOwnerComponent().getModel("ODataV2");
    
                
            
        },
        onRead: function () {
            var oModel = this.getOwnerComponent().getModel("ODataV2");

            oModel.read("/SubProjects", {
                success: function (oData) {
                    if (oData.results.length > 0) {
                        MessageToast.show("Data: " + JSON.stringify(oData.results));
                    } else {
                        MessageToast.show("No data available");
                    }
                },
                error: function () {
                    MessageToast.show("Failed to fetch data.");
                }
            });
        },
        onSubmitNewSubProjectForm:function(){
            var oView =this.getView();
            var oModel = this.getOwnerComponent().getModel("ODataV2");
            
            var nFModuleId=oView.byId("npModuleId").getValue();
            var nFSPProjectID=oView.byId("npSPProjectID").getValue();
            var nFAssignedToE=oView.byId("npAssignedToE").getValue();
            var nFprojectTimeAssigned=oView.byId("npTimeSPAssigned").getValue();
            var nFProjectManagerID=oView.byId("npProjectManagerID").getValue();
            var nFModuleStatus=oView.byId("npModuleStatus").getSelectedKey();
            var nFCreatedBy=oView.byId("npCreatedBy").getValue();
            var npModifiedBy=oView.byId("npModifiedBy").getValue();
            var nFprojectStatus=oView.byId("npSPStatus").getSelectedKey();
              var nFprojectStartDate=oView.byId("npSPStartDate").getDateValue() ? oView.byId("npSPStartDate").getDateValue().toISOString() : null; 
            var nFprojectEndDate= oView.byId("npSPEndDate").getDateValue() ? oView.byId("npSPEndDate").getDateValue().toISOString() : null; 
            
            var formData={
                moduleId:nFModuleId,
            projectId:nFSPProjectID,
            assignedToE:nFAssignedToE,
            timeAssigned:nFprojectTimeAssigned,
            projectManagerId:nFProjectManagerID,
            moduleStatus:nFModuleStatus,
            createdBy:nFCreatedBy,
            modifiedBy:npModifiedBy,
            startDate:nFprojectStartDate,
            endDate:nFprojectEndDate,
            status:nFprojectStatus
            };
            var oModel = this.getOwnerComponent().getModel("ODataV2"); 
            oModel.create("/SubProjects",formData,{
              
                success: function() {
                    sap.m.MessageToast.show("Registration successful!");
                   
                },
                error: function() {
                    sap.m.MessageToast.show("Registration failed.");
                }
                
            });
            
            
        },
        onCancelNewSubProjectForm:function(){
            this.getView().destroy();
        },
    });
  });