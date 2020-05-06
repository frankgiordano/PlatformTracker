app.service('ResolutionService', function ($http, $q, ReferenceDataService, IncidentGroupService) {
    this.getResolutions = function () {
        var d = $q.defer();

        $http.get('incidentResolution/resolutions/retrieve')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.search = function (searchTerm, pageno) {
        var d = $q.defer();
        $http.get('incidentResolution/retrieve/' + searchTerm + '/' + pageno)
        .success(function (response) {
            d.resolve(response);
        })
        .error(function () {
            d.reject();
        });

        return d.promise;
    };

    this.getGroupResolutions = function (id) {
        var d = $q.defer();

        $http.get('incidentResolution/retrieve/resolutions/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.saveLinkedResolutions = function (resolutions) {
        var d = $q.defer();
        $http.post('incidentResolution/resolutions/linkProjects', resolutions)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getIncidentResolution = function (id) {
        var deferred = $q.defer();
        var promise1 = IncidentGroupService.getGroups();
        var promise2 = ReferenceDataService.getHorizons();
        var promise3 = ReferenceDataService.getStatus();
        var promise4 = ReferenceDataService.getTypes();
        var promise5 = this.getResolution(id);
        $q.all([promise1, promise2, promise3, promise4, promise5]).then(function (data) {
            deferred.resolve(data);
        },
            function (errors) {
                deferred.reject(errors);
            });
        return deferred.promise;
    };

    this.getResolution = function (id) {
        var d = $q.defer();

        $http.get('incidentResolution/resolutions/retrieve/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.saveResolution = function (resolution) {
        var d = $q.defer();

        $http.post('incidentResolution/save', resolution)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.deleteResolution = function (resolution) {
        var d = $q.defer();

        $http.delete('incidentResolution/delete/' + resolution.id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

});