app.run(function ($rootScope, $http, $location, Base64Service, AuthenticationService, localStorageService, editableOptions) {

    editableOptions.theme = 'bs3';

    $rootScope.errors = [];
    $rootScope.requests401 = [];
    $rootScope.navigateTo = "/main";

    $rootScope.$on('$routeChangeSuccess', function (event, next, current) {
        $rootScope.user = localStorageService.get('localStorageUser');
    });

    /**
     * Holds all the requests which failed due to 401 response.
     */
    $rootScope.$on('event:loginRequired', function () {
        $rootScope.requests401 = [];

        if ($location.path().indexOf("/login") == -1) {
            $rootScope.navigateTo = $location.path();
        }

        $location.path('/login');
    });

    /**
     * On 'event:loginConfirmed', resend all the 401 requests.
     */
    $rootScope.$on('event:loginConfirmed', function () {
        var i,
            requests = $rootScope.requests401,
            retry = function (req) {
                $http(req.config).then(function (response) {
                    req.deferred.resolve(response);
                });
            };

        for (i = 0; i < requests.length; i += 1) {
            retry(requests[i]);
        }

        $rootScope.requests401 = [];
        $rootScope.errors = [];
    });

    /**
     * On 'event:loginRequest' send credentials to the server.
     */
    $rootScope.$on('event:loginRequest', function (event, username, password) {
        // set the basic authentication header that will be parsed in the next request and used to authenticate
        $http.defaults.headers.common['Authorization'] = 'Basic ' + Base64Service.encode(username + ':' + password);

        AuthenticationService.login().then(
            function success() {
                $rootScope.user = localStorageService.get('localStorageUser');
                $rootScope.$broadcast('event:loginConfirmed');
                console.log("You have been successfully logged in.")
            },
            function error() {
                $rootScope.errors.push({ code: "LOGIN_FAILED", message: $rootScope.errormessages });
            });
    });

    $rootScope.$on('event:auth-login-failed', function (e, status) {
        //      var error = "Login failed.";
        if (status == 401) {
            //          error = "Invalid Username or Password.";
        } else if (status == 403) {
            error = "You don't have permission to access.";
        }
        //        $rootScope.errormessages = error;
    });

    /**
     * On 'logoutRequest' invoke logout on the server.
     */
    $rootScope.$on('event:logoutRequest', function () {
        $http.defaults.headers.common.Authorization = null;

        AuthenticationService.logout().then(
            function success() {
                $rootScope.user = localStorageService.get('localStorageUser');
                console.log("You have been successfully logged out.")
            },
            function error() {
                $rootScope.errors.push({ code: "LOGOUT_FAILED", message: "Oooooops something went wrong, please try again" });
            })
    });
});