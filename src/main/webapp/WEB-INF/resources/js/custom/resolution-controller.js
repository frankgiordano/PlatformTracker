app.controller('ResolutionController', function ($rootScope, $scope, OwnersService, localStorageService, ResolutionService, $location, $routeParams, IncidentGroupService, ReferenceDataService, ModalService, HelperService) {

    $scope.resolution = {};
    $scope.hideDuringLoading = false;
    $scope.pageno = 1; // initialize page num to 1
    $scope.searchGrpName = "";
    $scope.searchDesc = "";
    $scope.assignees = "";
    $scope.searchAssignee = "";
    $scope.totalCount = 0;
    $scope.itemsPerPage = 10;
    $scope.data = [];

    $scope.init = function () {
        $scope.setRouteSearchParms();
    };

    $scope.avoidRefresh = function () {
        var url = $location.absUrl();
        if (url.indexOf("search") === -1)
            return true;
        return false;
    };

    $scope.getData = function (pageno) {
        $scope.errorMessages = null;
        if ($scope.avoidRefresh() === true)
            return;
        $scope.pageno = pageno;
        $scope.currentPage = pageno;
        var search = {
            pageno: $scope.pageno,
            grpName: $scope.searchGrpName,
            desc: $scope.searchDesc,
            assignee: $scope.searchAssignee
        };
        $scope.checkFilters(search);
        $scope.waiting(true, "load");
        ResolutionService.search(search, pageno).then(
            function success(response) {
                $scope.data = response;
                $scope.waiting(false);
            },
            function error() {
                $scope.errorMessages = "RESOLUTIONS_GET_FAILURE - Retrieving resolutions failed, check logs or try again.";
                $scope.waiting(false);
            });
    };

    $scope.checkFilters = function (search) {
        if (search.grpName.trim() === "")
            search.grpName = '*';
        if (search.desc.trim() === "")
            search.desc = '*';
        if (search.assignee === "")
            search.assignee = '*';
    }

    $scope.sort = function (keyName) {
        $scope.sortKey = keyName;   //set the sortKey to the param passed
        $scope.reverse = !$scope.reverse; //if true make it false and vice versa
    };

    $scope.$watch("searchGrpName", function (val) {
        $scope.checkForAssignees();
        $scope.getData($scope.pageno);
    }, true);

    $scope.$watch("searchDesc", function (val) {
        $scope.checkForAssignees();
        $scope.getData($scope.pageno);
    }, true);

    $scope.$watch("assigneeList", function (val) {
        $scope.checkForAssignees();
        $scope.getData($scope.pageno);
    }, true);

    $scope.checkForAssignees = function () {
        if ($scope.assigneeList != null && $scope.assigneeList.length > 0) {
            var assignees = "";
            for (i = 0; i < $scope.assigneeList.length; i++) {
                assignees = assignees + "|" + $scope.assigneeList[i].userName;
            }
            if (assignees.length > 1)
                $scope.searchAssignee = assignees.substring(1, assignees.length);
        }
    };

    $scope.clearFilters = function () {
        $scope.checkLoginUserFromLocalStorage();
        $scope.searchGrpName = "";
        $scope.searchDesc = "";
        $scope.searchAssignee = "";
        for (var i in $scope.assigneeList) {
            for (var j in $scope.assignees) {
                if ($scope.assigneeList[i].userName === $scope.assignees[j].userName) {
                    $scope.assignees[j].ticked = false;
                }
            }
        }
    };

    $scope.setSearchOwner = function () {
        for (var i in $scope.assignees) {
            if ($scope.assignees[i].userName === $scope.searchAssignee) {
                $scope.assignees[i].ticked = true;
                break;
            }
        }
    };

    $scope.select = function (id) {
        $location.path('/resolution/edit/' + id + '/' + $scope.pageno + '/' + $scope.searchGrpName + '/' + $scope.searchDesc + '/' + $scope.searchAssignee);
    };

    $scope.waiting = function (value, action) {
        if (action === "save") {
            $scope.waitMessage = "Saving Resolution...";
        }
        if (action === "create") {
            $scope.waitMessage = "Creating Resolution...";
        }
        if (action === "delete") {
            $scope.waitMessage = "Deleting Resolution...";
        }
        if (action === "load") {
            $scope.waitMessage = "Loading...";
        }
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
    $scope.waiting(false);

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
                $scope.owners = response;
                $scope.assignees = HelperService.deepCopyJSONArray(response);
                $scope.setRouteSearchParms();
            },
            function error() {
                $rootScope.errors.push({
                    code: "OWNERS_GET_FAILURE",
                    message: "Error retrieving owners."
                });
            });
    })();

    $scope.createSetup = function () {
        $scope.checkLoginUser();
        $scope.setRouteSearchParms();
        // make sure it is the create screen no id in url
        if ($routeParams.id === null || $routeParams.id === undefined) {
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

            $scope.types1 = ReferenceDataService.getTypes().then(
                function success(response) {
                    $scope.types = response;
                    $scope.resolution.type = response[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "TYPES_GET_FAILURE",
                        message: "Error retrieving types."
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
                        message: "Error retrieving statuses."
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
                        message: "Error retrieving horizons."
                    });
                });
        }
    };

    $scope.getIncidentResolution = function () {
        $scope.checkLoginUser();
        $scope.setRouteSearchParms();
        ResolutionService.getIncidentResolution($routeParams.id).then(
            function success(response) {
                if (response) {
                    $scope.groups = response[0];
                    $scope.horizons = response[1];
                    $scope.status = response[2];
                    $scope.types = response[3];
                    response[4].estCompletionDate = moment(response[4].estCompletionDate).format('YYYY-MM-DD');
                    if (response[4].actualCompletionDate)
                        response[4].actualCompletionDate = moment(response[4].actualCompletionDate).format('YYYY-MM-DD');
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
                                message: "Error retrieving owners."
                            });
                        });

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
                    console.error("Unable to retrieve Resolution ID " + id);
                }
            },
            function error() {
                $rootScope.errors.push({
                    code: "RESOLUTION_GET_FAILURE",
                    message: "Error retrieving Resolution."
                });
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
                    $scope.waiting(true, "delete");
                    ResolutionService.deleteResolution(resolution).then(
                        function success(response) {
                            if (response) {
                                $scope.messages = "Resolution ID " + resolution.id + " has been deleted.";
                                console.log("Resolution has been deleted = " + JSON.stringify(response));
                            }
                            $scope.back = true;
                            $scope.waiting(false);
                        },
                        function error() {
                            $scope.errorMessages = "RESOLUTION_DELETE_FAILURE - Resolution may be linked with other entities and cannot be deleted or check logs.";
                            $scope.waiting(false);
                        });
                }
            });
        });
    };

    $scope.delete = function (resolution) {
        $scope.showComplex(resolution);
    };

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
    };

    $scope.update = function () {
        $scope.back = false;
        $scope.clearMsg();
        if ($routeParams.id > 0)
            $scope.waiting(true, "save");
        else
            $scope.waiting(true, "create");

        if ($scope.ownerList != null && $scope.ownerList.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerList.length; i++) {
                owners = owners + "|" + $scope.ownerList[i].userName;
            }
            if (owners.length > 1)
                $scope.resolution.owner = owners.substring(1, owners.length);
        }

        // handle lost DB connect.. if end user refreshes page fields will be empty
        if ($scope.status === undefined) {
            $scope.errorMessages = $rootScope.RESOLUTION_SAVE_ERROR_MSG;
            $scope.waiting(false);
            return;
        }
        var statusId = $scope.status.filter(function (item) {
            return item.id === $scope.resolution.status.id;
        });
        var typeId = $scope.types.filter(function (item) {
            return item.id === $scope.resolution.type.id;
        });
        var horizonId = $scope.horizons.filter(function (item) {
            return item.id === $scope.resolution.horizon.id;
        });

        var incidentGroupId = null;
        if ($scope.resolution.incidentGroup) {
            var incidentGroupId = $scope.groups.filter(function (item) {
                return item.id === $scope.resolution.incidentGroup.id;
            });
            $scope.resolution.incidentGroup = incidentGroupId[0];
        } else {
            $scope.resolution.incidentGroup = null;
        }

        if ($scope.resolution.actualCompletionDate === "Invalid date") {
            $scope.resolution.actualCompletionDate = null;
        }

        if ($scope.resolution.estcompletionDate === "Invalid date") {
            $scope.resolution.estcompletionDate = null;
        }

        // Trigger validation flag.
        $scope.submitted = true;
        $scope.ownerRequired = false;
        if ($scope.resolution.incidentGroup === null ||
            $scope.resolution.incidentGroup === undefined) {
            $scope.nameRequired = true;
            $scope.resolutionForm.name.$invalid = true;
        }
        if ($scope.resolution.owner === null ||
            $scope.resolution.owner === undefined) {
            $scope.ownerRequired = true;
        }
        if ($scope.resolution.description === null ||
            $scope.resolution.description === undefined ||
            $scope.resolution.description.trim() === "") {
            $scope.descriptionRequired = true;
            $scope.resolutionForm.description.$invalid = true;
        }
        if ($scope.resolution.estCompletionDate === null ||
            $scope.resolution.estCompletionDate === undefined ||
            $scope.resolution.estCompletionDate.trim() === "") {
            $scope.estCompletionDateRequired = true;
            $scope.resolutionForm.estCompletionDate.$invalid = true;
        }
        // End of validation

        enforceRequiredFields();

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
        };

        ResolutionService.saveResolution(resolution).then(
            function success(response) {
                if (response) {
                    if (!$scope.resolution.id) {
                        $scope.messages = "New Resolution has been saved.";
                    } else {
                        $scope.messages = "Resolution ID " + $scope.resolution.id + " has been saved.";
                    }
                    console.log("Resolution has been saved = " + JSON.stringify(response));
                    $scope.back = true;
                    $scope.waiting(false);
                }
            },
            function error() {
                $scope.errorMessages = $rootScope.RESOLUTION_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    // just do this for required fields that are not defaulted dropdown fields.
    var enforceRequiredFields = function () {
        if ($scope.resolution.owner === "")
            $scope.resolution.owner = null;
        if ($scope.resolution.description !== undefined &&
            $scope.resolution.description !== null &&
            $scope.resolution.description.trim() === "")
            $scope.resolution.description = null;
    };

    $scope.new = function () {
        $scope.checkLoginUserFromLocalStorage();
        $location.path('/resolution/create' + '/' + $scope.pageno + '/' + $scope.searchGrpName + '/' + $scope.searchDesc + '/' + $scope.searchAssignee);
    };

    $scope.cancel = function () {
        $scope.checkLoginUserFromLocalStorage();
        $location.path('/resolution/search' + '/' + $scope.pageno + '/' + $scope.searchGrpName + '/' + $scope.searchDesc + '/' + $scope.searchAssignee);
    };

    // to keep track where we left off so when we click on back/cancel button return to same search results
    $scope.setRouteSearchParms = function () {
        localStorageService.remove("incidentCreateButtonClicked");
        localStorageService.remove("incidentEditMode");
        if ($routeParams.searchGrpName !== undefined) {
            $scope.searchGrpName = $routeParams.searchGrpName;
        }
        if ($routeParams.searchDesc !== undefined) {
            $scope.searchDesc = $routeParams.searchDesc;
        }
        if ($routeParams.searchAssignee !== undefined) {
            $scope.searchAssignee = $routeParams.searchAssignee;
            $scope.setSearchOwner();
        }
        if ($routeParams.pageno !== undefined) {
            $scope.pageno = $routeParams.pageno;
        }
    };

});