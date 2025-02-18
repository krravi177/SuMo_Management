sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/core/Fragment",
    "sap/m/MessageToast",
    "sap/m/MessageBox"
], (Controller, Fragment, MessageToast, MessageBox) => {
    "use strict";
	return Controller.extend("com.dash.employeedashboard.controller.Employee", {
        onInit() {
            var oModel = this.getView().getModel("ODataV2");
            var oTable = this.getView().byId("employeesTable");
                       
            // oModel.bindContext("/SubProjects", {}).requestObject()
            //     .then(function (oData) {
            //         console.log(oData);
            //       }.bind(this))
            //     .catch(function (oError) {

            //         MessageBox.error("Error fetching  details. Please try again.");
            //         BusyIndicator.hide();
            //     }.bind(this));
            
                  
            //     },
            this.oFragment=new sap.ui.xmlfragment("com.dash.employeedashboard.view.NewEmployee",this);
            this.getView().addDependent(this.oFragment);
           
            this.updateCounters();
            },
            addTeamCalendor:function(){
                this.getOwnerComponent().getRouter().navTo("TeamCalendor");
                },
                onSearch: function (oEvent) {
                    var oTable = this.getView().byId("employeesTable");
                    var oBinding = oTable.getBinding("items");
                    var aFilters = []; 
                
                   
                    var oFilterBar = this.getView().byId("filterbarEmployee");
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
                
                    var oTable = this.getView().byId("employeesTable"),
                        oBinding = oTable.getBinding("items"),
                        oSorter = new sap.ui.model.Sorter("empCode", this._bDescendingSort);
                
                    oBinding.sort(oSorter);
                
                    
                    sap.m.MessageToast.show(`Sorted: ${this._bDescendingSort ? "Descending" : "Ascending"}`);
                },
            onRead: function () {
                var oModel = this.getOwnerComponent().getModel(); 
                if (oData.length > 0) {
                  MessageToast.show("Data: " + JSON.stringify(oData));
                } else {
                  MessageToast.show("No data available");
                }
              },
            onPressInsert:function(){
                this.oFragment.open();
                var oView =sap.ui.getCore();
                oView.byId("npEmpCode").setValue("");
                oView.byId("npEmpName").setValue("");
                oView.byId("npProjectID").setValue("");
                oView.byId("npRating").setValue("");
                oView.byId("npTimeTaken").setValue("");
 
                this.byId("copyBtn").setEnabled(false);
                this.byId("updateBtn").setEnabled(false);
            this.byId("deleteBtn").setEnabled(false);
            this.byId("refreshBtn").setEnabled(false);
                 
            },
            onSubmitNewEmployeeForm:function(){
                var oView =sap.ui.getCore();
                var oModel = this.getOwnerComponent().getModel("ODataV2");
                
                var nFEmpCode=oView.byId("npEmpCode").getValue();
                var nFnpEmpName=oView.byId("npEmpName").getValue();
                var nFnpProjectID=oView.byId("npProjectID").getValue();
                var nFnpRating=oView.byId("npRating").getValue();
                var nFnpTimeTaken=oView.byId("npTimeTaken").getValue();
                 
            s
                var formData={
                    empCode:nFEmpCode,
                empName:nFnpEmpName,
                projectId:nFnpProjectID,
                rating:nFnpRating,
                timeTaken:nFnpTimeTaken
                // createdBy:nFprojectStartDate,
                // modifiedBy:nFprojectEndDate,
                };
                var oModel = this.getOwnerComponent().getModel("ODataV2"); 
                oModel.create("/Employees",formData,{
                  
                    success: function() {
                        sap.m.MessageToast.show("Registration successful!");
                        
                    },
                    error: function() {
                        sap.m.MessageToast.show("Registration failed.");
                    }
                    
                });
                
                this.oFragment.close();
            },
            onCancelNewEmployeeForm:function(){
                this.oFragment.close();
                this.byId("updateBtn").setEnabled(true);
                this.byId("refreshBtn").setEnabled(true);
                this.byId("addBtn").setEnabled(true);
                this.byId("copyBtn").setEnabled(true);
            },
				
          getRouter: function () {
	           return sap.ui.core.UIComponent.getRouterFor(this);
            },
    
		onLogOutEmployee:function(){
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
        
                    
                    this.getView().byId("totalProjectEmp").setValue(iTotalProjects);
                    this.getView().byId("runningProjectsEmp").setValue(iRunningProjects);
                }.bind(this),
                error: function () {
                    sap.m.MessageToast.show("Failed to fetch project data.");
                }
            });
        
           
            oModel.read("/Employees", {
                success: function (oData) {
                    var iTotalEmployees = oData.results.length;
        
                  
                    this.getView().byId("totalEmployeesEmp").setValue(iTotalEmployees);
                }.bind(this),
                error: function () {
                    sap.m.MessageToast.show("Failed to fetch employee data.");
                }
            });
        },
        
                // onRowSelection: function(oEvent) {
                
                //     var oTable = this.getView().byId("employeesTable");
                //     var aSelectedContexts = oTable.getSelectedContexts();
                //     this._selectedItems = aSelectedContexts.map(context => context.getObject());
                //     console.log(this._selectedItems);
                // },
        
                // onSelectAll: function(oEvent) {
                //     var oTable = this.getView().byId("employeesTable");
                //     var bSelected = oEvent.getSource().getSelected();
                //     oTable.getItems().forEach(function(oItem) {
                //         var oCheckBox = oItem.getCells()[0]; 
                //         oCheckBox.setSelected(bSelected);
                //     });
                //     this.onRowSelection(); // Update selected items
                // },
        
                // onDeletePress: function() {
                //     var oModel = this.getView().getModel("ODataV2");
                //     var oTable = this.getView().byId("employeesTable");
                //     var aSelectedContexts = oTable.getSelectedContexts();
        
                //     if (aSelectedContexts.length === 0) {
                //         MessageToast.show("No row selected!");
                //         return;
                //     }
        
                //     MessageBox.confirm("Are you sure you want to delete the selected service instances?", {
                //         title: "Confirm Deletion",
                //         icon: MessageBox.Icon.WARNING,
                //         actions: [MessageBox.Action.YES, MessageBox.Action.NO],
                //         onClose: function(sAction) {
                //             if (sAction === MessageBox.Action.YES) {
                //                 aSelectedContexts.forEach(function(oContext) {
                //                     oModel.remove(oContext.getPath(), {
                //                         success: function() {
                //                             MessageToast.show("Deleted successfully");
                //                             oModel.refresh(true);
                //                         },
                //                         error: function() {
                //                             MessageToast.show("Error deleting record");
                //                         }
                //                     });
                //                 });
                //             }
                //         }
                //     });
                // },

                onItemPressed: function (oEvent) {
            var oListItem = oEvent.getParameter("listItem");
            var oTable = this.byId("employeesTable");
            var aSelectedItems = oTable.getSelectedItems();
            var bIsSelected = aSelectedItems.includes(oListItem);

            if (bIsSelected) {
            oTable.setSelectedItem(oListItem, false);
                sap.m.MessageToast.show("Item unselected!");
            } else {
                oTable.setSelectedItem(oListItem, true);
                sap.m.MessageToast.show("Item selected!");
                
            }
           },

        onSave: function (oEvent) {
            console.log(oEvent);
            console.log("Save Button Clicked");
            if (this._isCustomerNumberValid !== true) {
                sap.m.MessageToast.show("Please correct the errors before submitting.");
                return; // Stop form submission
            }

            var oData = {
                SONumber: this.byId("SONumber").getValue(),
                OrderDate: this.byId("OrderDate").getDateValue(),
                CustomerName: this.byId("CustomerName").getValue(),
                CustomerNumber:this.byId("CustomerNumber").getValue(),
                PONumber: this.byId("PONumber").getValue(),
                InquiryNumber: this.byId("InquiryNumber").getValue(),
                TotalOrderItems: parseInt(this.byId("TotalOrderItems").getValue(), 10)

            };
            console.table(oData);


      

            var oView = this.getView();
            var oModel = oView.getModel();
            var oDialog = this.byId("insertDialog");
            console.log(oModel);

            oModel.create("/SalesOrders", oData, {
                success: function () {
                    sap.m.MessageToast.show("Sales Order added successfully!");
                    oDialog.close();
                },
                error: function () {
                    sap.m.MessageToast.show("Error while adding Sales Order.");
                }
            });
            this.byId("edit").setEnabled(true);
            this.byId("delete").setEnabled(true);
            this.byId("add").setEnabled(true);
            this.byId("refresh").setEnabled(true);


        },

        onDelete: function () {
            console.log("delete butoon is pressed");

            var oTable = this.byId("salesOrderTable");
            var oModel = this.getView().getModel();
            var aSelectedItems = oTable.getSelectedItems();
            console.log(oTable);
            console.log(oModel);
            console.log(aSelectedItems);


            // Check if any row is selected
            if (aSelectedItems.length === 0) {
                MessageToast.show("Please select at least one row to delete.");
                return;
            }
            var aPaths = aSelectedItems.map(function (oItem) {
                return oItem.getBindingContext().getPath();
            });
            console.log(aPaths);
            MessageBox.confirm("Are you sure you want to delete the selected Sales Orders?", {
                actions: [MessageBox.Action.OK, MessageBox.Action.CANCEL],
                onClose: function (sAction) {
                    if (sAction === MessageBox.Action.OK) {
                        // Delete each selected item
                        aPaths.forEach(function (sPath) {
                            oModel.remove(sPath, {
                                success: function () {
                                    MessageToast.show("Sales Order deleted successfully.");
                                    sap.m.MessageBox.success("Sales Order deleted successfully.", {
                                        title: "Success",
                                    });
                                    oTable.removeSelections(); // Clear selection after delete
                                },
                                error: function () {
                                    MessageToast.show("Error deleting Sales Order.");
                                }
                            });
                        });
                    }
                }
            })




        },


        onRefresh: function () {
            console.log("Refresh butoon is pressed");
            this.this.getView().getModel("ODataV2").refresh();

         
            sap.m.MessageToast.show("Data refreshed successfully!");
        },
        onEdit: function (oEvent) {
            console.log("Edit button clicked");
        
            var oView = this.getView();
            var oTable = this.byId("salesOrderTable");
            var aSelectedItems = oTable.getSelectedItems();
            console.log(aSelectedItems.length);
        
            if (aSelectedItems.length === 0 || aSelectedItems.length > 1) {
               
                
                MessageToast.show("Please select a row to Edit.");
                return;
            }
          
        
            if (aSelectedItems.length === 1) {
                var oSelectedItem = aSelectedItems[0];
                var oContext = oSelectedItem.getBindingContext();
                var oData = oContext.getObject();
              
        if (!this.byId("insertDialog")) {
                    Fragment.load({
                        id: oView.getId(),
                        name: "com.sap.capproject.view.InsertSalesOrder",
                        controller: this
                    }).then(function (oDialog) {
                        oView.addDependent(oDialog);
                        oDialog.open();
                        this.byId("save").setText("Update");
                        this.byId("save").detachPress(this.onSave, this); 
                        this.byId("save").attachPress(this.onUpdate, this); 
        
                        // Set the input fields with selected row values AFTER dialog is loaded
                        this.byId("SONumber").setValue(oData.SONumber);
                        this.byId("OrderDate").setDateValue(new Date(oData.OrderDate));
                        this.byId("CustomerName").setValue(oData.CustomerName);
                        this.byId("CustomerNumber").setValue(oData.CustomerNumber);
                        this.byId("PONumber").setValue(oData.PONumber);
                        this.byId("InquiryNumber").setValue(oData.InquiryNumber);
                        this.byId("TotalOrderItems").setValue(oData.TotalOrderItems);
        
                    }.bind(this));  // Bind 'this' to access controller's scope
                } else {
                    this.byId("insertDialog").open();
            
        
                    // Set the input fields if the dialog is already loaded
                    this.byId("SONumber").setValue(oData.SONumber);
                    this.byId("OrderDate").setDateValue(new Date(oData.OrderDate));
                    this.byId("CustomerName").setValue(oData.CustomerName);
                    this.byId("CustomerNumber").setValue(oData.CustomerNumber);
                    this.byId("PONumber").setValue(oData.PONumber);
                    this.byId("InquiryNumber").setValue(oData.InquiryNumber);
                    this.byId("TotalOrderItems").setValue(oData.TotalOrderItems);
                }
        
                console.log(oData);
        
                this.byId("delete").setEnabled(false);
                this.byId("add").setEnabled(false);
                this.byId("refresh").setEnabled(false);
            }
        },

        onUpdate:function(){
             console.log("Update clicked");
  
                var oModel = this.getView().getModel(); 
                var oDialog = this.byId("insertDialog");
                
             
                var sSONumber = this.byId("SONumber").getValue();
                var sOrderDate = this.byId("OrderDate").getDateValue().toISOString(); 
                var sCustomerName = this.byId("CustomerName").getValue();
                var sCustomerNumber = this.byId("CustomerNumber").getValue();
                var sPONumber = this.byId("PONumber").getValue();
                var sInquiryNumber = this.byId("InquiryNumber").getValue();
                var sTotalOrderItems = this.byId("TotalOrderItems").getValue();
                
               
                var uData = {
                    SONumber: sSONumber,
                    OrderDate: sOrderDate,
                    CustomerName: sCustomerName,
                    CustomerNumber: sCustomerNumber,
                    PONumber: sPONumber,
                    InquiryNumber: sInquiryNumber,
                    TotalOrderItems: sTotalOrderItems
                };
            
                
                var oTable = this.byId("salesOrderTable");
                var aSelectedItems = oTable.getSelectedItems();
                var oSelectedItem = aSelectedItems[0];
                var sPath = oSelectedItem.getBindingContext().getPath();
                console.log(sPath);
                
            
              
                oModel.update(sPath, uData, {
                    success: function () {
                        MessageToast.show("Order updated successfully.");
                        oDialog.close();
                        oModel.refresh(); 
                    },
                    error: function (oError) {
                        MessageToast.show("Failed to update the order.",oError);
                        console.error(oError);
                    }

                });
                
                this.byId("delete").setEnabled(true);
                this.byId("add").setEnabled(true);
                this.byId("refresh").setEnabled(true);
             }
             
    });
});
        
	



