app.controller('IncidentGroupController', function ($rootScope, $scope, localStorageService, IncidentGroupService, groupStatuses, ModalService) {
    // set the defaults
    $scope.groupStatuses = groupStatuses;
    $scope.selectedGroup = null;
    $scope.createResolution = null; // this variable handles the display of the resolution creation sub form
    $scope.createRootCA = null; // this variable handles the display of the RCA creation sub form
    $scope.disableButton = false;
    $scope.hideDuringLoading = false;
    $scope.pageno = 1; // initialize page num to 1
    $scope.totalCount = 0;
    $scope.itemsPerPage = 10;
    $scope.data = [];

    $scope.init = function () {
        localStorageService.remove("incidentCreateButtonClicked");
        localStorageService.remove("incidentEditMode");
        $scope.searchName = "";
        $scope.searchDesc = "";
    };

    $scope.getData = function (pageno) {
        $scope.pageno = pageno;
        var search = {
            pageno: $scope.pageno,
            name: $scope.searchName,
            desc: $scope.searchDesc
        };
        $scope.checkFilters(search);
        IncidentGroupService.search(search, pageno).then(
            function success(response) {
                $scope.data = response;
            },
            function error() {
                $scope.errorMessages = "RESOLUTIONS_GET_FAILURE - Retrieving resolutions failed, check logs or try again.";
            });
    };

    $scope.sort = function (keyName) {
        $scope.sortKey = keyName;   //set the sortKey to the param passed
        $scope.reverse = !$scope.reverse; //if true make it false and vice versa
    };

    $scope.$watch("searchName", function (val) {
        $scope.getData($scope.pageno);
    }, true);

    $scope.$watch("searchDesc", function (val) {
        $scope.getData($scope.pageno);
    }, true);

    $scope.refreshData = function () {
        $scope.getData($scope.pageno);
    };

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

    $scope.select = function (group) {
        $scope.selectedGroup = group;
        $scope.disableButton = false;
        $scope.clearDisplayMessages();
    };

    $scope.update = function () {
        $scope.clearDisplayMessages();
        $scope.waiting(true);

        // Trigger validation flag.
        $scope.submitted = true;
        var isValid = true;
        if ($scope.selectedGroup.name === null ||
            $scope.selectedGroup.name === undefined ||
            $scope.selectedGroup.name.trim() === "") {
            $scope.groupNameRequired = true;
            $scope.groupForm.groupName.$invalid = true;
            isValid = false;
        }
        if ($scope.selectedGroup.description === null ||
            $scope.selectedGroup.description === undefined ||
            $scope.selectedGroup.description.trim() === "") {
            $scope.groupDescriptionRequired = true;
            $scope.groupForm.groupDescription.$invalid = true;
            isValid = false;
        }
        // End of validation

        var group = {
            "id": $scope.selectedGroup.id,
            "name": $scope.selectedGroup.name,
            "description": $scope.selectedGroup.description,
            "status": $scope.selectedGroup.status
        };

        if (isValid === false) {
            $scope.errorMessages = $rootScope.INCIDENT_GROUP_SAVE_ERROR_MSG;
            $scope.waiting(false);
        } else {
            IncidentGroupService.saveGroup(group).then(
                function success(response) {
                    if (response) {
                        $scope.messages = "Group ID " + group.id + " has been saved.";
                        console.log("Group ID " + group.id + " has been saved.");
                        $scope.errorMessages = null;
                        $scope.disableButton = true;
                    }
                    $scope.waiting(false);
                },
                function error() {
                    $scope.errorMessages = $rootScope.INCIDENT_GROUP_SAVE_ERROR_MSG;
                    $scope.waiting(false);
                });
        }

    };

    $scope.deleteG = function (id) {
        $scope.clearDisplayMessages();
        IncidentGroupService.deleteGroup(id).then(
            function success(response) {
                $scope.messages = "Group ID " + id + " has been deleted.";
                console.log("Group has been deleted = " + JSON.stringify(response));
                $scope.disableButton = true;
                $scope.refreshData();
            },
            function error(response) {
                if (response.includes("ConstraintErrorException") || response.includes("ConstraintViolationException")) {
                    $scope.errorMessages = "GROUP_DELETE_FAILURE - Child associated entities still exist.";
                    return;
                }
                $scope.errorMessages = "GROUP_DELETE_FAILURE - backend severe error, check logs and try again.";
            });
    };

    $scope.showOnDelete = function () {
        var title = "Group";
        var name = "Group Detail ID " + $scope.selectedGroup.id;

        ModalService.showModal({
            templateUrl: "resources/html/templates/complex.html",
            controller: "ComplexController",
            inputs: {
                title: "Delete " + title + " Confirmation:",
                name: name
            }
        }).then(function (modal) {
            modal.element.modal({ backdrop: 'static' });
            modal.close.then(function (result) {
                if (result.answer === 'Yes') {
                    $scope.deleteG($scope.selectedGroup.id);
                }
            });
        });
    };

    $scope.deleteAllGroupOrphans = function () {
        $scope.clearDisplayMessages();
        document.body.style.cursor = "wait";
        IncidentGroupService.deleteAllGroupOrphans().then(
            function success(response) {
                if (response) {
                    if (response.length === 0) {
                        $scope.errorMessages = "GROUP_ORPHANS_DELETE_FAILURE - Check logs, or no orphan groups to delete or problem deleting existing orphan groups.";
                        document.body.style.cursor = "default";
                        return;
                    }
                    if (response.length > 1)
                        $scope.messages = response.length + " orphan groups have been deleted.";
                    else
                        $scope.messages = "1 orphan group have been deleted.";
                    $scope.disableButton = true;
                    document.body.style.cursor = "default";
                    $scope.refreshData();
                }
            },
            function error() {
                $scope.errorMessages = "GROUP_ORPHANS_DELETE_FAILURE - Check logs, or no orphan groups to delete or problem deleting existing orphan groups.";
                document.body.style.cursor = "default";
            });
    };

    $scope.createRes = function () {
        $scope.checkLoginUserFromLocalStorage();
        $scope.clearDisplayMessages();
        $scope.createResolution = true;
    };

    $scope.createRCA = function () {
        $scope.checkLoginUserFromLocalStorage();
        $scope.clearDisplayMessages();
        $scope.createRootCA = true;
    };

    $scope.cancel = function () {
        $scope.selectedGroup = false;
        $scope.clearDisplayMessages();
        $scope.refreshData();
    };

    $scope.clearDisplayMessages = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
    };

    $scope.checkFilters = function (search) {
        if (search.name.trim() === "")
            search.name = '*';
        if (search.desc.trim() === "")
            search.desc = '*';
    }

});

