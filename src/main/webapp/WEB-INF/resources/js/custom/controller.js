app.value('statuss', [{
    name: 'Unresolved',
    value: 'Unresolved'
}, {
    name: 'Mitigated',
    value: 'Mitigated'
}, {
    name: 'Resolved',
    value: 'Resolved'
}]);

app.value('incidentstatuss', [{
    name: 'Open',
    value: 'Open'
}, {
    name: 'Closed',
    value: 'Closed'
}]);

app.value('recipents', [{
    name: 'Desktop Operations Only',
    value: 'Desktop Operations Only'
}, {
    name: 'Regular Distribution',
    value: 'Regular Distribution'
}, {
    name: 'Escalated Distribution',
    value: 'Escalated Distribution'
}]);

app.value('platforms', [{
    name: 'GEN0',
    value: 'GEN0'
}, {
    name: 'GEN1',
    value: 'GEN1'
}, {
    name: 'GEN2',
    value: 'GEN2'
}, {
    name: 'GEN3',
    value: 'GEN3'
}, {
    name: 'n\\a',
    value: 'n\\a'
}]);

app.value('options', [{
    name: 'P1',
    value: 'P1'
}, {
    name: 'P2',
    value: 'P2'
}]);

app.value('errors', [{
    name: 'Application process not running',
    value: 'Application process not running'
}, {
    name: 'Database errors in Logs',
    value: 'Database errors in Logs'
}, {
    name: 'Database Integrity Errors',
    value: 'Database Integrity Errors'
}, {
    name: 'Disconnected Printers',
    value: 'Disconnected Printers'
}, {
    name: 'Errors in Communication Logs',
    value: 'Errors in Communication Logs'
}, {
    name: 'None Detected',
    value: 'None Detected'
}, {
    name: 'Service not responding',
    value: 'Service not responding'
}, {
    name: 'System failure notification',
    value: 'System failure notification'
}, {
    name: '',
    value: null
}]);

app.value('locuss', [{
    name: 'Internal',
    value: 'Internal'
}, {
    name: 'Cloud Comm',
    value: 'Cloud Comm'
}, {
    name: 'Cloud Sys',
    value: 'Cloud Sys'
}, {
    name: 'Internet',
    value: 'Internet'
}]);

app.value('alerted_bys', [{
    name: 'Nagios',
    value: 'Nagios'
}, {
    name: 'End user',
    value: 'End user'
}, {
    name: 'Operations',
    value: 'Operations'
}, {
    name: 'JVM Heap Memory Monitor',
    value: 'JVM Heap Memory Monitor'
}, {
    name: 'Unknown',
    value: 'Unknown'
}]);

app.controller('MainController', function ($route, $rootScope, $scope, $location) {

    var SAVE_ERROR_MSG = "_SAVE_FAILURE - ";
    var NUMBER_FIELD_WARNING = "Be aware to not enter a string value on a number field.";
    var CHECK_REQUIRED_FIELDS_MSG = "Check required fields: ";
    $rootScope.REQUIRED_FIELDS_PRODUCT_MSG = CHECK_REQUIRED_FIELDS_MSG + "Platform, Incident Name, Start Date, Client Name, Short Name (10 character field) and Max Weekly Uptime. " + NUMBER_FIELD_WARNING;
    $rootScope.REQUIRED_FILEDS_PROJECT_MSG = CHECK_REQUIRED_FIELDS_MSG + "Name, Owner, Recording Date, Description and Due Date. " + NUMBER_FIELD_WARNING;
    $rootScope.REQUIRED_FILEDS_RC_MSG = CHECK_REQUIRED_FIELDS_MSG + "Incident Group Name and Owner.";
    $rootScope.REQUIRED_FILEDS_RESOLUTION_MSG = CHECK_REQUIRED_FIELDS_MSG + "Incident Group name, Horizon, Owner, Estimated Completion Date and Description.";
    $rootScope.REQUIRED_FILEDS_INCIDENT_MSG = CHECK_REQUIRED_FIELDS_MSG + "Start Date Time, Locus, Description, Error Condition and Products.";
    $rootScope.REQUIRED_FILEDS_CHRONOLOGY_MSG = CHECK_REQUIRED_FIELDS_MSG + "Date Time and Description.";
    $rootScope.REQUIRED_FILEDS_GROUP_MSG = CHECK_REQUIRED_FIELDS_MSG + "Name and Description.";

    $rootScope.PRODUCT_SAVE_ERROR_MSG = "PRODUCT" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FIELDS_PRODUCT_MSG;
    $rootScope.PROJECT_SAVE_ERROR_MSG = "PROJECT" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FILEDS_PROJECT_MSG;
    $rootScope.RC_SAVE_ERROR_MSG = "ROOT_CAUSE" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FILEDS_RC_MSG;
    $rootScope.RESOLUTION_SAVE_ERROR_MSG = "RESOLUTION" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FILEDS_RESOLUTION_MSG;
    $rootScope.INCIDENT_SAVE_ERROR_MSG = "INCIDENT" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FILEDS_INCIDENT_MSG;
    $rootScope.INCIDENT_CHRONOLOGY_SAVE_ERROR_MSG = "CHRONOLOGY" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FILEDS_CHRONOLOGY_MSG;
    $rootScope.INCIDENT_GROUP_SAVE_ERROR_MSG = "GROUP" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FILEDS_GROUP_MSG;


    $scope.linkClicked = function (link) {
        $route.reload();
        $location.path(link);
    };

    $scope.logout = function () {
        $scope.$emit('event:logoutRequest');
        $location.path("/main");
    };

    $scope.login = function (credentials) {
        $scope.$emit('event:loginRequest', credentials.email, credentials.password);
        $location.path($rootScope.navigateTo);
    };

    $scope.$on('event:auth-login-failed', function (e, status) {
        // var error = "Login failed.";
        if (status == 401) {
            // error = "Invalid Username or Password.";
        } else if (status == 403) {
            error = "You don't have permission to access.";
        }
        // $scope.errormessages = error;
    });

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errormessages = null;
    };

});

app.controller('ModalController', function ($scope, close) {

    $scope.close = function (result) {
        close(result, 500); // close, but give 500ms for bootstrap to animate
    };

});
