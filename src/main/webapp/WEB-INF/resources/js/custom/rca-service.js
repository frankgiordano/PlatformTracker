app.service('RcaService', function ($http, $q, ReferenceDataService, IncidentGroupService) {
    this.getRcas = function () {
        var d = $q.defer();

        $http.get('rootcause/search')
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
        $http.get('rootcause/retrieve/' + search.grpName + '/' + search.desc + '/' + pageno)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getRcas = function (rca) {
        var d = $q.defer();

        if (undefined == rca) {
            $http.get('rootcause/search')
                .success(function (response) {
                    d.resolve(response);
                })
                .error(function () {
                    d.reject();
                });
        } else {
            $http.post('rootcause/searchby', rca)
                .success(function (response) {
                    d.resolve(response);
                })
                .error(function () {
                    d.reject();
                });
        }

        return d.promise;
    };

    this.getRca = function (id) {

        var deferred = $q.defer();
        var promise1 = IncidentGroupService.getGroups();
        var promise2 = ReferenceDataService.getCategories();
        var promise3 = ReferenceDataService.getStatus();
        var promise4 = ReferenceDataService.getResources();
        var promise5 = this.getRca1(id);
        $q.all([promise1, promise2, promise3, promise4, promise5]).then(function (data) {
            deferred.resolve(data);
        },
            function (errors) {
                deferred.reject(errors);
            });
        return deferred.promise;
    };

    this.getRca1 = function (id) {
        var d = $q.defer();

        $http.get('rootcause/get/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.saveRca = function (rca) {
        var d = $q.defer();

        $http.post('rootcause/save', rca)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.deleteRca = function (rca) {
        var d = $q.defer();

        $http.delete('rootcause/delete/' + rca.id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

});

app.service('OwnersService', function ($http, $q) {
    this.getOwners = function () {
        var d = $q.defer();

        $http.get('ownerService/ownersInfo')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };
});