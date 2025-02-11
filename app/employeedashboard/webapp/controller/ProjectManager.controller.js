sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/Filter",
    "sap/m/MessageToast",
      "sap/ui/model/FilterOperator"
    	
], function (Controller, Filter, FilterOperator, MessageToast) {
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

            this._bDescendingSort = false;
            this.updateCounters();
        },
        onSearch: function (oEvent) {
            var oTable = this.getView().byId("subprojectTablePM");
            var oBinding = oTable.getBinding("items");
            var aFilters = []; 
        
           
            var oFilterBar = this.getView().byId("filterbarPM");
            var aFilterItems = oFilterBar.getAllFilterItems(); 
        
            aFilterItems.forEach(function (oFilterItem) {
                var sFilterName = oFilterItem.getName(); 
                var oControl = oFilterItem.getControl(); 
                if (oControl instanceof sap.m.MultiComboBox) {
                    var aSelectedKeys = oControl.getSelectedKeys();
        
                    if (aSelectedKeys.length > 0) {
                        var aValueFilters = aSelectedKeys.map(key => 
                            new sap.ui.model.Filter(sFilterName, sap.ui.model.FilterOperator.EQ, key)
                        );
                        aFilters.push(new sap.ui.model.Filter({
                            filters: aValueFilters,
                            and: false 
                        }));
                    }
                }
            });
           
            oBinding.filter(aFilters);
         },
        
        onSort: function () {
            this._bDescendingSort = !this._bDescendingSort;
        
            var oTable = this.getView().byId("projectTableSeeingAdmin"),
                oBinding = oTable.getBinding("items"),
                oSorter = new sap.ui.model.Sorter("projectId", this._bDescendingSort);
        
            oBinding.sort(oSorter);
        
            
            sap.m.MessageToast.show(`Sorted: ${this._bDescendingSort ? "Descending" : "Ascending"}`);
        },
        onRead: function () {
            var oModel = this.getOwnerComponent().getModel("ODataV2");
            if (oData.length > 0) {
                MessageToast.show("Data: " + JSON.stringify(oData));
            } else {
                MessageToast.show("No data available");
            }
        },
        addSubProjectForm: function () {
            this.getOwnerComponent().getRouter().navTo("NewSubProject");
        },

        onLogOutManager: function () {
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
                success: function (aData) {
                    var iTotalSubProject = aData.results.length;


                    this.getView().byId("totalSubprojects").setValue(iTotalSubProject);
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