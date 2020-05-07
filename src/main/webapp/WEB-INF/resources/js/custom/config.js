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
                    '/incident/globalsearch', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/search.html'
                })
                .when(
                    '/incident/create', {
                    controller: 'IncidentController',
                    templateUrl: 'resources/html/partials/view/incident/create.html'
                })
                .when(
                    '/incident/edit/:sourceLocation/:id', {
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
                    templateUrl: 'resources/html/partials/view/resolution/resolution_search.html',
                })
                .when(
                    '/resolution/create/:incidentGroup', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/resolution_create.html'
                })
                .when(
                    '/resolution/create', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/resolution_create.html'
                })
                .when(
                    '/resolution/edit/:id', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/resolution_edit.html'
                })
                .when(
                    '/resolution/retrieve/:id', {
                    controller: 'ResolutionController',
                    templateUrl: 'resources/html/partials/view/resolution/resolution_edit.html'
                })
                .when(
                    '/resolution/linkProject/:project', {
                    controller: 'ResolutionProjectLinkingController',
                    templateUrl: 'resources/html/partials/view/resolution/resolution_link_project.html'
                })
                .when(
                    '/rootcause/search', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/search.html'
                })
                .when(
                    '/rootcause/create', {
                    controller: 'RootCauseController',
                    templateUrl: 'resources/html/partials/view/rootcause/create.html'
                })
                .when(
                    '/rootcause/edit/:id', {
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
                    '/project/create', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/create.html'
                })
                .when(
                    '/project/edit/:id', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/edit.html'
                })
                .when(
                    '/project/delete/:id', {
                    controller: 'ProjectController',
                    templateUrl: 'resources/html/partials/view/project/delete.html'
                })
                .when(
                    '/product/globalsearch', {
                    controller: 'ProductController',
                    templateUrl: 'resources/html/partials/view/product/product_search.html'
                })
                .when(
                    '/product/create', {
                    controller: 'ProductController',
                    templateUrl: 'resources/html/partials/view/product/product_create.html'
                })
                .when(
                    '/report/incidentreport', {
                    controller: 'IncidentReportController',
                    templateUrl: 'resources/html/partials/view/report/incident_search_report.html'
                })
                .when(
                    '/report/incidentweeklyreport', {
                    controller: 'IncidentWeeklyReportController',
                    templateUrl: 'resources/html/partials/view/report/incident_weekly_report.html'
                })
                .when(
                    '/report/incidentreportbyproduct', {
                    controller: 'IncidentWeeklyReportController',
                    templateUrl: 'resources/html/partials/view/report/incident_report_by_product.html'
                })
                .when(
                    '/report/incidentreportsettings', {
                    controller: 'IncidentWeeklyReportController',
                    templateUrl: 'resources/html/partials/view/report/incident_report_settings.html'
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