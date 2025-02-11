sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/Filter",
    "sap/m/MessageToast",
      "sap/ui/model/FilterOperator"
    	
], function (Controller, Filter, FilterOperator, MessageToast) {
    "use strict";
   

    return Controller.extend("com.dash.employeedashboard.controller.Admin", {
        onInit() {
            
            var oModel = this.getOwnerComponent().getModel("ODataV2");

            // oModel.bindContext("/Projects", {}).requestObject()
            //     .then(function (oData) {
            //         console.log(oData);
            //       }.bind(this))
            //     .catch(function (oError) {

            //         MessageBox.error("Error fetching  details. Please try again.");
            //         BusyIndicator.hide();
            //     }.bind(this));

            
            this._bDescendingSort = false;
            this.updateCounters();
        	this.oRouter = this.getOwnerComponent().getRouter();
		},
		 onSearch: function (oEvent) {
            var oTable = this.getView().byId("projectTableSeeingAdmin");
            var oBinding = oTable.getBinding("items");
            var aFilters = []; 
        
           
            var oFilterBar = this.getView().byId("filterbarAdmin");
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
        onEditTableItem: function (oEvent) {
            var oContext = oEvent.getSource().getBindingContext();
            oContext.getModel().setProperty(oContext.getPath() + "/Editable", true);
        },

       
        onSave: function (oEvent) {
            var oContext = oEvent.getSource().getBindingContext();
            var oModel = oContext.getModel();
            var sPath = oContext.getPath();

            var oUpdatedData = {
                projectId: oContext.getProperty("projectId"),
                projectName: oContext.getProperty("projectName"),
                assignedToP: oContext.getProperty("assignedToP"),
                status: oContext.getProperty("status")
            };

            oModel.update(sPath, oUpdatedData, {
                success: function () {
                    MessageToast.show("Update successful!");
                },
                error: function () {
                    MessageToast.show("Update failed!");
                }
            });

            
            oModel.setProperty(sPath + "/Editable", false);
        },

       
        onCancel: function (oEvent) {
            var oContext = oEvent.getSource().getBindingContext();
            var oModel = oContext.getModel();
            oModel.setProperty(oContext.getPath() + "/Editable", false);
        },

        
        updateCounters: function () {
            var oModel = this.getOwnerComponent().getModel("ODataV2");
        
           
            oModel.read("/Projects", {
                
                success: function (oData) {
                    var aProjects = oData.results || [];
                    var iTotalProjects = aProjects.length;
                    var iRunningProjects = aProjects.filter(p => p.status === "Running").length;
        
                    var iDelayedProjects = aProjects.filter(p => p.status === "Delayed").length;
                    this.getView().byId("runningLateProjects").setValue(iDelayedProjects);
                    this.getView().byId("totalProject").setValue(iTotalProjects);
                    this.getView().byId("runningProjects").setValue(iRunningProjects);
                }.bind(this),
                error: function () {
                    sap.m.MessageToast.show("Failed to fetch project data.");
                }
            });
        
           
            oModel.read("/Employees", {
                success: function (oData) {
                    var iTotalEmployees = oData.results.length;
        
                  
                    this.getView().byId("totalEmployees").setValue(iTotalEmployees);
                }.bind(this),
                error: function () {
                    sap.m.MessageToast.show("Failed to fetch employee data.");
                }
            });
        },
        addForm:function(){
            this.getOwnerComponent().getRouter().navTo("ProjectForm");
        },
        onProjectTilePress: function () {
            var oProjectTable = this.byId("projectTableSeeingAdmin");
            var oEmployeeTable = this.byId("employeeTable");

            oProjectTable.setVisible(true);
            oEmployeeTable.setVisible(false);

            console.log("Project table displayed.");
        },

        onEmployeeTilePress: function () {
            var oEmployeeTable = this.byId("employeeTable");
            var oProjectTable = this.byId("projectTableSeeingAdmin");

            oEmployeeTable.setVisible(true);
            oProjectTable.setVisible(false);

            console.log("Employee table displayed.");
        },

        
     onItemPress:function(oEvent){

        
			// var oFCL = this.oView.getParent().getParent();

			// oFCL.setLayout(fioriLibrary.LayoutType.TwoColumnsMidExpanded);
		},

     onLogOutAdmin:function(){
        this.getOwnerComponent().getRouter().navTo("RouteView1");
      }
    });
});

