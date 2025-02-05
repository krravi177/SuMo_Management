sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/Filter",
    "sap/m/MessageToast",
    "sap/ui/model/FilterOperator"


], function (Controller, Filter, FilterOperator, MessageToast) {
    "use strict";

    return Controller.extend("com.dash.employeedashboard.controller.Admin", {
        onInit() {


            // oModel.bindContext("/Projects", {}).requestObject()
            //     .then(function (oData) {
            //         console.log(oData);
            //       }.bind(this))
            //     .catch(function (oError) {

            //         MessageBox.error("Error fetching  details. Please try again.");
            //         BusyIndicator.hide();
            //     }.bind(this));

            this.oFragment = new sap.ui.xmlfragment("com.dash.employeedashboard.view.NewProject", this);
            this.getView().addDependent(this.oFragment);

            // this.updateCounters();
        },
        onRead: function () {
            var oModel = this.getOwnerComponent().getModel();
            if (oData.length > 0) {
                MessageToast.show("Data: " + JSON.stringify(oData));
            } else {
                MessageToast.show("No data available");
            }
        },
        newProjectFregOpen: function () {
            this.oFragment.open();
        },
        onSubmitNewProjectForm: function () {
            // var oView = sap.ui.getCore();
            var oModel = this.getOwnerComponent().getModel("ODataV2");

                var oView = this.getView();
                var formData = {
                    projectId: oView.byId("npProjectId").getValue(),
                    projectName: oView.byId("npProjectName").getValue(),
                    assignedToP: oView.byId("npAssignedTo").getValue(),
                    timeAssigned: oView.byId("npTimeAssigned").getValue(),
                    projectChanges: oView.byId("npChanges").getValue(),
                    clientName: oView.byId("npClientName").getValue(),
                    description: oView.byId("npDescription").getValue(),
                    status: oView.byId("npStatus").getValue(),
                    urgency: oView.byId("npUrgency").getValue(),
                    createdBy: oView.byId("npCreateBy").getValue(),
                    modifiedBy: oView.byId("npModifieBy").getValue(),
                    startDate: oView.byId("npStartDate").getValue(),
                    endDate: oView.byId("npEndDate").getValue()
                };                
            // var oModel = this.getOwnerComponent().getModel("ODataV2");
            // oModel.create("/Projects", formData, {

            //     success: function () {
            //         MessageToast.show("Registration successful!");

            //     },
            //     error: function () {
            //         MessageToast.show("Registration failed.");
            //     }

            // });

            

            this.oModel.callFunction("/createProject", {
                method: "POST",
                urlParameters: formData,
                success: function(oData) {
                    sap.m.MessageToast.show("Employee created successfully!");
                },
                error: function(oError) {
                    sap.m.MessageToast.show("Error creating employee.");
                    console.error(oError);
                }
            });

            this.oFragment.close();
        },
        onCancelNewProjectForm: function () {
            this.oFragment.close();
        },
        onEditTableItem: function () {
            //     var oModel=this.getOwnerComponent().getModel();
            //     var oTable=this.getView().byId("projectTableSeeingAdmin");
            //     var oSelectedItem=oTable.getSelectedItem();
            //     var id=oSelectedItem.projectId;

            //     var sPath="/Projects("+id+")";
            //     var nFprojectID=oView.byId("adProjectId").getValue();
            //     var nFprojectName=oView.byId("adProjectName").getValue();
            //     var nFprojectAssigned=oView.byId("adAssignedToP").getValue();
            //     var nFprojectTimeAssigned=oView.byId("adTimeAssigned").getValue();
            //     var nFprojectChanges=oView.byId("adProjectChanges").getValue();
            //     var nFprojectClient=oView.byId("adClientName").getValue();
            //     var nFDescription=oView.byId("adDescription").getValue();
            //     var nFprojectStatus=oView.byId("adStatus").getValue();
            //     var nFprojectUrgency=oView.byId("adUrgency").getValue();
            //     var nFCreatedBy=oView.byId("adCreatedBy").getValue();
            //     var nFModifiedBy=oView.byId("adModifiedBy").getValue();
            //     var nFprojectStartDate=oView.byId("adStartDate").getValue();
            //      var nFprojectEndDate=oView.byId("adEndDate").getValue();

            //      var formData={
            //         projectId:nFprojectID,
            //     projectName:nFprojectName,
            //     assignedToP:nFprojectAssigned,
            //     timeAssigned:nFprojectTimeAssigned,
            //     projectChanges:nFprojectChanges,
            //     clientName:nFprojectClient,
            //    description :nFDescription,
            //     status:nFprojectStatus,
            //     urgency:nFprojectUrgency,
            //     createdBy:nFCreatedBy,
            //     startDate:nFprojectStartDate,
            //     modifiedBy:nFModifiedBy,
            //     endDate:nFprojectEndDate
            //     };
            //     var oModel = this.getOwnerComponent().getModel("ODataV2"); 
            //     oModel.update(sPath,formData,{

            //         success: function() {
            //             MessageToast.show("Registration successful!");

            //         },
            //         error: function() {
            //             MessageToast.show("Registration failed.");
            //         }

            //     });



            var oContext = oEvent.getSource().getBindingContext();
            var oModel = oContext.getModel();
            oModel.setProperty(oContext.getPath() + "/Editable", true);
        },

        // Save Updated Data
        onSave: function (oEvent) {
            var oModel = this.getView().getModel();
            var oContext = oEvent.getSource().getBindingContext();
            var sPath = oContext.getPath();

            var oUpdatedData = {
                projectId: nFprojectID,
                //     projectName:nFprojectName,
                //     assignedToP:nFprojectAssigned,
                //     timeAssigned:nFprojectTimeAssigned,
                //     projectChanges:nFprojectChanges,
                //     clientName:nFprojectClient,
                //    description :nFDescription,
                //     status:nFprojectStatus,
                //     urgency:nFprojectUrgency,
                //     createdBy:nFCreatedBy,
                //     startDate:nFprojectStartDate,
                //     modifiedBy:nFModifiedBy,
                //     endDate:nFprojectEndDate
                ProductName: oContext.getProperty("projectName"),
                Price: oContext.getProperty("assignedToP")
            };

            oModel.update(sPath, oUpdatedData, {
                success: function () {
                    MessageToast.show("Update successful!");
                },
                error: function () {
                    MessageToast.show("Update failed!");
                }
            });

            // Disable Edit Mode
            oModel.setProperty(sPath + "/Editable", false);
        },

        // Cancel Editing
        onCancel: function (oEvent) {
            var oContext = oEvent.getSource().getParent().getParent().getBindingContext();
            var oModel = oContext.getModel();
            oModel.setProperty(oContext.getPath() + "/Editable", false);
        },
        
        // updateCounters: function () {
        //     var oModel = this.getOwnerComponent().getModel("ODataV2");
        //     var aProjects = oModel.getProperty("/Projects");
        //     var iTotalProjects = aProjects.length;
        //     var iTotalEmployees = oModel.getProperty("/Employees").length;
        //     var iRunningProjects = aProjects.filter(p => p.status === "Running").length;

        //     this.byId("totalProject").setValue(iTotalProjects);
        //     this.byId("totalEmployees").setValue(iTotalEmployees);
        //     this.byId("runningProjects").setValue(iRunningProjects);
        // },

        onTilePress: function (oEvent) {
            sap.m.MessageToast.show(oEvent.getSource().getHeader() + " clicked!");
        },


        onSelectionChange: function () {
            this.applyFilters();
        },

        onSearch: function () {
            this.applyFilters();
        },

        applyFilters: function () {
            var oTable = this.byId("projectTableSeeingAdmin");
            var oBinding = oTable.getBinding("items");
            var aFilters = [];

            var oProjectIdFilter = this.byId("filterbarAdmin").getControlByKey("ProjectID");
            var oProjectNameFilter = this.byId("filterbarAdmin").getControlByKey("ProjectName");
            var oClientNameFilter = this.byId("filterbarAdmin").getControlByKey("ClientName");

            var aSelectedProjectIds = oProjectIdFilter ? oProjectIdFilter.getSelectedKeys() : [];
            var aSelectedProjectNames = oProjectNameFilter ? oProjectNameFilter.getSelectedKeys() : [];
            var aSelectedClientNames = oClientNameFilter ? oClientNameFilter.getSelectedKeys() : [];

            if (aSelectedProjectIds.length > 0) {
                aFilters.push(new Filter("projectId", FilterOperator.Contains, aSelectedProjectIds));
            }

            if (aSelectedProjectNames.length > 0) {
                aFilters.push(new Filter("projectName", FilterOperator.Contains, aSelectedProjectNames));
            }

            if (aSelectedClientNames.length > 0) {
                aFilters.push(new Filter("clientName", FilterOperator.Contains, aSelectedClientNames));
            }

            oBinding.filter(aFilters);
        },

        onAfterVariantLoad: function () {
            this.applyFilters();
        }
    });
});

