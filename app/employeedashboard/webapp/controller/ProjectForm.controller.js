sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast"
], function (Controller, MessageToast) {
    "use strict";

    return Controller.extend("com.dash.employeedashboard.controller.ProjectForm", {
        
        onInit: function () {
            var oModel = this.getView().getModel("ODataV2");
        },

        onRead: function () {
            var oModel = this.getOwnerComponent().getModel("ODataV2");

            oModel.read("/Projects", {
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

        onSubmitNewProjectFormAdmin: function () {
            var oView = this.getView();
            var oModel = oView.getModel("ODataV2");

            var formData = {
                projectId: oView.byId("pProjectId").getValue(),
                projectName: oView.byId("pProjectName").getValue(),
                assignedToP: oView.byId("pAssignedTo").getValue(),
                timeAssigned: oView.byId("pTimeAssigned").getValue(),
                projectChanges: oView.byId("pChanges").getValue(),
                clientName: oView.byId("pClientName").getValue(),
                description: oView.byId("pnDescription").getValue(),
                status: oView.byId("pStatus").getSelectedKey(), 
                urgency: oView.byId("pUrgency").getSelectedKey(), 
                createdBy: oView.byId("pCreateBy").getValue(),
                modifiedBy: oView.byId("pModifieBy").getValue(),
                startDate: oView.byId("pStartDate").getDateValue() ? oView.byId("pStartDate").getDateValue().toISOString() : null, // FIXED
                endDate: oView.byId("pEndDate").getDateValue() ? oView.byId("pEndDate").getDateValue().toISOString() : null // FIXED
            };

            oModel.create("/Projects", formData, {
                success: function () {
                    MessageToast.show("Project created successfully!");
                    oModel.refresh();
                },
                error: function () {
                    MessageToast.show("Registration failed.");
                }
            });
        },
        onNavAdminProjectInfoPage:function(){
            var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            oRouter.navTo("RouteAdmin"); 
        },
        onCancelNewProjectFormAdmin: function () {
            var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            oRouter.navTo("RouteAdmin"); 
        }
    });
});
