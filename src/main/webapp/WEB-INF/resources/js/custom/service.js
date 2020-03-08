// Just for fun use JavaScript IIFE pattern to define a service instead of the pattern used
// for the rest here.
(function () {

    var helperService = function () {

        var sortByKey = function (array, key) {
            return array.sort(function (a, b) {
                var x = a[key];
                var y = b[key];
                return ((x < y) ? -1 : ((x > y) ? 1 : 0));
            });
        };

        var search = function (source, name) {
            var results = [];
            var index;
            var entry;

            for (index = 0; index < source.length; ++index) {
                entry = source[index];
                if (entry.name === name) {
                    return true;
                }
            }
            return false;
        };

        var compare = function (type) {
            if (type == "chronology") {
                return function compare(a, b) {
                    return new Date(a.dateTime).getTime() - new Date(b.dateTime).getTime();
                }
            } else {
                if (type == "resolution") {
                    return function compare(a, b) {
                        return new Date(a.estCompletionDate).getTime() - new Date(b.estCompletionDate).getTime();
                    }
                }
            }
        };

        return {
            sortByKey: sortByKey,
            search: search,
            compare: compare
        };

    };

    var module = angular.module("app");
    module.factory("helperService", helperService);

}());

app.service('AuthenticationService', function ($http, $q, localStorageService) {
    this.login = function () {
        var d = $q.defer();

        $http.get('user/authenticated')
            .success(function (user) {
                localStorageService.set('localStorageUser', user);

                d.resolve();
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.logout = function () {
        var d = $q.defer();

        $http.get('j_spring_security_logout')
            .success(function () {
                localStorageService.remove('localStorageUser');

                d.resolve();
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };
});

app.service('Base64Service', function () {
    var keyStr = "ABCDEFGHIJKLMNOP" +
        "QRSTUVWXYZabcdef" +
        "ghijklmnopqrstuv" +
        "wxyz0123456789+/" +
        "=";
    this.encode = function (input) {
        var output = "",
            chr1, chr2, chr3 = "",
            enc1, enc2, enc3, enc4 = "",
            i = 0;

        while (i < input.length) {
            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
                keyStr.charAt(enc1) +
                keyStr.charAt(enc2) +
                keyStr.charAt(enc3) +
                keyStr.charAt(enc4);
            chr1 = chr2 = chr3 = "";
            enc1 = enc2 = enc3 = enc4 = "";
        }

        return output;
    };

    this.decode = function (input) {
        var output = "",
            chr1, chr2, chr3 = "",
            enc1, enc2, enc3, enc4 = "",
            i = 0;

        // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {
            enc1 = keyStr.indexOf(input.charAt(i++));
            enc2 = keyStr.indexOf(input.charAt(i++));
            enc3 = keyStr.indexOf(input.charAt(i++));
            enc4 = keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

            chr1 = chr2 = chr3 = "";
            enc1 = enc2 = enc3 = enc4 = "";
        }
    };
});

app.service('ReferenceDataService', function ($http, $q) {

    this.getStatus = function (id) {
        var d = $q.defer();

        $http.get('reference/groupData/3', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getResources = function () {
        var d = $q.defer();

        $http.get('reference/groupData/4', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getCategories = function () {
        var d = $q.defer();

        $http.get('reference/groupData/5', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getHorizons = function () {
        var d = $q.defer();

        $http.get('reference/groupData/6', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getTypes = function () {
        var d = $q.defer();

        $http.get('reference/groupData/7', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getPdlcStatus = function () {
        var d = $q.defer();

        $http.get('reference/groupData/8', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getWikiTypes = function () {
        var d = $q.defer();

        $http.get('reference/groupData/9', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

    this.getApplicationStatus = function () {
        var d = $q.defer();

        $http.get('reference/groupData/10', {
            cache: true
        })
            .success(function (response) {
                d.resolve(response);
            })
            .error(function () {
                d.reject();
            });

        return d.promise;
    };

});