app.controller('RootCauseChildController', function ($rootScope, $scope, ReferenceDataService, RcaService, OwnersService, $filter) {

    $scope.rca = {};
    $scope.hideDuringLoading = false;

    $scope.waiting = function (value) {
        if (value == true) {
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

    (function () {
        ReferenceDataService.getStatus().then(
            function success(response) {
                $scope.statuses = response;
                $scope.rca.status = $scope.statuses[1];
            },
            function error() {
                $rootScope.errors.push({
                    code: "STATUS_GET_FAILURE",
                    message: "Error retrieving status."
                });
            });
    })();

    (function () {
        ReferenceDataService.getCategories().then(
            function success(response) {
                $scope.categories = response;
                $scope.rca.category = $scope.categories[15];
            },
            function error() {
                $rootScope.errors.push({
                    code: "CATEGORIES_GET_FAILURE",
                    message: "Error retrieving categories."
                });
            });
    })();

    (function () {
        ReferenceDataService.getResources().then(
            function success(response) {
                $scope.resources = response;
                $scope.rca.resource = $scope.resources[11];
            },
            function error() {
                $rootScope.errors.push({
                    code: "RESOURCES_GET_FAILURE",
                    message: "Error retrieving resources."
                });
            });
    })();

    $scope.clear = function () {
        $scope.rca.status = $scope.statuses[1];
        $scope.rca.category = $scope.categories[15];
        $scope.rca.resource = $scope.resources[11];
        $scope.rca.problemDescription = null;
        $scope.whys = [];
        angular.forEach($scope.owners, function (value, key) {
            value['ticked'] = false;
        });
        $scope.rca.dueDate = null;
        $scope.rca.completionDate = null;
        $scope.rca.owner = null;
        $scope.messages = null;
        $scope.errorMessages = null;
        $scope.waiting(false);
        cleanFormValidation();
    };

    $scope.cancel = function () {
        $scope.$parent.createRootCA = false;
        $scope.disableButton = false;
        $scope.clear();
    };

    // start - this is for rca create screen from group detail screen
    $scope.rca = {};
    $scope.whys = [];

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
        if ($scope.whys) {
            if ($scope.whys.length < 5) {
                $scope.whys.push({
                    id: $scope.whys.length + 1,
                    name: '',
                    isNew: true
                });
            } else { }
        }
    };
    // end - this is for rca create screen from group detail screen

    // add related actions stuff
    $scope.actions = [];

    $scope.filterAction = function (action) {
        return action.isDeleted !== true;
    };

    $scope.deleteAction = function (id) {
        var filtered = $filter('filter')($scope.actions, {
            id: id
        });
        if (filtered.length) {
            filtered[0].isDeleted = true;
        }
        for (var i = $scope.actions.length; i--;) {
            var action = $scope.actions[i];
            if (action.isDeleted || action.name.trim().length == 0) {
                $scope.actions.splice(i, 1);
            }
        }
    };

    $scope.addAction = function () {
        if ($scope.actions.length < 10) {
            $scope.actions.push({
                id: $scope.actions.length + 1,
                name: '',
                isNew: true
            });
        } else { }
    };
    // END OF - add related actions stuff

    $scope.submit = function () {
        $scope.waiting(true);

        if ($scope.ownerList != null && $scope.ownerList.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerList.length; i++) {
                owners = owners + "|" + $scope.ownerList[i].userName;
            }
            if (owners.length > 1)
                $scope.rca.owner = owners.substring(1, owners.length);
        }

        // Trigger validation flag.
        $scope.submitted = true;
        $scope.ownerRequired = false;
        if ($scope.rca.owner === null ||
            $scope.rca.owner === undefined) {
            $scope.ownerRequired = true;
        }
        // End of validation

        var whys = $scope.whys.map(function (x) {
            return x.name
        }).join('|');

        var rca = {
            "problem": $scope.rca.problemDescription,
            "whys": whys,
            "dueDate": $scope.rca.dueDate,
            "completionDate": $scope.rca.completionDate,
            "category": $scope.rca.category,
            "resource": $scope.rca.resource,
            "owner": $scope.rca.owner,
            "status": $scope.rca.status,
            "incidentGroup": $scope.$parent.selectedGroup
        };

        RcaService.saveRca(rca).then(
            function success(response) {
                if (response) {
                    $scope.messages = "New Root Cause created for Group " + '"' + $scope.$parent.selectedGroup.name + '".';
                    $scope.errorMessages = null;
                    $scope.disableButton = true;
                }
                $scope.waiting(false);
            },
            function error() {
                $scope.errorMessages = $rootScope.RC_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    var cleanFormValidation = function () {
        $scope.ownerRequired = false;
    }

});

app.controller('ResolutionChildController', function ($rootScope, $scope, $routeParams, IncidentGroupService, ReferenceDataService, OwnersService, ResolutionService) {

    $scope.resolution = {};
    $scope.hideDuringLoading = false;

    $scope.waiting = function (value) {
        if (value == true) {
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

    $scope.createSetup = function () {
        if ($routeParams.incidentGroup !== "") {
            IncidentGroupService.getGroup($routeParams.incidentGroup).then(
                function success(response) {
                    $scope.selectedGroup = response;
                    console.log("get = " + JSON.stringify(response));
                },
                function error() {
                    $rootScope.errors.push({
                        code: "GOURP_GET_FAILURE",
                        message: "Error retrieving Incident Group."
                    });
                });
        }
    };

    (function () {
        ReferenceDataService.getHorizons().then(
            function success(response) {
                $scope.horizons = response;
                $scope.resolution.horizon = $scope.horizons[2];
            },
            function error() {
                $rootScope.errors.push({
                    code: "HORIZONS_GET_FAILURE",
                    message: "Error retrieving horizons."
                });
            });
    })();

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

    (function () {
        ReferenceDataService.getStatus().then(
            function success(response) {
                $scope.statuses = response;
                $scope.resolution.status = $scope.statuses[0];
            },
            function error() {
                $rootScope.errors.push({
                    code: "STATUS_GET_FAILURE",
                    message: "Error retrieving status."
                });
            });
    })();

    (function () {
        ReferenceDataService.getTypes().then(
            function success(response) {
                $scope.types = response;
                $scope.resolution.type = $scope.types[2];
            },
            function error() {
                $rootScope.errors.push({
                    code: "TYPES_GET_FAILURE",
                    message: "Error retrieving types."
                });
            });
    })();

    $scope.clear = function () {
        $scope.resolution.horizon = $scope.horizons[2];
        $scope.resolution.status = $scope.statuses[0];
        $scope.resolution.type = $scope.types[2];
        $scope.resolution.description = null;
        $scope.resolution.owner = null;
        angular.forEach($scope.owners, function (value, key) {
            value['ticked'] = false;
        });
        $scope.resolution.sriArtifact = null;
        $scope.resolution.estCompletionDate = null;
        $scope.resolution.actualCompletionDate = null;
        $scope.messages = null;
        $scope.errorMessages = null;
        cleanFormValidation();
    };

    $scope.cancel = function () {
        $scope.$parent.createResolution = false;
        $scope.disableButton = false;
        $scope.clear();
    };

    $scope.submit = function () {
        $scope.waiting(true);

        if ($scope.ownerList != null && $scope.ownerList.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerList.length; i++) {
                owners = owners + "|" + $scope.ownerList[i].userName;
            }
            if (owners.length > 1)
                $scope.resolution.owner = owners.substring(1, owners.length);
        }

        // Trigger validation flag.
        $scope.submitted = true;
        $scope.ownerRequired = false;
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

        var resolution = {
            "description": $scope.resolution.description,
            "status": $scope.resolution.status,
            "owner": $scope.resolution.owner,
            "sriArtifact": $scope.resolution.sriArtifact,
            "estCompletionDate": $scope.resolution.estCompletionDate,
            "actualCompletionDate": $scope.resolution.actualCompletionDate,
            "type": $scope.resolution.type,
            "horizon": $scope.resolution.horizon,
            "incidentGroup": $scope.selectedGroup
        };

        ResolutionService.saveResolution(resolution).then(
            function success(response) {
                if (response) {
                    $scope.messages = $scope.resolution.horizon.displayName + " Resolution created for Group " + '"' + $scope.selectedGroup.name + '".';
                    $scope.errorMessages = null;
                    $scope.disableButton = true;
                }
                $scope.waiting(false);
            },
            function error() {
                $scope.errorMessages = $rootScope.RESOLUTION_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    var cleanFormValidation = function () {
        $scope.ownerRequired = false;
        $scope.descriptionRequired = false;
        $scope.resolutionForm.description.$invalid = false;
        $scope.estCompletionDateRequired = false;
        $scope.resolutionForm.estCompletionDate.$invalid = false;
    }

});