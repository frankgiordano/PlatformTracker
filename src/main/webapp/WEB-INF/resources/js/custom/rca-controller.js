app.controller('RootCauseController', function ($rootScope, $scope, RcaService, $location, $routeParams, IncidentGroupService, ReferenceDataService, $filter, ModalService, OwnersService) {

    $scope.rca = {};
    $scope.whys = [];
    $scope.hideduringloading = false;
    $scope.loading = true;

    $scope.filterWhy = function (why) {
        return why.isDeleted !== true;
    };

    $scope.deleteWhy = function (id) {
        var filtered = $filter('filter')($scope.whys, {
            id: id
        });
        if (filtered.length) {
            filtered[0].isDeleted = true;
        }
        for (var i = $scope.whys.length; i--;) {
            var why = $scope.whys[i];
            if (why.isDeleted || why.name.trim().length == 0) {
                $scope.whys.splice(i, 1);
            }
        }
    };

    $scope.addWhy = function () {
        if ($scope.whys.length < 5) {
            $scope.whys.push({
                id: $scope.whys.length + 1,
                name: '',
                isNew: true
            });
        } else { }
    };

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
                $scope.owners = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "OWNERS_GET_FAILURE",
                    message: "Error retrieving owners."
                });
            });
    })();

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
                        message: "Error retrieving groups."
                    });
                });
        })();

        $scope.categories1 = ReferenceDataService.getCategories().then(
            function success(response) {
                $scope.categories = response;
                $scope.rca.category = $scope.categories[0]
            },
            function error() {
                $rootScope.errors.push({
                    code: "CATEGORIES_GET_FAILURE",
                    message: "Error retrieving categories."
                });
            });

        $scope.status1 = ReferenceDataService.getStatus().then(
            function success(response) {
                $scope.status = response;
                $scope.rca.status = $scope.status[0];
            },
            function error() {
                $rootScope.errors.push({
                    code: "STATUS_GET_FAILURE",
                    message: "Error retrieving Status."
                });
            });

        $scope.resources1 = ReferenceDataService.getResources().then(
            function success(response) {
                $scope.resources = response;
                $scope.rca.resource = $scope.resources[0];
            },
            function error() {
                $rootScope.errors.push({
                    code: "RESOURCES_GET_FAILURE",
                    message: "Error retrieving resources."
                });
            });
    }

    $scope.saveFilter = function () {
        $rootScope.resolutionFilterText = $scope.filterOptions.filterText;
    };

    $scope.filterOptions = {
        filterText: ''
    };

    $scope.gridOptions = {
        data: 'myData',
        filterOptions: $scope.filterOptions,

        showFooter: true,

        enablePinning: true,
        showGroupPanel: true,
        enableColumnResize: true,
        enableColumnReordering: true,
        columnDefs: [{
            field: "id",
            displayName: 'Root Cause Id',
            width: '7%',
            cellTemplate: '<div class="ngCellText" ng-class="col.colIndex()"><a ng-click="saveFilter()" href="#/rootcause/retrieve/{{row.getProperty(col.field)}}">{{row.getProperty(col.field)}}</a></div>',
            pinned: true
        }, {
            field: "incidentGroup",
            displayName: 'Incident Group Name',
            width: '33%',
            pinned: true
        }, {
            field: "problem",
            displayName: 'Problem',
            width: '26%'
        }, {
            field: "status",
            displayName: 'Status',
            width: '7%'
        }, {
            field: "owner",
            displayName: 'Owner',
            width: '13%'
        },
        // { field: "whys", displayName:'Whys', width: '13%' },
        {
            field: "dueDate",
            displayName: 'Due Date',
            width: '11%',
            cellFilter: "date:'yyyy-MM-dd'"
        }, {
            field: "completionDate",
            displayName: 'Completion Date',
            width: '11%',
            cellFilter: "date:'yyyy-MM-dd'"
        }, {
            field: "category",
            displayName: 'Category',
            width: '13%'
        }, {
            field: "resource",
            displayName: 'Resource',
            width: '13%'
        }
        ]
    };

    $scope.myData = [];

    $scope.init = function () {
        if ($rootScope.resolutionFilterText != null) {
            $scope.filterOptions.filterText = $rootScope.resolutionFilterText;
        }

        RcaService.getRcas().then(
            function success(response, status, headers, config) {
                $scope.myData = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "ROOT_CAUSES_GET_FAILURE",
                    message: "Error retrieving Root Causes."
                });
            });
    };

    $scope.getRca = function () {
        RcaService.getRca($routeParams.id).then(
            function success(response) {
                if (response) {
                    $scope.groups = response[0];
                    $scope.categories = response[1];
                    $scope.status = response[2];
                    $scope.resources = response[3];
                    $scope.rca = response[4];
                    var whys = $scope.rca.whys;
                    var whysList = [];

                    if (whys != null) {
                        whysList = whys.split("|");
                    }

                    var newWhys = [];
                    for (var i in whysList) {
                        newWhys.push({
                            id: $scope.whys.length + 1,
                            name: whysList[i],
                            isNew: true
                        });

                    }
                    $scope.whys = newWhys;

                    var owners = $scope.rca.owner;
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
                    //  $scope.owners = newOwners;                    

                    var statusId = $scope.status.filter(function (item) {
                        return item.id === $scope.rca.status.id;
                    });
                    $scope.rca.status = statusId[0];

                    var resourceId = $scope.resources.filter(function (item) {
                        return item.id === $scope.rca.resource.id;
                    });
                    $scope.rca.resource = resourceId[0];

                    var categoryId = $scope.categories.filter(function (item) {
                        return item.id === $scope.rca.category.id;
                    });
                    $scope.rca.category = categoryId[0];

                    var incidentGroupId = $scope.groups.filter(function (item) {
                        return item.id === $scope.rca.incidentGroup.id;
                    });
                    $scope.rca.incidentGroup = incidentGroupId[0];

                    if ($scope.rca.dueDate)
                        $scope.rca.dueDate = moment($scope.rca.dueDate).format('YYYY-MM-DD');

                    if ($scope.rca.completionDate)
                        $scope.rca.completionDate = moment($scope.rca.completionDate).format('YYYY-MM-DD');

                } else {
                    console.error("Unable to retrieve Root Cuase ID " + id);
                }
            },
            function error() {
                $rootScope.errors.push({
                    code: "ROOT_CAUSE_GET_FAILURE",
                    message: "Error retrieving Root Cause."
                });
            });
    };

    $scope.showComplex = function (rca) {
        $scope.name = " Root Cause ID " + rca.id;

        ModalService.showModal({
            templateUrl: "resources/html/templates/complex.html",
            controller: "ComplexController",
            inputs: {
                title: "Delete Root Cause Confirmation:",
                name: $scope.name
            }
        }).then(function (modal) {
            modal.element.modal({ backdrop: 'static' });
            modal.close.then(function (result) {
                if (result.answer === 'Yes') {
                    $scope.deleteRootCauseById(rca);
                }
            });
        });
    };

    $scope.delete = function (rca) {
        $scope.showComplex(rca);
    };

    $scope.deleteRootCauseById = function (rca) {
        RcaService.deleteRca(rca).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Root Cause ID " + rca.id + " has been deleted.";
                    console.log("Root Cause has been deleted = " + response);
                }
                $scope.back = true;
            },
            function error() {
                $scope.errormessages = "ROOT_CAUSE_DELETE_FAILURE - Check logs or invalid RCA.";
            });
    };

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errormessages = null;
    }

    $scope.update = function () {
        $scope.clearMsg();
        $scope.waiting(true);

        if ($scope.ownerlist != null && $scope.ownerlist.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerlist.length; i++) {
                owners = owners + "|" + $scope.ownerlist[i].userName;
            }
            if (owners.length > 1)
                $scope.rca.owner = owners.substring(1, owners.length);;
        }

        var whys = $scope.whys.map(function (x) {
            return x.name
        }).join('|');

        var statusId = $scope.status.filter(function (item) {
            return item.id === $scope.rca.status.id;
        });

        var resourceId = $scope.resources.filter(function (item) {
            return item.id === $scope.rca.resource.id;
        });

        var categoryId = $scope.categories.filter(function (item) {
            return item.id === $scope.rca.category.id;
        });

        $scope.rca.status = statusId[0];
        $scope.rca.resource = resourceId[0];
        $scope.rca.category = categoryId[0];
        $scope.rca.whys = whys;

        $scope.setEmptyStringToNull();

        RcaService.saveRca($scope.rca).then(
            function success(response) {
                if (response) {
                    if (!$scope.rca.id) {
                        $scope.messages = "New Root Cause has been saved.";
                    } else {
                        $scope.messages = "Root Cause ID " + $scope.rca.id + " has been saved.";
                    }
                    console.log("Root Cause has been saved = " + JSON.stringify(response));
                    $scope.back = true;
                    $scope.waiting(false);
                }
            },
            function error() {
                $scope.errormessages = $rootScope.RC_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    // just do this for required fields that are not defaulted dropdown fields.
    $scope.setEmptyStringToNull = function () {
        if ($scope.rca.owner === "")
            $scope.rca.owner = null;
    }

    $scope.new = function () {
        $location.path('/rootcause/create');
    };

    $scope.createResolution = function () {
        $scope.resolution = {};
        $routeParams.incidentGroup = $scope.rca.incidentGroup;
        popitup('/plattrk/#/resolution/create/' + $scope.rca.incidentGroup.id);
    };

    function popitup(url) {
        params = 'width=' + screen.width;
        params += ', height=' + screen.height;
        params += ', top=0, left=0'
        params += ', fullscreen=yes';

        newwin = window.open(url, 'create resolution', params);
        if (window.focus) {
            newwin.focus()
        }
        return false;
    }

    $scope.cancel = function () {
        $location.path('/rootcause/search');
    };

    $scope.waiting = function(value) {
        if (value == true) { 
            $scope.hideduringloading = true;
            $scope.loading = false;
            document.body.style.cursor = "wait";
        } else {
            $scope.hideduringloading = false;
            $scope.loading = true;
            document.body.style.cursor = "default";
        }
    }

});