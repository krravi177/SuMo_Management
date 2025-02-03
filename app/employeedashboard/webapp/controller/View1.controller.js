sap.ui.define([
    "sap/ui/core/mvc/Controller"
], (Controller) => {
    "use strict";

    return Controller.extend("com.dash.employeedashboard.controller.View1", {
        onInit() {
        },
        onNavToAdmin: function () {
            this.getOwnerComponent().getRouter().navTo("RouteAdmin");
        },
        onNavToProjectManager: function () {
			this.getOwnerComponent().getRouter().navTo("RouteProjectManager");
		},
        onNavToEmployees: function () {
			this.getOwnerComponent().getRouter().navTo("RouteEmployees");
        }
    });
});