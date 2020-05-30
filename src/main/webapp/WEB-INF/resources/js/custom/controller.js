app.value('groupStatuses', [{
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

app.value('severities', [{
    name: 'P1',
    value: 'P1'
}, {
    name: 'P2',
    value: 'P2'
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
    var CHECK_REQUIRED_FIELDS_MSG = "Check required fields: ";
    var GENERIC_ERROR_MSG = "Check for required fields or check console for error messages.";
    $rootScope.REQUIRED_FIELDS_CHRONOLOGY_MSG = CHECK_REQUIRED_FIELDS_MSG + "Date Time and Description.";
    $rootScope.PROJECT_LINK_RESOLUTON_MSG = "Linking Resolution(s) to Project failed.";
    $rootScope.INCIDENT_VERSION_MSG = "Editing an older Incident detail version, please reload Incident detail from search page and try again. You may need to click on the Refresh Data button if one exist on the page.";

    $rootScope.PRODUCT_SAVE_ERROR_MSG = "PRODUCT" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.PROJECT_SAVE_ERROR_MSG = "PROJECT" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.RC_SAVE_ERROR_MSG = "ROOT_CAUSE" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.RESOLUTION_SAVE_ERROR_MSG = "RESOLUTION" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.INCIDENT_SAVE_ERROR_MSG = "INCIDENT" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.INCIDENT_CHRONOLOGY_SAVE_ERROR_MSG = "CHRONOLOGY" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FIELDS_CHRONOLOGY_MSG;
    $rootScope.INCIDENT_GROUP_SAVE_ERROR_MSG = "GROUP" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.PROJECT_LINK_RESOLUTON_ERROR_MSG = "PROJECT_LINK_RESOLUTON" + SAVE_ERROR_MSG + $rootScope.PROJECT_LINK_RESOLUTON_MSG;
    $rootScope.INCIDENT_VERSION_ERROR_MSG = "INCIDENT_VERSION" + SAVE_ERROR_MSG + $rootScope.INCIDENT_VERSION_MSG;

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
