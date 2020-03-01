app.controller('IncidentReportSettingsController', function ($scope, IncidentService) {

    $scope.init = function () {
        IncidentService.isToggleAutoWeeklyReport().then(
            function success(response) {
                console.log(JSON.stringify(response));
                if (response === "true") {
                    $scope.onoffswitch = "ON";
                } else {
                    $scope.onoffswitch = "OFF";
                }
            },
            function error() {
                $scope.errormessages = "GET_SETTING_FAILURE - Retrieving toggle status failed, check logs or try again.";
            });
    };

    $scope.saveReportSettings = function () {
        $scope.clearMsg();

        actionValue = {
            "action": $scope.onoffswitch
        };

        IncidentService.toggleAutoWeeklyReport(actionValue).then(
            function success(response) {
                console.log(JSON.stringify(response));
                if (response === "true") {
                    $scope.messages = "Saved successfully.";
                } else {
                    $scope.errormessages = "Failure on saving..";
                }
            },
            function error() {
                $scope.errormessages = "SETTING_SAVE_FAILURE - Error saving toggle setting, check logs or try again.";
            });
    };

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errormessages = null;
    };

});
