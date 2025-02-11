sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/m/MessageToast"
], (Controller,MessageToast) => {
    "use strict";
   
    return Controller.extend("com.dash.employeedashboard.controller.View1", {
        onInit() {
        },
       
        onSubmitLogInForm: function () {
                    var oView = this.getView();
                    var sUsername = oView.byId("usernameInput").getValue()?.trim();
                    var sPassword = oView.byId("passwordInput").getValue()?.trim();
                    var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
        
                    if (!sUsername || !sPassword) {
                        MessageToast.show("Username and Password cannot be empty.");
                        return;
                    }
        
                
                    var aUsers = [
                        { id: "admin", password: "admin123", role: "Admin" },
                        { id: "pm", password: "pm123", role: "ProjectManager" },
                        { id: "emp", password: "emp123", role: "Employee" }
                    ];
        
                    
                    var oUser = aUsers.find(user => user.id === sUsername);
        
                    if (!oUser) {
                        MessageToast.show("User not found.");
                        return;
                    }
        
                    if (oUser.password !== sPassword) {
                        MessageToast.show("Incorrect password.");
                        return;
                    }
        
                    MessageToast.show("Login Successful!");
        
                    
                    switch (oUser.role) {
                        case "Admin":
                            oRouter.navTo("RouteAdmin");
                            break;
                        case "ProjectManager":
                            oRouter.navTo("RouteProjectManager");
                            break;
                        case "Employee":
                            oRouter.navTo("RouteEmployees");
                            break;
                        default:
                            MessageToast.show("Invalid role. Contact Admin.");
                    }
                },
        
                
        onNavToAdmin: function () {
            this.getOwnerComponent().getRouter().navTo("RouteAdmin");
        },
        onNavToProjectManager: function () {
			this.getOwnerComponent().getRouter().navTo("RouteProjectManager");
		},
        onNavToEmployees: function () {
            // var oFCL = this.getParent().getParent();

			// oFCL.setLayout(fioriLibrary.LayoutType.TwoColumnsMidExpanded);
			this.getOwnerComponent().getRouter().navTo("RouteEmployees");
        }
    });
});