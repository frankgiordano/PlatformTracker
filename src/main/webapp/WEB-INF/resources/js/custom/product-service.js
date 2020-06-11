app.service('ProductService', function ($http, $q) {
    this.getProducts = function () {
        var d = $q.defer();

        $http.get('product/products/retrieve')
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
        $http.get('product/products/retrieve/' + encodeURI(searchTerm) + '/' + pageno)
        .success(function (response) {
            d.resolve(response);
        })
        .error(function () {
            d.reject();
        });

        return d.promise;
    };

    this.getActiveProducts = function () {
        var d = $q.defer();

        $http.get('product/products/retrieveactive')
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getProductIncidents = function (id) {
        var d = $q.defer();

        $http.get('product/incidents/retrieve/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.saveProduct = function (product) {
        var d = $q.defer();

        $http.post('product/save', product)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.deleteProduct = function (id) {
        var d = $q.defer();

        $http.delete('product/delete/' + id)
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

});