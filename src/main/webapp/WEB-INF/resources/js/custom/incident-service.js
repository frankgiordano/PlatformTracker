app.service('IncidentService', function ($http, $q, ReferenceDataService, IncidentGroupService) {
    this.getIncidents = function () {
        var d = $q.defer();

        $http.get('incident/incidents/retrieve')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.search = function (search, pageno) {
        var d = $q.defer();

        $http.get('incident/retrieve/' + encodeURI(search.tag) + '/' + encodeURI(search.desc) + '/' + encodeURI(search.assignee) + '/' + pageno)
        .success(function (response) {
            d.resolve(response);
        })
        .error(function () {
            d.reject();
        });

        return d.promise;
    };

    this.getErrorConditions = function () {
        var d = $q.defer();

        $http.get('condition/errors')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getGroup = function (id) {
        var d = $q.defer();
        
        $http.get('incident/retrieve/group/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getProducts = function (id) {
        var d = $q.defer();

        $http.get('incident/retrieve/products/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getErrorCode = function (id) {
        var d = $q.defer();

        $http.get('incident/retrieve/errorcode/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getApplicationStatus = function (id) {
        var d = $q.defer();

        $http.get('incident/retrieve/applicationstatus/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getChronologies = function (id) {
        var d = $q.defer();

        $http.get('incident/retrieve/chronologies/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getIncidentPlus = function (id) {
        var deferred = $q.defer();
        var promise1 = IncidentGroupService.getGroups();
        var promise2 = this.getChronologies(id);
        var promise3 = this.getErrorConditions();
        var promise4 = ReferenceDataService.getApplicationStatus();
        var promise5 = this.getProducts(id);
        var promise6 = this.getIncident(id);
        $q.all([promise1, promise2, promise3, promise4, promise5, promise6]).then(function (data) {
            deferred.resolve(data);
        },
            function (errors) {
                deferred.reject(errors);
            });
        return deferred.promise;
    };

    this.getIncident = function (id) {
        var d = $q.defer();

        $http.get('incident/retrieve/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.deleteIncident = function (id) {
        var d = $q.defer();

        $http.delete('incident/delete/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.generateWeeklyIncidentReport = function (address) {
        var d = $q.defer();

        $http.post('incidentreport/weeklyreport', address)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.generateIncidentReportByProduct = function (incidentReport) {
        var d = $q.defer();

        $http.post('incidentreport/byproduct', incidentReport)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.toggleAutoWeeklyReport = function (onoffswitch) {
        var d = $q.defer();

        $http.post('incidentreport/toggle', onoffswitch)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.isToggleAutoWeeklyReport = function () {
        var d = $q.defer();

        $http.get('incidentreport/isToggle')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.saveIncident = function (incident) {
        var d = $q.defer();

        $http.post('incident/save', incident)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function (data) {
                d.reject(data);
            });

        return d.promise;
    };
    
});

app.service('ChronologyService', function ($http, $q) {

    this.saveChronology = function (chronology) {
        var d = $q.defer();

        $http.post('chronology/save', chronology)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function (data) {
                d.reject(data);
            });

        return d.promise;

    };

    this.deleteChronology = function (id) {
        var d = $q.defer();

        $http.delete('chronology/delete/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

});