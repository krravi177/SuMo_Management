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
        
           
       this.updateCounters();
            },
            onRead: function () {
                var oModel = this.getOwnerComponent().getModel(); 
                if (oData.length > 0) {
                  MessageToast.show("Data: " + JSON.stringify(oData));
                } else {
                  MessageToast.show("No data available");
                }
              },
              addSubProjectForm:function(){
                this.getOwnerComponent().getRouter().navTo("NewSubProject");
            },
            
                onLogOutManager:function(){
                    var oRouter = this.getOwnerComponent().getRouter();
                    oRouter.navTo("RouteView1");
                },
                updateCounters: function () {
                    var oModel = this.getOwnerComponent().getModel("ODataV2");
                
                   
                    oModel.read("/Projects", {
                        
                        success: function (oData) {
                            var aProjects = oData.results || [];
                            var iTotalProjects = aProjects.length;
                            var iRunningProjects = aProjects.filter(p => p.status === "Running").length;
                
                            
                            this.getView().byId("totalProjectPm").setValue(iTotalProjects);
                            this.getView().byId("runningProjectsPm").setValue(iRunningProjects);
                        }.bind(this),
                        error: function () {
                            sap.m.MessageToast.show("Failed to fetch project data.");
                        }
                    });
                 
                    oModel.read("/SubProjects", {
                        success: function (oData) {
                            var iTotalSubProject = oData.results.length;
                
                          
                            this.getView().byId("subprojectTablePM").setValue(iTotalSubProject);
                        }.bind(this),
                        error: function () {
                            sap.m.MessageToast.show("Failed to fetch SubProject data.");
                        }
                    });
                   
                    oModel.read("/Employees", {
                        success: function (oData) {
                            var iTotalEmployees = oData.results.length;
                
                          
                            this.getView().byId("totalEmployeesPm").setValue(iTotalEmployees);
                        }.bind(this),
                        error: function () {
                            sap.m.MessageToast.show("Failed to fetch employee data.");
                        }
                    });
                }
          
        
        
    });
});