app.service('IncidentGroupService', function ($http, $q) {
    this.getGroup = function (id) {
        var d = $q.defer();

        $http.get('group/retrieve/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getGroups = function () {
        var d = $q.defer();

        $http.get('group/groups/retrieve')
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
        $http.get('group/groups/retrieve/' + searchTerm + '/' + pageno)
        .success(function (response) {
            d.resolve(response);
        })
        .error(function () {
            d.reject();
        });

        return d.promise;
    };

    this.getGroupIncidents = function (id) {
        var d = $q.defer();

        $http.get('group/incidents/retrieve/' + id)
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

        $http.get('group/retrieve/resolutions/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.saveGroup = function (group) {
        var d = $q.defer();

        $http.post('group/save', group)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.deleteGroup = function (id) {
        var d = $q.defer();

        $http.delete('group/delete/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function (response) {
                d.reject(response);
            });

        return d.promise;
    };

    this.deleteAllGroupOrphans = function () {
        var d = $q.defer();

        $http.delete('group/deleteallorphans')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

});