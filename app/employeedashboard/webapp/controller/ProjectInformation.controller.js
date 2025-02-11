sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";

    return Controller.extend("com.dash.employeedashboard.controller.ProjectInformation", {
        onInit: function () {
            var oRouter = this.getOwnerComponent().getRouter();
            oRouter.getRoute("ProjectInformation").attachPatternMatched(this._onObjectMatched, this);
          
          
        },


        _onObjectMatched: function (oEvent) {
            var oModel = this.getOwnerComponent().getModel("ODataV2");
            var oArgs = oEvent.getParameter("arguments");
            this._sProjectId = oArgs.projectId;
            var oView = this.getView();

            oView.bindElement({
                path: "/Projects('" + this._sProjectId + "')",
                events: {
                    change: this._onBindingChange.bind(this),
                    dataRequested: function () {
                        oView.setBusy(true);
                    },
                    dataReceived: function () {
                        oView.setBusy(false);
                    }
                }
              
            });
        },


        _onBindingChange: function () {
            var oElementBinding;

            oElementBinding = this.getView().getElementBinding();

            // No data for the binding 
            if (oElementBinding && !oElementBinding.getBoundContext()) {
                this.getRouter().getTargets().display("notFound");
            }
        },

        onEditToggleButtonPress: function () {
            var oObjectPage = this.getView().byId("ObjectPageLayout"),
                bCurrentShowFooterState = oObjectPage.getShowFooter();

            oObjectPage.setShowFooter(!bCurrentShowFooterState);
        },
        addForm:function(){
            this.getOwnerComponent().getRouter().navTo("ProjectForm");
        },
        handleClosePage:function(){
            this.getOwnerComponent().getRouter().navTo("RouteAdmin");
        }
    });
});
