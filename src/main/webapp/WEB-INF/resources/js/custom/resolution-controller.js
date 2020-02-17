app.controller('ResolutionRetrieveController', function ($http, $rootScope, $scope, OwnersService, ResolutionService, limitToFilter, $location, $routeParams, IncidentGroupService, ReferenceDataService, $filter, ModalService) {
    $scope.resolution = {};

    //    $scope.actions = [];

    //    $scope.filterAction = function(action) {
    //        return action.isDeleted !== true;
    //    };
    //
    //    $scope.deleteAction = function(id) {
    //        var filtered = $filter('filter')($scope.actions, {
    //            id: id
    //        });
    //        if (filtered.length) {
    //            filtered[0].isDeleted = true;
    //        }
    //        for (var i = $scope.actions.length; i--;) {
    //            var action = $scope.actions[i];
    //            if (action.isDeleted || action.name.trim().length == 0) {
    //                $scope.actions.splice(i, 1);
    //            }
    //        }
    //    };

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
                $scope.owners = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "OWNERS_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    })();

    //    $scope.addAction = function() {
    //        if ($scope.actions.length < 5) {
    //            $scope.actions.push({
    //                id: $scope.actions.length + 1,
    //                name: '',
    //                isNew: true
    //            });
    //        } else {}
    //    };

    if ($routeParams.id == null) {
        $scope.clearMsg();
        (function () {
            IncidentGroupService.getGroups().then(
                function success(response) {
                    $scope.groups = response;

                },
                function error() {
                    $rootScope.errors.push({
                        code: "GROUPS_GET_FAILURE",
                        message: "Oooooops something went wrong, please try again"
                    });
                });
        })();

        $scope.types1 = ReferenceDataService.getTypes().then(
            function success(response) {
                $scope.types = response;

                $scope.resolution.type = response[0];

            },
            function error() {
                $rootScope.errors.push({
                    code: "TYPES_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });

        $scope.status1 = ReferenceDataService.getStatus().then(
            function success(response) {
                $scope.status = response;

                $scope.resolution.status = response[0];
            },
            function error() {
                $rootScope.errors.push({
                    code: "STATUS_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });

        $scope.horizons1 = ReferenceDataService.getHorizons().then(
            function success(response) {
                $scope.horizons = response;

                $scope.resolution.horizon = response[0];
            },
            function error() {
                $rootScope.errors.push({
                    code: "HORIZONS_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    }

    $scope.filterOptions = {
        filterText: ''
    };

    $scope.getHeader = function () {
        return $scope.gridOptions.columnDefs.displayName;
    };

    $scope.myData = [];

    $scope.init = function () {
        var i, resolution;
        var projects = {};
        for (i = 0; i < gridData.length; ++i) {
            resolution = gridData[i];
            resolution.status = resolution.statusName;
            resolution.type = resolution.typeName;
            resolution.horizon = resolution.horizonName;
            resolution.incidentGroup = resolution.incidentGroupName;
            resolution.resolutionProject = resolution.resolutionProjectName;
        }
        $scope.myData = gridData;
    };

    $scope.gridOptions = {
        data: 'myData',
        filterOptions: $scope.filterOptions,
        showFooter: true,
        showGroupPanel: true,
        enableColumnResize: true,
        enableColumnReordering: true,
        columnDefs: [{
            field: "id",
            displayName: 'Resolution Id',
            width: '7%',
            cellTemplate: '<div class="ngCellText" ng-class="col.colIndex()"><a href="#/resolution/retrieve/{{row.getProperty(col.field)}}">{{row.getProperty(col.field)}}</a></div>',
            pinned: true
        }, {
            field: "incidentGroup",
            displayName: 'Incident Group Name',
            width: '13%',
            pinned: true
        }, {
            field: "description",
            displayName: 'Description',
            width: '36%',
            pinned: true
        }, {
            field: "horizon",
            displayName: 'Horizon',
            width: '7%'
        }, {
            field: "owner",
            displayName: 'Owner',
            width: '13%'
        }, {
            field: "resolutionProject",
            displayName: 'Resolution Project',
            width: '13%'
        }, {
            field: "actualCompletionDate",
            displayName: 'Actual Completion Date',
            width: '5%',
            cellFilter: "date:'yyyy-MM-dd'"
        }, {
            field: "estCompletionDate",
            displayName: 'Estimated Completion Date',
            width: '5%',
            cellFilter: "date:'yyyy-MM-dd'"
        }, {
            field: "status",
            displayName: 'Status',
            width: '7%'
        }, {
            field: "type",
            displayName: 'Type',
            width: '7%'
        }, {
            field: "sriArtifact",
            displayName: 'SRI Artifact',
            width: '5%'
        }]
    };

    $scope.getIncidentResolution = function () {
        ResolutionService.getIncidentResolution($routeParams.id).then(
            function success(response) {
                if (response) {
                    $scope.groups = response[0];
                    $scope.horizons = response[1];
                    $scope.status = response[2];
                    $scope.types = response[3];
                    $scope.resolution = response[4];

                    OwnersService.getOwners().then(
                        function success(response) {
                            $scope.owners = response;
                            var owners = $scope.resolution.owner;
                            var ownersList = [];
                            if (owners != null) {
                                ownersList = owners.split("|");
                            }
                            var newOwners = [];
                            for (var i in ownersList) {
                                for (var j in $scope.owners) {
                                    if (ownersList[i] === $scope.owners[j].userName) {
                                        $scope.owners[j].ticked = true;
                                    }
                                }
                            }
                        },
                        function error() {
                            $rootScope.errors.push({
                                code: "OWNERS_GET_FAILURE",
                                message: "Oooooops something went wrong, please try again"
                            });
                        });

                    //                    var actions = $scope.resolution.relatedActions;
                    //                    var actionsList = [];
                    //                    if (actions != null) {
                    //                        actionsList = actions.split("|");
                    //                    }
                    //                    var newActions = [];
                    //                    for (var i in actionsList) {
                    //                        newActions.push({
                    //                            id: $scope.actions.length + 1,
                    //                            name: actionsList[i],
                    //                            isNew: true
                    //                        });
                    //
                    //                    }
                    //                    $scope.actions = newActions;

                    var statusId = $scope.status.filter(function (item) {
                        return item.id === $scope.resolution.status.id;
                    });
                    $scope.resolution.status = statusId[0];

                    var typeId = $scope.types.filter(function (item) {
                        return item.id === $scope.resolution.type.id;
                    });
                    $scope.resolution.type = typeId[0];

                    var horizonId = $scope.horizons.filter(function (item) {
                        return item.id === $scope.resolution.horizon.id;
                    });
                    $scope.resolution.horizon = horizonId[0];

                    var incidentGroupId = $scope.groups.filter(function (item) {
                        return item.id === $scope.resolution.incidentGroup.id;
                    });
                    $scope.resolution.incidentGroup = incidentGroupId[0];

                } else {
                    console.error("Unable to retrieve resolution for resolution " + id);
                }
            },
            function error() {
                $scope.errormessages = "Failed in resolution detail";
            });

    };

    $scope.getResolution = function () {
        ResolutionService.getResolution($routeParams.id).then(
            function success(response) {
                if (response) {
                    resolution = response;
                    // alert(JSON.stringify(resolution));
                    var statusId = $scope.status.filter(function (item) {
                        return item.value === resolution.statusId.toString();
                    });
                    var typeId = $scope.types.filter(function (item) {
                        return item.value === resolution.typeId.toString();
                    });
                    var horizonId = $scope.horizons.filter(function (item) {
                        return item.value === resolution.horizonId.toString();
                    });
                    var groupId = $scope.groups.filter(function (item) {
                        console.log(item.id === resolution.incidentId);
                        return item.id === resolution.incidentId;
                    });
                    resolution.statusId = statusId[0];
                    resolution.typeId = typeId[0];
                    resolution.horizonId = horizonId[0];
                    resolution.incidentGroup = groupId[0];
                    $scope.resolution = resolution;
                    //                    var actions = $scope.resolution.relatedActions;
                    //                    var actionsList = actions.split("|");
                    //                    var newActions = [];
                    //                    for (var i in actionsList) {
                    //                        newActions.push({
                    //                            id: $scope.actions.length + 1,
                    //                            name: actionsList[i],
                    //                            isNew: true
                    //                        });
                    //
                    //                    }
                    //                    $scope.actions = newActions;


                } else {
                    console.error("Unable to retrieve resolution for resolution " + id);
                }
            },
            function error() {
                $scope.errormessages = "Failed in get resolution detail";
            });

    };

    $scope.showComplex = function (resolution) {
        var name = "Incident Resolution ID " + resolution.id;

        ModalService.showModal({
            templateUrl: "resources/html/templates/complex.html",
            controller: "ComplexController",
            inputs: {
                title: "Delete Incident Resolution Confirmation:",
                name: name
            }
        }).then(function (modal) {
            modal.element.modal({ backdrop: 'static' });
            modal.close.then(function (result) {
                $scope.clearMsg();
                if (result.answer === 'Yes') {
                    ResolutionService.deleteResolution(resolution).then(
                        function success(response) {
                            if (response) {
                                $scope.messages = "Resolution ID " + resolution.id + " has been deleted.";
                            } else {
                                $scope.errormessages = "Delete operation failure, check logs or child associated entities still exist.";
                		        console.error("Resolution ID " + resolution.id + " was unable to be deleted.");
                            }
                            $scope.back = true;
                            return;
                        },
                        function error() {
                            $scope.errormessages = "Delete operation failure, check logs or child associated entities still exist.";
                        });
                } else {
                    return;
                }
            });
        });

    };

    $scope.delete = function (resolution) {
        $scope.showComplex(resolution);
    };

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errormessages = null;
        $scope.messages2 = null;
        $scope.errormessages2 = null;
    }

    $scope.update = function () {
        $scope.back = false;
        $scope.clearMsg();

        if ($scope.ownerlist != null && $scope.ownerlist.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerlist.length; i++) {
                owners = owners + "|" + $scope.ownerlist[i].userName;
            }
            if (owners.length > 1)
                $scope.resolution.owner = owners.substring(1, owners.length);
        }

        //        var actions = $scope.actions.map(function(x) {
        //            return x.name
        //        }).join('|');

        var statusId = $scope.status.filter(function (item) {
            return item.id === $scope.resolution.status.id;
        });
        var typeId = $scope.types.filter(function (item) {
            return item.id === $scope.resolution.type.id;
        });
        var horizonId = $scope.horizons.filter(function (item) {
            return item.id === $scope.resolution.horizon.id;
        });

        var incidentGroupId = $scope.groups.filter(function (item) {
            return item.id === $scope.resolution.incidentGroup.id;
        });

        $scope.resolution.incidentGroup = incidentGroupId[0];

        if ($scope.resolution.actualCompletionDate != null) {
            var d = new Date($scope.resolution.actualCompletionDate.toString());
            d.setTime(d.getTime() + d.getTimezoneOffset() * 60 * 1000);
            $scope.resolution.actualCompletionDate = d;
            d = null;
        }

        if ($scope.resolution.estCompletionDate != null) {
            var d = new Date($scope.resolution.estCompletionDate.toString());
            d.setTime(d.getTime() + d.getTimezoneOffset() * 60 * 1000);
            $scope.resolution.estCompletionDate = d;
            d = null;
        }

        var resolution = {
            "id": $scope.resolution.id,
            "horizon": horizonId[0],
            "incidentGroup": $scope.resolution.incidentGroup,
            "actualCompletionDate": $scope.resolution.actualCompletionDate,
            "description": $scope.resolution.description,
            "owner": $scope.resolution.owner,
            "resolutionProject": $scope.resolution.resolutionProject,
            "sriArtifact": $scope.resolution.sriArtifact,
            "estCompletionDate": $scope.resolution.estCompletionDate,
            "status": statusId[0],
            "type": typeId[0],
            "issue": $scope.resolution.issue
            //            "relatedActions": actions
        };

        ResolutionService.saveResolution(resolution).then(
            function success(response) {
                if (response) {
                    if (!$scope.resolution.id)
                        $scope.messages = "New Resolution has been saved.";
                    else {
                        $scope.messages = "Resolution " + resolution.id + " has been saved.";
                    }
                    $scope.back = true;
                    return;
                } else {
                    $scope.errormessages = ("Resolution was unable to be saved, description, owner and estimated completion date are required")
                }
            },
            function error() {

                $scope.errormessages = ("Resolution was unable to be saved, description, owner and estimated completion date are required")
            });
    };

    clear = function () {
        $location.path('/resolution/search');
    };

    $scope.cancel = function () {
        $location.path('/resolution/search');
    };

    $scope.new = function () {
        $location.path('/resolution/create');
    };

});

var resolutionCtrl = app.controller('ResolutionReportController', function ($http, $rootScope, $scope, ResolutionService, limitToFilter, $location, $routeParams, IncidentGroupService, ReferenceDataService, $filter, ModalService, gridData) {
    $scope.resolution = {};

    $scope.filterAction = function (action) {
        return action.isDeleted !== true;
    };

    $scope.filterOptions = {
        filterText: ''
    };

    $scope.saveFilter = function () {
        $rootScope.resolutionFilterText = $scope.filterOptions.filterText;
    };

    $scope.getHeader = function () {
        return $scope.gridOptions.columnDefs.displayName;
    };

    $scope.myData = [];
    $scope.init = function () {

        if ($rootScope.resolutionFilterText != null) {
            $scope.filterOptions.filterText = $rootScope.resolutionFilterText;
        }

        var i, resolution;
        var projects = {};
        $scope.myData = gridData;

    };

    $scope.gridOptions = {
        data: 'myData',
        filterOptions: $scope.filterOptions,
        plugins: [new pluginNgGridCVSExport()],
        showFooter: true,
        enablePinning: true,
        showGroupPanel: true,
        enableColumnResize: true,
        enableColumnReordering: true,
        columnDefs: [{
            field: "id",
            displayName: 'Resolution Id',
            width: '7%',
            cellTemplate: '<div class="ngCellText" ng-class="col.colIndex()"><a ng-click="saveFilter()" href="#/resolution/retrieve/{{row.getProperty(col.field)}}">{{row.getProperty(col.field)}}</a></div>',
            pinned: true
        }, {
            field: "incidentGroupName",
            displayName: 'Incident Group Name',
            width: '13%',
            pinned: true
        }, {
            field: "description",
            displayName: 'Description',
            width: '36%',
            pinned: true
        }, {
            field: "horizonName",
            displayName: 'Horizon',
            width: '9%'
        }, {
            field: "owner",
            displayName: 'Owner',
            width: '13%'
        }, {
            field: "statusName",
            displayName: 'Status',
            width: '7%'
        }, {
            field: "estCompletionDate",
            displayName: 'Estimated Completion Date',
            width: '11%',
            cellFilter: "date:'yyyy-MM-dd'"
        }, {
            field: "resolutionProjectName",
            displayName: 'Resolution Project',
            width: '33%'
        }, {
            field: "actualCompletionDate",
            displayName: 'Actual Completion Date',
            width: '11%',
            cellFilter: "date:'yyyy-MM-dd'"
        }, {
            field: "typeName",
            displayName: 'Type',
            width: '6%'
        }, {
            field: "sriArtifact",
            displayName: 'SRI Artifact',
            width: '6%'
        }]
    };

    $scope.new = function () {
        $location.path('/resolution/create');
    };

});

resolutionCtrl.getResolutions = function (ResolutionService) {
    return ResolutionService.getResolutions();
}