app.factory('responseObserver', function responseObserver($q, $window, $rootScope) {
    return function (promise) {
        return promise.then(function (successResponse) {
            return successResponse;
        }, function (errorResponse) {
            switch (errorResponse.status) {
                case 401:
                    $window.location = $window.location;
                    $rootScope.$broadcast('event:auth-login-failed', errorResponse.status);
                    break;
                case 403:
                    $window.location = $window.location;
                    $rootScope.$broadcast('event:auth-login-failed', errorResponse.status);
                    break;
                case 500:
                    $window.location = $window.location;
                    $rootScope.$broadcast('event:auth-login-failed', errorResponse.status);
                    break;
            }
            return $q.reject(errorResponse);
        });
    };
});

app.config(function ($httpProvider) {
    $httpProvider.responseInterceptors.push('responseObserver');
}).config(
    [
        '$routeProvider',
        '$httpProvider',
        'localStorageServiceProvider',
        function ($routeProvider, $httpProvider,
            localStorageServiceProvider) {

            // ======= local storage configuration ========

            localStorageServiceProvider.prefix = 'example';

            // ======= router configuration =============

            $routeProvider
                .when(
                    '/main', {
                    templateUrl: 'resources/html/partials/view/main.html'
                })
                .when(
                    '/incident/search', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/search.html'
                })
                .when(
                    '/incident/search/:pageno/:search', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/search.html'
                })
                .when(
                    '/incident/create', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/create.html'
                })
                .when(
                    '/incident/create/:pageno/:search', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/create.html'
                })
                .when(
                    '/incident/edit/:sourceLocation/:id/:pageno/:search', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/edit.html'
                })
                .when(
                    '/incident/edit/:sourceLocation/:id/:gid', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/edit.html'
                })
                .when(
                    '/incident/fromgroupsearch', {
                    controller: 'IncidentFromGroupController',
                    templateUrl: 'resources/html/partials/view/incident/from_group_search.html'
                })
                .when(
                    '/incident/fromgroupsearch/:id', {
                    controller: 'IncidentFromGroupController',
                    templateUrl: 'resources/html/partials/view/incident/from_group_search.html'
                })
                .when(
                    '/incident/incidentgroupsearch', {
                    controller: 'IncidentGroupController',
                    templateUrl: 'resources/html/partials/view/incident_group/search.html'
                })
                .when(
                    '/resolution/search', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/search.html',
                })
                .when(
                    '/resolution/search/:pageno/:search', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/search.html',
                })
                .when(
                    '/resolution/create/fromRootCause/:incidentGroup', {
                    controller: 'ResolutionChildController',
                    templateUrl: 'resources/html/partials/view/resolution/create_from_rc.html'
                })
                .when(
                    '/resolution/create', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/create.html'
                })
                .when(
                    '/resolution/create/:pageno/:search', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/create.html'
                })
                .when(
                    '/resolution/edit/:id/:pageno/:search', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/edit.html'
                })
                .when(
                    '/resolution/retrieve/:id', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/edit.html'
                })
                .when(
                    '/resolution/linkProject/:project', {
                    controller: 'ResolutionProjectLinkingController',
                    templateUrl: 'resources/html/partials/view/resolution/link_project.html'
                })
                .when(
                    '/rootcause/search', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/search.html'
                })
                .when(
                    '/rootcause/search/:pageno/:search', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/search.html'
                })
                .when(
                    '/rootcause/create', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/create.html'
                })
                .when(
                    '/rootcause/create/:pageno/:search', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/create.html'
                })
                .when(
                    '/rootcause/edit/:id/:pageno/:search', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/edit.html'
                })
                .when(
                    '/rootcause/retrieve/:id', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/edit.html'
                })
                .when(
                    '/rootcause/delete/:id', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/delete.html'
                })
                .when(
                    '/project/search', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/search.html'
                })
                .when(
                    '/project/search/:pageno/:search', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/search.html'
                })
                .when(
                    '/project/create', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/create.html'
                })
                .when(
                    '/project/create/:pageno/:search', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/create.html'
                })
                .when(
                    '/project/edit/:id/:pageno/:search', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/edit.html'
                })
                .when(
                    '/project/delete/:id', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/delete.html'
                })
                .when(
                    '/product/search', {
                    controller: 'ProductController',
                    templateUrl: 'resources/html/partials/view/product/search.html'
                })
                .when(
                    '/product/search/:pageno/:search', {
                    controller: 'ProductController',
                    templateUrl: 'resources/html/partials/view/product/search.html'
                })
                .when(
                    '/product/create', {
                    controller: 'ProductController',
                    templateUrl: 'resources/html/partials/view/product/create.html'
                })
                .when(
                    '/product/create/:pageno/:search', {
                    controller: 'ProductController',
                    templateUrl: 'resources/html/partials/view/product/create.html'
                })
                .when(
                    '/report/incidentreport', {
                    controller: 'IncidentReportController',
                    templateUrl: 'resources/html/partials/view/incident_report/by_search.html'
                })
                .when(
                    '/report/incidentweeklyreport', {
                    controller: 'IncidentWeeklyReportController',
                    templateUrl: 'resources/html/partials/view/incident_report/by_weekly.html'
                })
                .when(
                    '/report/incidentreportbyproduct', {
                    controller: 'IncidentWeeklyReportController',
                    templateUrl: 'resources/html/partials/view/incident_report/by_product.html'
                })
                .when(
                    '/report/incidentreportsettings', {
                    controller: 'IncidentWeeklyReportController',
                    templateUrl: 'resources/html/partials/view/incident_report/settings.html'
                })
                .when(
                    '/login', {
                    templateUrl: 'resources/html/partials/view/login.html'
                }).otherwise({
                    redirectTo: "/main"
                });

            // ======== http configuration ===============

            // configure $http to view a login whenever a 401 unauthorized response arrives
            $httpProvider.responseInterceptors
                .push(function ($rootScope, $q) {
                    return function (promise) {
                        return promise
                            .then(
                                // success -> don't intercept
                                function (response) {
                                    return response;
                                },
                                // error -> if 401 save the request and broadcast an event
                                function (response) {
                                    if (response.status === 401) {
                                        var deferred = $q
                                            .defer(),
                                            req = {
                                                config: response.config,
                                                deferred: deferred
                                            };

                                        $httpProvider.defaults.headers.common.Authorization = null;

                                        $rootScope.requests401
                                            .push(req);
                                        $rootScope
                                            .$broadcast('event:loginRequired');

                                        return deferred.promise;
                                    }
                                    return $q
                                        .reject(response);
                                });
                    };
                });
        }
    ]);