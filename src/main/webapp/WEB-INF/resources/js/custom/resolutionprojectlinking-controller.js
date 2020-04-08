app.controller('ResolutionProjectLinkingController', function ($rootScope, $scope, ResolutionService, $routeParams, $window) {
    
    $scope.hidebutton = false;
    $scope.filterOptions = {
        filterText: ''
    };
    $scope.resolution = {};
    $scope.selected = [];
    $scope.gridOptions = {
        data: 'myData',
        showSelectionCheckbox: true,
        selectedItems: $scope.selected,
        filterOptions: $scope.filterOptions,
        showFilter: true,
        showFooter: true,
        columnDefs: [{
            field: "description",
            displayName: 'Description',
            width: '40%',
            pinned: true
        }, {
            field: "resolutionProjectName",
            displayName: 'Resolution Project',
            width: '13%'
        }, {
            field: "incidentGroupName",
            displayName: 'Incident Group Name',
            width: '40%',
            pinned: true
        }, {
            field: "owner",
            displayName: 'Owner',
            width: '40%'
        }]
    };
    $scope.myData = [];

    $scope.waiting = function (value) {
        if (value == true) {
            $scope.hideduringloading = true;
            $scope.loading = false;
            document.body.style.cursor = "wait";
        } else {
            $scope.hideduringloading = false;
            $scope.loading = true;
            document.body.style.cursor = "default";
        }
    };
    $scope.waiting(false);

    $scope.linkResolutions = function () {
        ResolutionService.getResolutions().then(
            function success(response, status, headers, config) {
                var i, resolution;
                for (i = 0; i < response.length; ++i) {
                    resolution = response[i];
                    if (resolution.projectId == $routeParams.project) {
                        $scope.selected.push(resolution);
                    }
                }
                $scope.myData = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "RESOLUTIONS_GET_FAILURE",
                    message: "Error retrieving resolutions."
                });
            });
    };

    $scope.$on('ngGridEventData', function () {
        // $scope.gridOptions.selectRow(0, true);
    });

    $scope.link = function () {
        $scope.waiting(true);
        $scope.getSelectedRows();
    }

    $scope.getSelectedRows = function () {
        $scope.clearMsg();
        var resolutions = [];
        var inputs = [];
        for (i = 0; i < $scope.myData.length; ++i) {
            var resolution = $scope.myData[i];
            if (resolution.projectId == $routeParams.project) {
                resolutions.push(resolution);
            }
        }

        var selectedRows = $scope.gridOptions.$gridScope.selectedItems;
        for (i = 0; i < selectedRows.length; ++i) {
            var item = selectedRows[i];
            if (resolutions.indexOf(item) > -1) {
                resolutions.splice(resolutions.indexOf(item), 1);
                continue;
            } else {
                inputs.push({
                    "id": selectedRows[i].id,
                    "projectId": $routeParams.project,
                    "operation": 1
                });
            }
        }

        for (i = 0; i < resolutions.length; ++i) {
            var item = resolutions[i];
            inputs.push({
                "id": item.id,
                "projectId": $routeParams.project,
                "operation": 2
            });
        }

        if (inputs.length == 0) {
            $scope.messages = "No updates to process.";
            $scope.waiting(false);
            return;
        }

        ResolutionService.saveLinkedResolutions(inputs).then(
            function success(response) {
                var numForOpOne = 0;
                var numForOpTwo = 0;
                var message = "";
                response.forEach(element => {
                    if (element.operation === 1)
                        numForOpOne++;
                    if (element.operation === 2)
                        numForOpTwo++;
                });
                if (numForOpOne > 0) {
                    if (numForOpOne === 1)
                        message = "Linked " + numForOpOne + " resolution to Project ID " + $routeParams.project + " successfully. ";
                    else 
                        message = "Linked " + numForOpOne + " resolutions to Project ID " + $routeParams.project + " successfully. ";
                }
                if (numForOpTwo > 0) {
                    if (numForOpTwo === 1)
                        message = message +  "Unlinked " + numForOpTwo + " resolution to Project ID " + $routeParams.project + " successfully.";
                    else 
                        message = message + "Unlinked " + numForOpTwo + " resolutions to Project ID " + $routeParams.project + " successfully.";
                }
                $scope.messages = message;
                $scope.hidebutton = true;
                $scope.waiting(false);
                return;
            },
            function error() {
                $scope.errormessages = $rootScope.PROJECT_LINK_RESOLUTON_ERROR_MSG;
                $scope.waiting(false);
            });
    }

    $scope.close = function () {
        $window.close();
        $scope.clearMsg();
    }

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errormessages = null;
    }

});