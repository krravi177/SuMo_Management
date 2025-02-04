sap.ui.define([
	 "sap/ui/core/mvc/Controller"
], function (Controller) {
	"use strict";

	return Controller.extend("com.dash.employeedashboard.controller.Employee", {
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
            this.oFragment=new sap.ui.xmlfragment("com.dash.employeedashboard.view.NewEmployee",this);
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
            newEmployeeFregOpen:function(){
                this.oFragment.open();
            },
            onSubmitNewEmployeeForm:function(){
                var oView =sap.ui.getCore();
                var oModel = this.getOwnerComponent().getModel("ODataV2");
                
                var nFEmpCode=oView.byId("npEmpCode").getValue();
                var nFnpEmpName=oView.byId("npEmpName").getValue();
                var nFnpProjectID=oView.byId("npProjectID").getValue();
                var nFnpRating=oView.byId("npRating").getValue();
                var nFnpTimeTaken=oView.byId("npTimeTaken").getValue();
                 
                
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
            },
				
          getRouter: function () {
	           return sap.ui.core.UIComponent.getRouterFor(this);
            },
           onItemPressed : function(oEvent){
        //      var oItem = oEvent.getParameter("listItem");
		// var	oCtx = oItem.getBindingContext();
			//var sempCode= oCtx.getProperty("empCode");
			this.getRouter().navTo("RouteEmployeeDetails",{
				//empcode :sempCode
			});

		},
		onNavBack:function(){
			var oRouter = this.getOwnerComponent().getRouter();
			oRouter.navTo("appHome");
		},
	
  
  

	});

});



