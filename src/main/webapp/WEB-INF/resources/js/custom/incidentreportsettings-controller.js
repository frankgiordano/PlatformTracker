app.controller('IncidentReportSettingsController', function($http, $rootScope, $filter, $scope, IncidentService, helperService) {
	
	$scope.init = function() {
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
                    $scope.errormessages = "Retrieving toggle status failed, contact administrator.";
                });	
    };
    
    $scope.saveReportSettings = function() {
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
                    $scope.errormessages = "Save operation failure, contact administrator.";
                });	
    };
    
    $scope.clearMsg = function() {
    	$scope.messages = null;
    	$scope.errormessages = null;
    };
    
});
