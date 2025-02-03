sap.ui.define([
    "sap/ui/core/mvc/Controller"
], (Controller) => {
    "use strict";

    return Controller.extend("com.dash.employeedashboard.controller.ProjectManager", {
        onInit() {
            var oModel = this.getOwnerComponent().getModel(); 
                       
            // oModel.bindContext("/SubProjects", {}).requestObject()
            //     .then(function (oData) {
            //         console.log(oData);
            //       }.bind(this))
            //     .catch(function (oError) {

            //         MessageBox.error("Error fetching  details. Please try again.");
            //         BusyIndicator.hide();
            //     }.bind(this));
            
                  
            //     },
            this.oFragment=new sap.ui.xmlfragment("com.dash.employeedashboard.view.NewSubProject",this);
            this.getView().addDependent(this.oFragment);
           
       
            },
            onRead: function () {
                var oModel = this.getOwnerComponent().getModel(); 
                if (oData.length > 0) {
                  MessageToast.show("Data: " + JSON.stringify(oData));
                } else {
                  MessageToast.show("No data available");
                }
              },
            newSubProjectFregOpen:function(){
                this.oFragment.open();
            },
            onSubmitNewSubProjectForm:function(){
                var oView =sap.ui.getCore();
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
                
                this.oFragment.close();
            },
            onCancelNewSubProjectForm:function(){
                this.oFragment.close();
            },

                onNav:function(){
                    var oRouter = this.getOwnerComponent().getRouter();
                    oRouter.navTo("");
                },
                onNavBack:function(){
                    var oRouter = this.getOwnerComponent().getRouter();
                    oRouter.navTo("appHome");
                }
          
        
        
    });
});