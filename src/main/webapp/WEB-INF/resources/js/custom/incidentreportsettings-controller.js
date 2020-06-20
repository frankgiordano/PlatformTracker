app.controller('IncidentReportSettingsController', function ($scope, localStorageService, IncidentService) {

    $scope.init = function () {
        localStorageService.remove("incidentCreateButtonClicked");
        localStorageService.remove("incidentEditMode");
        IncidentService.isToggleAutoWeeklyReport().then(
            function success(response) {
                if (response === "true") {
                    $scope.onoffswitch = "ON";
                } else {
                    $scope.onoffswitch = "OFF";
                }
            },
            function error() {
                $scope.errorMessages = "GET_SETTING_FAILURE - Retrieving toggle status failed, check logs or try again.";
            });
    };

    $scope.saveReportSettings = function () {
        $scope.clearMsg();

        actionValue = {
            "action": $scope.onoffswitch
        };

        IncidentService.toggleAutoWeeklyReport(actionValue).then(
            function success(response) {
                if (response === "true") {
                    $scope.messages = "Saved successfully.";
                } else {
                    $scope.errorMessages = "Failure on saving..";
                }
            },
            function error() {
                $scope.errorMessages = "SETTING_SAVE_FAILURE - Error saving toggle setting, check logs or try again.";
            });
    };

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
    };

});
