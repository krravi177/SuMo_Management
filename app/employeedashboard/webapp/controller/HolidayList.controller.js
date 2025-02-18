sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/model/json/JSONModel"
], function (Controller, JSONModel) {
    "use strict";

    var PageController = Controller.extend("com.dash.employeedashboard.controller.HolidayList", {
        onInit: function () {
            this._sValidPath = sap.ui.require.toUrl("com/dash/employeedashboard/extraa/Hoilday List_2025.pdf");
            this._oModel = new JSONModel({
                Source: this._sValidPath,
                Title: "Holiday Calendar",
                Height: "500px"
            });
            this.getView().setModel(this._oModel);
        },

        onClosePDF: function () {
           this.getOwnerComponent().getRouter().navTo("RouteView1");
        }
    });

    return PageController;
});
