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

app.controller('MainController', function($rootScope, $scope, $location) {
    $scope.logout = function() {
        $scope.$emit('event:logoutRequest');

        $location.path("/main");
    };

    $scope.login = function(credentials) {
        $scope.$emit('event:loginRequest', credentials.email, credentials.password);

        $location.path($rootScope.navigateTo);
    };
    
    $scope.$on('event:auth-login-failed', function(e, status) {
//        var error = "Login failed.";
        if (status == 401) {
//          error = "Invalid Username or Password.";
        }else if(status == 403) {
          error = "You don't have permission to access.";
        }
//        $scope.errormessages = error;
      });
    
    $scope.clearMsg = function() {
        $scope.messages = null;
        $scope.errormessages = null;
    };    
    
});

app.controller('ModalController', function($scope, close) {

    $scope.close = function(result) {
        close(result, 500); // close, but give 500ms for bootstrap to animate
    };

});
