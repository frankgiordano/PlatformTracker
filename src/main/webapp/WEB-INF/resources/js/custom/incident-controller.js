app.controller('IncidentController', function ($rootScope, $scope, IncidentGroupService, localStorageService, IncidentService, locuss, alerted_bys, severities, groupStatuses, incidentstatuss, recipents, ModalService, ChronologyService, helperService, ProductService, $routeParams, $location, ReferenceDataService, OwnersService) {

    $scope.incident = {};
    $scope.hideDuringLoading = false;
    $scope.pageno = 1; // initialize page num to 1
    $scope.searchTag = "";
    $scope.searchDesc = "";
    $scope.assignee = "";
    $scope.searchAssignee = "";
    $scope.totalCount = 0;
    $scope.itemsPerPage = 10;
    $scope.data = [];
    $scope.clearButtonClicked = false;
    $scope.userFirstChanged = false;

    $scope.init = function () {
        $scope.setRouteSearchParms();
    };

    $scope.getData = function (pageno) {
        $scope.pageno = pageno;
        $scope.currentPage = pageno;
        var search = {
            pageno: $scope.pageno,
            tag: $scope.searchTag,
            desc: $scope.searchDesc,
            assignee: $scope.searchAssignee
        };
        $scope.checkFilters(search);
        IncidentService.search(search, pageno).then(
            function success(response) {
                $scope.data = response;
            },
            function error() {
                $scope.errorMessages = "INCIDENTS_GET_FAILURE - Retrieving incidents failed, check logs or try again.";
            });
    };

    $scope.checkFilters = function (search) {
        if (search.tag.trim() === "")
            search.tag = '*';
        if (search.desc.trim() === "")
            search.desc = '*';
        if (search.assignee === "")
            search.assignee = '*';
    }

    $scope.sort = function (keyName) {
        $scope.sortKey = keyName;   //set the sortKey to the param passed
        $scope.reverse = !$scope.reverse; //if true make it false and vice versa
    };

    $scope.$watch("searchTag", function (val) {
        if ($routeParams.sourceLocation === "fromsearchbygroup")
            return;
        $scope.checkForAssignees();
        $scope.getData($scope.pageno);
    }, true);

    $scope.$watch("searchDesc", function (val) {
        if ($routeParams.sourceLocation === "fromsearchbygroup")
            return;
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
            if (assignees.length > 1) {
                $scope.searchAssignee = assignees.substring(1, assignees.length);
                $scope.userFirstChanged = true;
            }
        } 
    };

    $scope.clearFilters = function () {
        $scope.clearButtonClicked = true;
        $scope.searchTag = "";
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

    $scope.setSearchOwner = function (userName) {
        $scope.searchAssignee = userName;
        for (var i in $scope.assignees) {
            if ($scope.assignees[i].userName === userName) {
                $scope.assignees[i].ticked = true;
                break;
            }
        }
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

    $scope.refreshData = function () {
        var newData = $scope.init();
        $scope.rowCollection = newData;
    };

    // no matter where this controller is being loaded from execute the following call 
    (function () {
        ProductService.getActiveProducts().then(
            function success(response) {
                $scope.myProducts = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "PRODUCTS_GET_FAILURE",
                    message: "Error retrieving products."
                });
            });
    }());

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
                $scope.owners = response;
                $scope.assignees = response;
                $scope.setRouteSearchParms();
                var createButtonClicked = localStorageService.get("incidentCreateButtonClicked");
                var incidentEditMode = localStorageService.get("incidentEditMode");
                if (incidentEditMode === null && $scope.clearButtonClicked === false && $scope.userFirstChanged === false && createButtonClicked === null) {
                    if ($rootScope.user) {
                        $scope.setSearchOwner($rootScope.user.username);
                        $scope.getData($scope.pageno);  // workaround to handle timing issue when loading page first time after app deployment
                    }
                }            
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
            IncidentService.getErrorConditions().then(
                function success(response) {
                    $scope.errors = response;
                    $scope.incident.error = $scope.errors[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "ERROR_CONDITION_GET_FAILURE",
                        message: "Error retrieving error conditions."
                    });
                });

            ReferenceDataService.getApplicationStatus().then(
                function success(response) {
                    $scope.applicationStatuses = response;
                    $scope.incident.applicationStatus = $scope.applicationStatuses[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "APPLICATION_STATUSES_GET_FAILURE",
                        message: "Error retrieving application statuses."
                    });
                });

            $scope.loadUIHardCodedValues();

            $scope.incident.severity = $scope.severities[0];
            $scope.incident.status = $scope.incidentstatuss[0];
            $scope.incident.emailRecipents = $scope.recipents[0];
            $scope.incident.locus = $scope.locuss[0];
            $scope.incident.alertedBy = $scope.alerted_bys[0];

            $scope.showResolution = false;
        }
    };

    $scope.editIncidentSetup = function () {
        $scope.checkLoginUser();
        localStorageService.set("incidentEditMode", true);  // set to any value we only check if is exist
        $scope.setRouteSearchParms();
        IncidentService.getIncidentPlus($routeParams.id).then(
            function success(response) {
                if (response) {
                    $scope.groups = response[0];
                    $scope.chronologies = response[1];
                    $scope.errors = response[2];
                    $scope.applicationStatuses = response[3];
                    $scope.products = response[4];

                    response[5].startTime = moment(response[5].startTime).format('MM-DD-YYYY HH:mm');
                    if (response[5].endTime)
                        response[5].endTime = moment(response[5].endTime).format('MM-DD-YYYY HH:mm');

                    $scope.incident = response[5];

                    $scope.getGroup($scope.incident.id);

                    $scope.loadUIHardCodedValues();

                    var error = $scope.errors.filter(function (item) {
                        return item.id === $scope.incident.error.id;
                    });
                    $scope.incident.error = error[0].name;

                    var applicationStatus = $scope.applicationStatuses.filter(function (item) {
                        return item.id === $scope.incident.applicationStatus.id;
                    });
                    $scope.incident.applicationStatus = applicationStatus[0].displayName;

                    // store relatedActions for viewing
                    var actions = $scope.incident.relatedActions;
                    if (actions != null) {
                        var actionsList = actions.split("|");
                        var newActions = [];
                        for (var i in actionsList) {
                            newActions.push({
                                id: i,
                                name: actionsList[i],
                                isNew: true
                            });
                        }
                        $scope.actions = newActions;
                    }

                    var owners = $scope.incident.owner;
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

                    $scope.disableButton = false;
                }
            },
            function error() {
                $rootScope.errors.push({
                    code: "INCIDENT_GET_FAILURE",
                    message: "Error retrieving Incident."
                });
            });
    };

    // the following method will load in UI hard coded values
    // to fill in apppropriate dropdown fields. see controller.js
    $scope.loadUIHardCodedValues = function () {
        $scope.severities = severities;
        $scope.incidentstatuss = incidentstatuss;
        $scope.recipents = recipents;
        $scope.locuss = locuss;
        $scope.alerted_bys = alerted_bys;
        $scope.groupStatuses = groupStatuses;
    };

    // paid attention here.. this object is used for the ng-include directive which creates its own child scope.
    // since it relies on scope inheritance to resolve bindings, then a ng-model reference should have a '.' in it to
    // resolve properly. otherwise, selectedNewGroup used as a single variable outside an object for the drop down
    // list, it will create a shadow variable on the child scope that is a copy of the variable in the parent scope.
    // This breaks the model binding.. 
    $scope.groupModel = {
        currentGroupName: null,
        selectedNewGroup: null
    };

    $scope.selectedGroup = null;

    // these next two watches are for the date time picker fields when displaying an incident detail
    // from incident list filtered by groups
    // these next two watches are for the date time fields when displaying an incident detail
    // from incident list
    $scope.$watch("incident.startTime", function (val) {
        if ($scope.incident) { // this needs to be a truthy test
            $scope.vDateStart = moment($scope.incident.startTime).format('MM-DD-YYYY HH:mm UTC');
        }
    }, true);

    $scope.$watch("incident.endTime", function (val) {
        if ($scope.incident) { // this needs to be a truthy test
            $scope.vDateEnd = moment($scope.incident.endTime).format('MM-DD-YYYY HH:mm UTC');
        }
    }, true);

    // this watch is for the date time field for the chronology section of an incident detail
    $scope.$watch("createChronology.chronologyDateTime", function (val) {
        if ($scope.createChronology) { // this needs to be a truthy test 	
            $scope.vDateTime = moment($scope.createChronology.chronologyDateTime).format('MM-DD-YYYY HH:mm');
        }
    }, true);

    $scope.$watch("incident.status", function (val) {
        if ($scope.incident)
            if ($scope.incident.status)
                if ($scope.incident.status.name === "Closed")
                    $scope.showResolution = true;
                else
                    $scope.showResolution = false;
    }, true);

    // START - DATETIMEPICKER SETUP - https://github.com/Gillardo/bootstrap-ui-datetime-picker  
    $scope.open = {
        startTime: false,
        endTime: false,
        dateTime: false,
        date4: false,
        date5: false,
    };

    // Disable weekend selection
    $scope.disabled = function (date, mode) {
        return (mode === 'day' && (new Date().toDateString() == date.toDateString()));
    };

    $scope.dateOptions = {
        showWeeks: false,
        startingDay: 1
    };

    $scope.timeOptions = {
        readonlyInput: true,
        showMeridian: false
    };

    $scope.openCalendar = function (e, date) {
        e.preventDefault();
        e.stopPropagation();
        $scope.open[date] = true;
    };
    // END - DATETIMEPICKER SETUP  - https://github.com/Gillardo/bootstrap-ui-datetime-picker  

    // START - RELATED ACTIONS SETUP 
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
    // END - RELATED ACTIONS SETUP

    $scope.submitChronology = function () {
        $scope.clearDisplayMessages();

        // dates are currently in UTC format.. reset them to local timezone format for saving.. 
        var dateTimeValue = new Date($scope.createChronology.chronologyDateTime);

        var chronology = {
            "dateTime": dateTimeValue,
            "description": $scope.createChronology.chronDescription,
            "recordedBy": $scope.user.username,
            "incident": { id: $scope.incident.id }
        };

        document.body.style.cursor = "wait";
        ChronologyService.saveChronology(chronology).then(
            function success(response) {
                document.body.style.cursor = "default";
                if (response) {
                    $scope.chronMessages = "Chronology timeline for Incident tag " + $scope.incident.tag + " created.";
                    console.log("Chronology for Incident tag " + $scope.incident.tag + " created = " + JSON.stringify(response));
                    $scope.clear('chronology');
                    $scope.chronErrorMessages = null;
                    $scope.getRelatedChronologies($scope.incident.id);
                }
            },
            function error() {
                document.body.style.cursor = "default";
                $scope.chronErrorMessages = $rootScope.INCIDENT_CHRONOLOGY_SAVE_ERROR_MSG;
                $scope.chronMessages = null;
            });
    };

    $scope.removeChronology = function (item) {
        var title = "Incident Chronology";
        var name = "Incident Chronology ID " + item.id;

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
                    $scope.deleteChronology(item.id);
                }
            });
        });
    };

    // get selected Incident's related chronologies for display
    $scope.getRelatedChronologies = function (id) {
        IncidentService.getChronologies(id).then(
            function success(response) {
                $scope.chronologies = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "CHRONOLOGIES_GET_FAILURE",
                    message: "Error retrieving chronologies."
                });
            });
    };

    $scope.update = function () {
        $scope.clearDisplayMessages();
        $scope.waiting(true);

        $scope.formValidation();
        $scope.generateTag();

        var actions = $scope.actions.map(function (x) {
            return x.name
        }).join('|');

        var usersImpactedValue = $scope.incident.usersImpacted;
        // dates are currently in UTC format.. reset them to local timezone format for saving.. 
        var startTimeValue = new Date($scope.incident.startTime);
        var endTimeValue;
        if ($scope.incident.endTime) {
            endTimeValue = new Date($scope.incident.endTime);
        }

        // this is to protect the existing group details from being overwritten in case the user specifies an existing group to be reassigned too
        // don't change its description and status.. 
        var newGroupSpecified = false;
        if ($scope.groupModel.selectedNewGroup) {
            if (!helperService.search($scope.groups, $scope.groupModel.selectedNewGroup)) {
                newGroupSpecified = true;
            }
        }

        var groupCurrentORNew = null;
        if ($scope.groupModel.selectedNewGroup && newGroupSpecified) {
            groupCurrentORNew = {
                "name": $scope.groupModel.selectedNewGroup,
                "description": $scope.incident.description + " " + $scope.incident.summary,
                "status": $scope.groupStatuses[0].value
            };
        } else {
            // if an existing group specified in the Reassign Group field then use selectedNewGroup
            // value don't form a new group variable for group creation
            // or else if Reassign Group is empty use the current group value
            if ($scope.groupModel.selectedNewGroup) {
                groupCurrentORNew = $scope.groupModel.selectedNewGroup;
            } else {
                groupCurrentORNew = $scope.groupModel.currentGroupName;
            }
        }

        var errorCode;
        Object.keys($scope.errors).forEach(function (key) {
            if ($scope.errors[key].name === $scope.incident.error) {
                errorCode = {
                    "id": $scope.errors[key].id,
                    "name": $scope.errors[key].name
                }
            }
        });

        var applicationStatus;
        Object.keys($scope.applicationStatuses).forEach(function (key) {
            if ($scope.applicationStatuses[key].displayName === $scope.incident.applicationStatus) {
                applicationStatus = {
                    "id": $scope.applicationStatuses[key].id,
                }
            }
        });

        $scope.setOwner();

        enforceRequiredFields();

        var incident = {
            "id": $scope.incident.id,
            "version": $scope.incident.version,
            "tag": $scope.incident.tag,
            "severity": $scope.incident.severity,
            "locus": $scope.incident.locus,
            "description": $scope.incident.description,
            "usersImpacted": usersImpactedValue,
            "alertedBy": $scope.incident.alertedBy,
            "error": errorCode,
            "applicationStatus": applicationStatus,
            "transactionIdsImpacted": $scope.incident.transactionIdsImpacted,
            "startTime": startTimeValue,
            "endTime": endTimeValue,
            "incidentGroup": groupCurrentORNew,
            "incidentReport": $scope.incident.incidentReport,
            "callsReceived": $scope.incident.callsReceived,
            "products": $scope.products,
            "customerImpact": $scope.incident.customerImpact,
            "name": $scope.incident.name,
            "owner": $scope.incident.owner,
            "summary": $scope.incident.summary,
            "recordedBy": $scope.incident.recordedBy,
            "status": $scope.incident.status,
            "emailRecipents": $scope.incident.emailRecipents,
            "reviewedBy": $scope.incident.reviewedBy,
            "issue": $scope.incident.issue,
            "correctiveAction": $scope.incident.correctiveAction,
            "relatedActions": actions
        };

        if ($scope.groupModel.selectedNewGroup && newGroupSpecified) {
            // A new group was specified, so go ahead and create it and then save the incident with the new group 
            // associated with it. This is using the chain promises technique. 
            IncidentGroupService.saveGroup(groupCurrentORNew)
                .then(function success(response) {
                    if (response) {
                        console.log("New Group has been saved = " + JSON.stringify(response));
                        $scope.errorMessages = null;
                        // no need to use response to attach it as group to incident to save
                        // backend will use group name associated existing one if any..
                        // this needs to be revisit if it still needs to be done this way. 
                        return IncidentService.saveIncident(incident);
                    }
                }, function error() {
                    $scope.errorMessages = "GROUP_SAVE_FAILURE - Creating new Group " + groupCurrentORNew.name + " failed, check logs and try again.";
                    $scope.waiting(false);
                    return $q.reject();
                })
                .then(function success(response) {
                    if (response) {
                        $scope.postIncidentSave(incident);
                    }
                    $scope.waiting(false);
                }, function error(response) {
                    if (response.includes("OptimisticLockException")) {
                        $scope.errorMessages = $rootScope.INCIDENT_VERSION_ERROR_MSG;
                        return;
                    }
                    $scope.waiting(false);
                    $scope.errorMessages = "Incident ID " + incident.id + " was unable to be saved with newly created Group " + groupCurrentORNew.name + ".";
                });
        } else {
            // An existing current group is still there with no new group specified. Hence nothing special just save.
            IncidentService.saveIncident(incident).then(
                function success(response) {
                    if (response) {
                        $scope.postIncidentSave(incident);
                    }
                    $scope.waiting(false);
                },
                function error(response) {
                    if (response.includes("OptimisticLockException")) {
                        $scope.errorMessages = $rootScope.INCIDENT_VERSION_ERROR_MSG;
                        $scope.waiting(false);
                        return;
                    }
                    $scope.errorMessages = $rootScope.INCIDENT_SAVE_ERROR_MSG;
                    $scope.waiting(false);
                });
        }
    };

    $scope.postIncidentSave = function (incident) {
        $scope.getGroup(incident.id);
        $scope.messages = "Incident ID " + incident.id + " has been saved.";
        $scope.errorMessages = null;
        $scope.incident.version++;
        $scope.disableButton = true;
        $scope.groupModel.selectedNewGroup = null;
    };

    // just do this for required fields that are not defaulted dropdown fields.
    var enforceRequiredFields = function () {
        if ($scope.incident.description !== undefined &&
            $scope.incident.description !== null &&
            $scope.incident.description.trim() === "")
            $scope.incident.description = null;
    };

    $scope.submit = function () {
        $scope.clearDisplayMessages();
        $scope.waiting(true);

        $scope.formValidation();
        $scope.generateTag();

        // dates are currently in UTC format.. reset them to local timezone format for saving.. 
        var startTimeValue = new Date($scope.incident.startTime);
        if ($scope.incident.endTime) {
            var endTimeValue = new Date($scope.incident.endTime);
        }

        if (endTimeValue) {
            if (startTimeValue > endTimeValue) {
                $scope.errorMessages = "INCIDENT_SAVE_FAILURE - End Date Time needs to be set after Start Date Time.";
                $scope.waiting(false);
                return;
            }
        }

        var summary = "";
        var group = null;

        if ($scope.incident.description) {

            if ($scope.incident.summary) {
                summary = $scope.incident.summary;
            } else {
                summary = " ";
            }

            if ($scope.incident.incidentGroup) {
                group = {
                    "name": $scope.incident.incidentGroup,
                    "description": $scope.incident.description + " " + summary,
                }
            } else {
                var myNameString = $scope.incident.description + " " + summary;
                group = {
                    "name": myNameString.substring(0, 120),
                    "description": $scope.incident.description + " " + summary,
                }
            }
        }

        $scope.setOwner();

        var incident = {
            "tag": $scope.incident.tag,
            "severity": $scope.incident.severity.value,
            "locus": $scope.incident.locus.value,
            "description": $scope.incident.description,
            "usersImpacted": $scope.incident.usersImpacted,
            "alertedBy": $scope.incident.alertedBy.value,
            "error": {
                "id": $scope.incident.error.id,
                "name": $scope.incident.error.name
            },
            "applicationStatus": $scope.incident.applicationStatus,
            "transactionIdsImpacted": $scope.incident.transactionIdsImpacted,
            "startTime": startTimeValue,
            "endTime": endTimeValue,
            "incidentGroup": group,
            "incidentReport": $scope.incident.incidentReport,
            "callsReceived": $scope.incident.callsReceived,
            "products": $scope.products,
            "customerImpact": $scope.incident.customerImpact,
            "name": $scope.incident.name,
            "owner": $scope.incident.owner,
            "summary": summary,
            "recordedBy": $scope.user.username,
            "status": $scope.incident.status.value,
            "emailRecipents": $scope.incident.emailRecipents.value,
            "correctiveAction": $scope.incident.correctiveAction
        };

        IncidentService.saveIncident(incident).then(
            function success(response) {
                if (response) {
                    $scope.messages = "New Incident has been saved.";
                    console.log("New Incident has been saved = " + JSON.stringify(response));
                    $scope.errorMessages = null;
                    $scope.disableButton = true;
                    $scope.waiting(false);
                }
            },
            function error() {
                $scope.errorMessages = $rootScope.INCIDENT_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    $scope.generateTag = function () {
        var tag = null;
        if ($scope.incident.startTime && $scope.products) {
            var d = new Date($scope.incident.startTime);
            tag = moment(d).format('MMDDYYYY_HHmm');
            switch ($scope.products.length) {
                case 0:
                    $scope.incident.tag = null;
                    break;
                case 1:
                    $scope.incident.tag = $scope.products[0].shortName + "_" + tag;
                    break;
                default:
                    $scope.incident.tag = "MULTI_" + tag;
            }
        }
    };

    $scope.setOwner = function () {
        if ($scope.ownerList != null && $scope.ownerList.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerList.length; i++) {
                owners = owners + "|" + $scope.ownerList[i].userName;
            }
            if (owners.length > 1)
                $scope.incident.owner = owners.substring(1, owners.length);
        }
    };

    $scope.getGroup = function (id) {
        $scope.clearDisplayMessages();
        if (id == null) return;
        IncidentService.getGroup(id).then(
            function success(response) {
                if (response) {
                    $scope.groupModel.currentGroupName = response.name;
                    $scope.currentGroupId = response.id;
                    console.log("Group retrieved for Incident ID " + id + " = " + JSON.stringify(response));
                }
            },
            function error() {
                $scope.errormessages = "GROUP_GET_FAILURE - Group may not exist, please try again.";
            });
    };

    $scope.showOnDelete = function () {
        var title = "Incident";
        var name = "Incident Detail ID " + $scope.incident.id;

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
                    $scope.deleteIncident($scope.incident.id);
                }
            });
        });
    };

    $scope.deleteIncident = function (id) {
        $scope.clearDisplayMessages();
        IncidentService.deleteIncident(id).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Incident ID " + id + " has been deleted.";
                    console.log("Incident has been deleted = " + JSON.stringify(response));
                }
                $scope.disableButton = true;
            },
            function error() {
                $scope.errormessages = "INCIDENT_DELETE_FAILURE - Check logs or invalid Incident.";
            });
    };

    $scope.deleteChronology = function (id) {
        ChronologyService.deleteChronology(id).then(function success(response) {
            if (response) {
                $scope.chronMessages = "Chronology timeline for Incident tag " + $scope.incident.tag + " with id " + id + " deleted.";
                console.log("Chronology timeline for Incident tag " + $scope.incident.tag + " with Chronology timeline id " + id + " deleted.");
                $scope.chronErrorMessages = null;
                $scope.getRelatedChronologies($scope.incident.id);
            }
        }, function error() {
            $scope.chronErrorMessages = "CHRONOLOGY_DELETE_FAILURE - Check logs or try again.";
            $scope.chronMessages = null;
        });
    }

    $scope.cancelEdit = function (option) {
        switch (option) {
            case "incident":
                if ($routeParams.sourceLocation === "fromsearchbygroup") {
                    $location.path('/incident/fromgroupsearch/' + $routeParams.gid);
                }
                if ($routeParams.sourceLocation === "fromsearch") {
                    $location.path('/incident/search' + '/' + $scope.pageno + '/' + $scope.searchTag + '/' + $scope.searchDesc + '/' + $scope.searchAssignee);
                }
                break;
            case "createChronology":
                $scope.createChronology = null;
                break;
            case "group":
                $scope.selectedGroup = false;
                break;
        }
        $scope.clearDisplayMessages();
    };

    $scope.cancelCreate = function () {
        $location.path('/incident/search' + '/' + $scope.pageno + '/' + $scope.searchTag + '/' + $scope.searchDesc + '/' + $scope.searchAssignee);
    };

    $scope.select = function (option, object) {
        switch (option) {
            case "incident":
                var sourceLocation = "fromsearch";
                var incident = object;
                $location.path('/incident/edit/' + sourceLocation + '/' + incident.id + '/' + $scope.pageno + '/' + $scope.searchTag + '/' + $scope.searchDesc + '/' + $scope.searchAssignee);
                break;
            case "chronology":
                $scope.createChronology = new Object();
                break;
        }
        $scope.clearDisplayMessages();
    };

    $scope.clear = function (option) {
        switch (option) {
            case "chronology":
                $scope.createChronology.chronDescription = null;
                break;
        }
    };

    $scope.clearDisplayMessages = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
        $scope.chronMessages = null;
        $scope.chronErrorMessages = null;
    };

    $scope.new = function () {
        localStorageService.set("incidentCreateButtonClicked", true);  // set to any value we only check to see if it exist
        $location.path('/incident/create' + '/' + $scope.pageno + '/' + $scope.searchTag + '/' + $scope.searchDesc + '/' + $scope.searchAssignee);
    };

    // to keep track where we left off so when we click on back/cancel button return to same search results
    $scope.setRouteSearchParms = function () {
        if ($routeParams.searchTag !== undefined) {
            $scope.searchTag = $routeParams.searchTag;
        }
        if ($routeParams.searchDesc !== undefined) {
            $scope.searchDesc = $routeParams.searchDesc;
        }
        if ($routeParams.searchAssignee !== undefined) {
            $scope.setSearchOwner($routeParams.searchAssignee);
        }
        if ($routeParams.pageno !== undefined) {
            $scope.pageno = $routeParams.pageno;
        }
    };

    $scope.formValidation = function () {
        $scope.submitted = true;
        $scope.productsRequired = false;
        if ($scope.incident.description === null ||
            $scope.incident.description === undefined ||
            $scope.incident.description.trim() === "") {
            $scope.technicalDescriptionRequired = true;
            $scope.incidentForm.technicalDescription.$invalid = true;
        }
        if ($scope.incident.startTime === null ||
            $scope.incident.startTime === undefined) {
            $scope.startTimeRequired = true;
            $scope.incidentForm.startTime.$invalid = true;
        }
        if ($scope.products === null ||
            $scope.products === undefined ||
            $scope.products.length === 0) {
            $scope.productsRequired = true;
        }
    }

});


