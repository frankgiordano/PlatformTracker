app.service('ProjectService', function ($http, $q, ReferenceDataService, IncidentGroupService) {
    this.getProjects = function () {
        var d = $q.defer();

        $http.get('project/search')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getProject = function (id) {
        var deferred = $q.defer();
        var promise1 = ReferenceDataService.getStatus();
        var promise2 = ReferenceDataService.getPdlcStatus();
        var promise3 = ReferenceDataService.getWikiTypes();
        var promise4 = this.getProject1(id);
        $q.all([promise1, promise2, promise3, promise4]).then(function (data) {
            deferred.resolve(data);
        },
            function (errors) {
                deferred.reject(errors);
            });
        return deferred.promise;
    };

    this.getProject1 = function (id) {
        var d = $q.defer();

        $http.get('project/get/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.saveProject = function (project) {
        var d = $q.defer();

        $http.post('project/save', project)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.deleteProject = function (project) {
        var d = $q.defer();

        $http.delete('project/delete/' + project.id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

});