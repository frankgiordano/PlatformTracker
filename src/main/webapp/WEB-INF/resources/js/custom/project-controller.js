app.controller('ProjectController', function ($rootScope, $scope, ProjectService, localStorageService, $location, $routeParams, OwnersService, ReferenceDataService, ModalService) {

    $scope.project = {};
    $scope.hideDuringLoading = false;
    $scope.pageno = 1; // initialize page num to 1
    $scope.searchName = "";
    $scope.assignee = "";
    $scope.searchAssignee = "";
    $scope.totalCount = 0;
    $scope.itemsPerPage = 10;
    $scope.data = [];

    $scope.init = function () {
        $scope.setRouteSearchParms();
    };

    $scope.getData = function (pageno) {
        $scope.pageno = pageno;
        $scope.currentPage = pageno;
        var search = {
            pageno: $scope.pageno,
            name: $scope.searchName,
            assignee: $scope.searchAssignee
        };
        $scope.checkFilters(search);
        ProjectService.search(search, pageno).then(
            function success(response) {
                $scope.data = response;
            },
            function error() {
                $scope.errorMessages = "PROJECT_GET_FAILURE - Retrieving projects failed, check logs or try again.";
            });
    };

    $scope.checkFilters = function (search) {
        if (search.name.trim() === "")
            search.name = '*';
        if (search.assignee === "")
            search.assignee = '*';
    }

    $scope.sort = function (keyName) {
        $scope.sortKey = keyName;   //set the sortKey to the param passed
        $scope.reverse = !$scope.reverse; //if true make it false and vice versa
    };

    $scope.$watch("searchName", function (val) {
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
        $scope.searchName = "";
        $scope.searchAssignee = "";
        for (var i in $scope.assigneeList) {
            for (var j in $scope.assignees) {
                if ($scope.assigneeList[i].userName === $scope.assignees[j].userName) {
                    $scope.assignees[j].ticked = false;
                }
            }
        }
        $location.path('/project/search');
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
        $location.path('/project/edit/' + id + '/' + $scope.pageno + '/' + $scope.searchName + '/' + $scope.searchAssignee);
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
    $scope.waiting(false);

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
    };

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
                $scope.owners = response;
                $scope.assignees = response;
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

            $scope.pdlcStatus1 = ReferenceDataService.getPdlcStatus().then(
                function success(response) {
                    $scope.pdlcStatus = response;
                    $scope.project.pdlcStatus = $scope.pdlcStatus[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "PDLC_STATUS_GET_FAILURE",
                        message: "Error retrieving PDLC status."
                    });
                });

            $scope.status1 = ReferenceDataService.getStatus().then(
                function success(response) {
                    $scope.status = response;
                    $scope.project.status = $scope.status[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "STATUS_GET_FAILURE",
                        message: "Error retrieving status."
                    });
                });

            $scope.wikiTypes1 = ReferenceDataService.getWikiTypes().then(
                function success(response) {
                    $scope.wikiTypes = response;
                    $scope.project.wikiType = $scope.wikiTypes[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "WIKI_TYPES_GET_FAILURE",
                        message: "Error retrieving wiki types."
                    });
                });
        }
    };

    $scope.getProject = function () {
        $scope.checkLoginUser();
        $scope.setRouteSearchParms();
        ProjectService.getProject($routeParams.id).then(
            function success(response) {
                if (response) {

                    for (var j in $scope.owners) {
                        $scope.owners[j].ticked = false;
                    }

                    $scope.status = response[0];
                    $scope.pdlcStatus = response[1];
                    $scope.wikiTypes = response[2];
                    $scope.project = response[3];

                    response[3].actualCompletionDate = moment(response[3].actualCompletionDate).format('YYYY-MM-DD');
                    response[3].estCompletionDate = moment(response[3].estCompletionDate).format('YYYY-MM-DD');
                    response[3].recordingDate = moment(response[3].recordingDate).format('YYYY-MM-DD');
                    response[3].statusChangeDate = moment(response[3].statusChangeDate).format('YYYY-MM-DD');

                    OwnersService.getOwners().then(
                        function success(response) {
                            $scope.owners = response;
                            var owners = $scope.project.owner;
                            var ownersList = [];
                            if (owners != null) {
                                ownersList = owners.split("|");
                            }
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
                        return item.id === $scope.project.status.id;
                    });
                    $scope.project.status = statusId[0];

                    var pdlcStatusId = $scope.pdlcStatus.filter(function (item) {
                        return item.id === $scope.project.pdlcStatus.id;
                    });
                    $scope.project.pdlcStatus = pdlcStatusId[0];

                    var wikiTypesId = $scope.wikiTypes.filter(function (item) {
                        return item.id === $scope.project.wikiType.id;
                    });
                    $scope.project.wikiType = wikiTypesId[0];
                } else {
                    $scope.errorMessages = "PROJECT_GET_FAILURE - Retrieving project failed, check logs or try again.";
                }
            },
            function error() {
                $scope.errorMessages = "PROJECT_GET_FAILURE - Retrieving project failed, check logs or try again.";
            });
    };

    $scope.showComplex = function (project) {
        var title = "Project";
        var name = "Project Detail ID " + project.id;

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
                    ProjectService.deleteProject(project).then(
                        function success(response) {
                            if (response) {
                                $scope.messages = "Project ID " + project.id + " has been deleted.";
                                console.log("Project ID " + project.id + " has been deleted.");
                            } else {
                                $scope.errorMessages = "PROJECT_DELETE_FAILURE - Check logs or invalid project.";
                                console.error("Project ID " + project.id + " was unable to be deleted.");
                            }
                            $scope.back = true;
                            return;
                        },
                        function error() {
                            $scope.errorMessages = "PROJECT_DELETE_FAILURE - Check logs or invalid project.";
                            return;
                        });
                } else {
                    return;
                }
            });
        });
    };

    $scope.delete = function (project) {
        $scope.showComplex(project);
    };

    $scope.update = function () {
        $scope.clearMsg();
        $scope.waiting(true);

        if ($scope.ownerList != null && $scope.ownerList.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerList.length; i++) {
                owners = owners + "|" + $scope.ownerList[i].userName;
            }
            if (owners.length > 1)
                $scope.project.owner = owners.substring(1, owners.length);
        }

        $scope.back = false;

        if ($scope.project.actualCompletionDate === "Invalid date") {
            $scope.project.actualCompletionDate = null;
        }

        if ($scope.project.estCompletionDate === "Invalid date") {
            $scope.project.estCompletionDate = null;
        }

        if ($scope.project.recordingDate === "Invalid date") {
            $scope.project.recordingDate = null;
        }

        if ($scope.project.statusChangeDate === "Invalid date") {
            $scope.project.statusChangeDate = null;
        }

        // Trigger validation flag.
        $scope.submitted = true;
        $scope.ownerRequired = false;
        if ($scope.project.name === null ||
            $scope.project.name === undefined ||
            $scope.project.name.trim() === "") {
            $scope.nameRequired = true;
            $scope.projectForm.name.$invalid = true;
        }
        if ($scope.project.recordingDate === null ||
            $scope.project.recordingDate === undefined ||
            $scope.project.recordingDate.trim() === "") {
            $scope.recordingDateRequired = true;
            $scope.projectForm.recordingDate.$invalid = true;
        }
        if ($scope.project.description === null ||
            $scope.project.description === undefined ||
            $scope.project.description.trim() === "") {
            $scope.descriptionRequired = true;
            $scope.projectForm.description.$invalid = true;
        }
        if ($scope.project.estCompletionDate === null ||
            $scope.project.estCompletionDate === undefined ||
            $scope.project.estCompletionDate.trim() === "") {
            $scope.dueDateRequired = true;
            $scope.projectForm.dueDate.$invalid = true;
        }
        if ($scope.project.owner === null ||
            $scope.project.owner === undefined) {
            $scope.ownerRequired = true;
        }
        // End of validation

        enforceRequiredFields();

        var project = {
            "id": $scope.project.id,
            "name": $scope.project.name,
            "owner": $scope.project.owner,
            "isHighPriority": $scope.project.isHighPriority,
            "description": $scope.project.description,
            "ecdeId": $scope.project.ecdeId,
            "status": $scope.project.status,
            "estEffort": $scope.project.estEffort,
            "actualEffort": $scope.project.actualEffort,
            "actualCompletionDate": $scope.project.actualCompletionDate,
            "estCompletionDate": $scope.project.estCompletionDate,
            "pdlcStatus": $scope.project.pdlcStatus,
            "recordingDate": $scope.project.recordingDate,
            "statusChangeDate": $scope.project.statusChangeDate,
            "wikiType": $scope.project.wikiType,
            "jiraId": $scope.project.jiraId,
            "confluenceId": $scope.project.confluenceId
        };

        ProjectService.saveProject(project).then(
            function success(response) {
                if (response) {
                    if (!$scope.project.id) {
                        $scope.messages = "New Project has been saved.";
                        console.log("New Project has been saved = " + JSON.stringify(response));
                    } else {
                        $scope.messages = "Project ID " + $scope.project.id + " has been saved.";
                        console.log("Project has been saved = " + JSON.stringify(response));
                    }
                    $scope.back = true;
                    $scope.waiting(false);
                    return;
                }
            },
            function error() {
                $scope.errorMessages = $rootScope.PROJECT_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    // just do this for required fields that are not defaulted dropdown fields.
    var enforceRequiredFields = function () {
        if ($scope.project.name !== undefined &&
            $scope.project.name !== null &&
            $scope.project.name.trim() === "")
            $scope.project.name = null;
        if ($scope.project.description !== undefined &&
            $scope.project.description !== null &&
            $scope.project.description.trim() === "")
            $scope.project.description = null;
        if ($scope.project.owners === "")
            $scope.project.owners = null;
    };

    $scope.linkResolutions = function (project) {
        $scope.checkLoginUserFromLocalStorage();
        $scope.resolution = {};
        popitup('/plattrk/#/resolution/linkProject/' + project.id);
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
    };

    $scope.new = function () {
        $location.path('/project/create' + '/' + $scope.pageno + '/' + $scope.searchName + '/' + $scope.searchAssignee);
    };

    $scope.cancel = function () {
        $location.path('/project/search' + '/' + $scope.pageno + '/' + $scope.searchName + '/' + $scope.searchAssignee);
    };

    // to keep track where we left off so when we click on back/cancel button return to same search results
    $scope.setRouteSearchParms = function () {
        localStorageService.remove("incidentCreateButtonClicked");
        localStorageService.remove("incidentEditMode");
        if ($routeParams.searchName !== undefined) {
            $scope.searchName = $routeParams.searchName;
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

app.controller('ComplexController', [
    '$scope', '$element', 'title', 'name', 'close',
    function ($scope, $element, title, name, close) {
        $scope.title = title;
        $scope.name = name;

        $scope.close = function (result) {
            close({
                name: $scope.name,
                answer: result
            }, 500);
        };

    }
]);
