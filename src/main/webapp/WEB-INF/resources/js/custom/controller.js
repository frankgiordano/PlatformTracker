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
    name: 'Code',
    value: 'Code'
}, {
    name: 'Linux',
    value: 'Linux'
}, {
    name: 'Windows',
    value: 'Windows'
}, {
    name: 'z/OS',
    value: 'z/OS'
}, {
    name: 'Other',
    value: 'other'
}]);

app.value('severities', [{
    name: 'P1',
    value: 'P1'
}, {
    name: 'P2',
    value: 'P2'
}, {
    name: 'P3',
    value: 'P3'
}, {
    name: 'P4',
    value: 'P4'
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

app.controller('MainController', function ($route, $rootScope, $scope, $location, $window, localStorageService) {

    var SAVE_ERROR_MSG = "_SAVE_FAILURE - ";
    var GENERATE_ERROR_MSG = "_GENERATE_FAILURE - ";
    var CHECK_REQUIRED_FIELDS_MSG = "Check required fields: ";
    var GENERIC_ERROR_MSG = "Check for required fields or check console for error messages.";
    $rootScope.REQUIRED_FIELDS_CHRONOLOGY_MSG = CHECK_REQUIRED_FIELDS_MSG + "Date Time and Description.";
    $rootScope.PROJECT_LINK_RESOLUTON_MSG = "Linking Resolution(s) to Project failed.";
    $rootScope.INCIDENT_VERSION_MSG = "Editing an older Incident detail version, please reload Incident detail from search page and try again.";

    $rootScope.PRODUCT_SAVE_ERROR_MSG = "PRODUCT" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.PROJECT_SAVE_ERROR_MSG = "PROJECT" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.RC_SAVE_ERROR_MSG = "ROOT_CAUSE" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.RESOLUTION_SAVE_ERROR_MSG = "RESOLUTION" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.INCIDENT_SAVE_ERROR_MSG = "INCIDENT" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.INCIDENT_CHRONOLOGY_SAVE_ERROR_MSG = "CHRONOLOGY" + SAVE_ERROR_MSG + $rootScope.REQUIRED_FIELDS_CHRONOLOGY_MSG;
    $rootScope.INCIDENT_GROUP_SAVE_ERROR_MSG = "GROUP" + SAVE_ERROR_MSG + GENERIC_ERROR_MSG;
    $rootScope.PROJECT_LINK_RESOLUTON_ERROR_MSG = "PROJECT_LINK_RESOLUTON" + SAVE_ERROR_MSG + $rootScope.PROJECT_LINK_RESOLUTON_MSG;
    $rootScope.INCIDENT_VERSION_ERROR_MSG = "INCIDENT_VERSION" + SAVE_ERROR_MSG + $rootScope.INCIDENT_VERSION_MSG;
    $rootScope.REPORT_SAVE_ERROR_MSG = "REPORT" + GENERATE_ERROR_MSG + GENERIC_ERROR_MSG;

    $scope.linkClicked = function (link) {
        $route.reload();
        $location.path(link);
    };

    $scope.clearLocalStorage = function () {
        localStorageService.remove('incidentCreateButtonClicked');
        localStorageService.remove('incidentEditMode');
    }

    $scope.checkLoginUser = function () {
        if ($rootScope.user === null) {
            $scope.$emit('event:logoutRequest');
        }
    }

    // this is a workaround method in place of $scope.checkLoginUser 
    // use for those screens where ng-hide is used or child popup screen
    // to handle the parent screen.. 
    $scope.checkLoginUserFromLocalStorage = function () {
        $rootScope.user = localStorageService.get('localStorageUser');
        if ($rootScope.user === null) {
            $scope.$emit('event:logoutRequest');
            $location.path("/main");
        }
    }

    $scope.logout = function () {
        $scope.$emit('event:logoutRequest');
        $location.path("/main");
    };

    // sleep time expects milliseconds
    function sleep(time) {
        return new Promise((resolve) => setTimeout(resolve, time));
    }

    $scope.login = function (credentials) {
        $rootScope.loginButton = true;
        $scope.$emit('event:loginRequest', credentials.email, credentials.password);
        sleep(2).then(() => {
            $location.path($rootScope.navigateTo);
        });
    };

    $scope.$on('event:auth-login-failed', function (e, status) {
        var errorMsg = "";
        if (status == 401) {
            errorMsg = "Invalid Username or Password.";
        } else if (status == 403) {
            errorMsg = "You don't have permission to access.";
        }
        if ($rootScope.loginButton === true && errorMsg.length !== 0) {
            $rootScope.errorLoginMsg = "LOGIN_FAILURE - " + errorMsg; 
            $rootScope.loginButton = false;
        }
    });

    $scope.$on('event:auth-login-confirmed', function (e, status) {
        $rootScope.loginButton = false;
        $rootScope.errorLoginMsg = null;
    });

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
    };

    $scope.help = function () {
        $window.open('resources/html/partials/common/UsersGuide.pdf', '_blank');
    }

});

app.controller('ModalController', function ($scope, close) {

    $scope.close = function (result) {
        close(result, 500); // close, but give 500ms for bootstrap to animate
    };

});
