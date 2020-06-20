app.controller('IncidentFromGroupController', function ($routeParams, $location, $rootScope, localStorageService, $filter, $scope, IncidentGroupService, ngTableParams, groupStatuses) {
    $scope.init = function () {
        localStorageService.remove("incidentCreateButtonClicked");
        localStorageService.remove("incidentEditMode");
        IncidentGroupService.getGroups().then(
            function success(response) {
                $scope.groups = response;
                if ($routeParams.id) {
                    $scope.changedGroup($routeParams.id);
                }
            },
            function error() {
                $rootScope.errors.push({
                    code: "GROUPS_GET_FAILURE",
                    message: "Error retrieving groups."
                });
            });
    };

    // set the defaults
    $scope.groupStatuses = groupStatuses;
    $scope.selectedGroup = null;
    $scope.createResolution = null; // this variable handles the display of the resolution creation sub form
    $scope.createRootCA = null; // this variable handles the display of the RCA creation sub form
    $scope.disableButton = false;
    $scope.hideDuringLoading = false;

    $scope.waiting = function (value) {
        if (value === true) {
            $scope.hideDuringLoading = true;
            $scope.loading = false;
            document.body.style.cursor = "wait";
        } else {
            $scope.hideDuringLoading = false;
            $scope.loading = true;
            document.body.style.cursor = "default";
        }
    };
    $scope.waiting();

    var data = [];

    $scope.tableParams = new ngTableParams({
        page: 1, // show first page
        count: 10, // count per page
        sorting: {
            startTime: 'desc' // initial sorting
        }
    }, {
        total: data.length, // length of data
        getData: function ($defer, params) {
            // use build-in angular filter
            var filteredData = params.filter() ?
                $filter('filter')(data, params.filter()) :
                data;
            var orderedData = params.sorting() ?
                $filter('orderBy')(filteredData, params.orderBy()) :
                data;
            params.total(orderedData.length); // set total for recalc pagination
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

    $scope.refreshData = function () {
        var newData = $scope.init();
        $scope.rowCollection = newData;
    };

    $scope.select = function (object, sourceLocation) {
        $scope.selectedIncident = object;
        $location.path('/incident/edit/' + sourceLocation + '/' + $scope.selectedIncident.id + '/' + $scope.selectedGroup.id);
        $scope.clearDisplayMessages();
    };

    $scope.changedGroup = function (id) {
        var value = null;
        if (id)
            value = Number(id);
        else
            value = Number($scope.selectedGroup.id);

        IncidentGroupService.getGroupIncidents(value).then(
            function success(response) {
                var group = $scope.groups.filter(function (item) {
                    return item.id === value;
                });
                $scope.selectedGroup = group[0];

                data = response;
                $scope.tableParams.total(data.length);
                $scope.tableParams.reload();
                $scope.tableParams.sorting({ startTime: 'desc' });
            },
            function error() {
                $rootScope.errors.push({
                    code: "GROUPS_GET_FAILURE",
                    message: "Error retrieving groups."
                });
            });
    };

    $scope.clearDisplayMessages = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
    };

});
