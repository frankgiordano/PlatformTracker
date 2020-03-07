app.controller('IncidentController', function ($rootScope, $scope, IncidentGroupService, IncidentService, locuss, alerted_bys, severities, groupStatuses, incidentstatuss, recipents, ModalService, ChronologyService, helperService, ProductService, $routeParams, $location, ReferenceDataService) {

    $scope.incident = {};

    $scope.init = function () {
        IncidentService.getIncidents().then(
            function success(response) {
                $scope.incidents = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "INCIDENTS_GET_FAILURE",
                    message: "Error retrieving incidents."
                });
            });
    };

    $scope.refreshData = function () {
        var newData = $scope.init();
        $scope.rowCollection = newData;
    };

    // no matter where this controller is being loaded exacute the following call 
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

    $scope.createSetup = function () {
        // make sure it is the create screen no id in url
        if ($routeParams.id === undefined) {
            IncidentService.getErrorConditions().then(
                function success(response) {
                    $scope.errors = response;
                    $scope.incident.error = $scope.errors[0];  // default error dropdown for create screen
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
    }

    $scope.editIncidentSetup = function () {
        IncidentService.getIncidentPlus($routeParams.id).then(
            function success(response) {
                if (response) {
                    $scope.groups = response[0];
                    $scope.chronologies = response[1];
                    $scope.errors = response[2];
                    $scope.applicationStatuses = response[3];
                    $scope.products = response[4];

                    response[5].startTime = moment(response[5].startTime).format('MM-DD-YYYY HH:mm');;
                    if (response[5].endTime)
                        response[5].endTime = moment(response[5].endTime).format('MM-DD-YYYY HH:mm');;

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
    }

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
        if ($scope.incident) {// this needs to be a truthy test
            $scope.vDateEnd = moment($scope.incident.endTime).format('MM-DD-YYYY HH:mm UTC');
        }
    }, true);

    // this watch is for the date time field for the chronology section of an incident detail
    $scope.$watch("createChronology.chronologyDateTime", function (val) {
        if ($scope.createChronology) {// this needs to be a truthy test 	
            $scope.vDateTime = moment($scope.createChronology.chronologyDateTime).format('MM-DD-YYYY HH:mm');
        }
    }, true);

    $scope.$watch("incident.status", function (val) {
        if ($scope.incident.status.name === "Closed") {// this needs to be a truthy test 	
            $scope.showResolution = true;
        } else {
            $scope.showResolution = false;
        }
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
                    $scope.chronmessages = "Chronology timeline for Incident tag " + $scope.incident.tag + " created.";
                    console.log("Chronology for Incident tag " + $scope.incident.tag + " created = " + JSON.stringify(response));
                    $scope.clear('chronology');
                    $scope.chronerrormessages = null;
                    $scope.getRelatedChronologies($scope.incident.id);
                }
            },
            function error() {
                document.body.style.cursor = "default";
                $scope.chronerrormessages = $rootScope.INCIDENT_CHRONOLOGY_SAVE_ERROR_MSG;
                $scope.chronmessages = null;
            });
    };

    $scope.removeChronology = function (item) {
        console.log("inside remove " + JSON.stringify(item));
        ChronologyService.deleteChronology(item.id).then(
            function success(response) {
                if (response) {
                    $scope.chronmessages = "Chronology timeline for Incident tag " + $scope.incident.tag + " with id " + item.id + " deleted.";
                    console.log("Chronology timeline for Incident tag " + $scope.incident.tag + " with Chronology timeline id " + item.id + " deleted.");
                    $scope.chronerrormessages = null;
                    $scope.getRelatedChronologies($scope.incident.id);
                }
            },
            function error() {
                $scope.chronerrormessages = "CHRONOLOGY_DELETE_FAILURE - Check logs or try again.";
                $scope.chronmessages = null;
            });
    };

    // get selected Incident's related chronologies for display
    $scope.getRelatedChronologies = function (id) {
        IncidentService.getChronologies(id).then(
            function success(response) {
                console.log("Chronologies " + JSON.stringify(response));
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
        // generate tag again just in case the products and start-time were changed
        $scope.generateTag();
        if (!$scope.incident.tag) {
            $scope.errormessages = "INCIDENT_SAVE_FAILURE - problem with generating tag. Please fill in Start Date Time and Products.";
            return;
        }

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

        $scope.enforceRequiredFields();

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
            "reportOwner": $scope.incident.reportOwner,
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
                        $scope.errormessages = null;
                        return IncidentService.saveIncident(incident);
                    }
                }, function error() {
                    $scope.errormessages = "GROUP_SAVE_FAILURE - Creating new Group " + groupCurrentORNew.name + " failed, check logs and try again.";
                    return $q.reject();
                })
                .then(function success(response) {
                    if (response) {
                        $scope.postIncidentSave(incident);
                    }
                }, function error(response) {
                    if (response.includes("OptimisticLockException")) {
                        $scope.errormessages = $rootScope.INCIDENT_VERSION_ERROR_MSG;
                        return;
                    }
                    $scope.errormessages = "Incident ID " + incident.id + " was unable to be saved with newly created Group " + groupCurrentORNew.name + ".";
                });
        } else {
            // An existing current group is still there with no new group specified. Hence nothing special just save.
            console.log("inside updateInSearch with existing Group " + JSON.stringify(incident));
            IncidentService.saveIncident(incident).then(
                function success(response) {
                    if (response) {
                        $scope.postIncidentSave(incident);
                    }
                },
                function error(response) {
                    if (response.includes("OptimisticLockException")) {
                        $scope.errormessages = $rootScope.INCIDENT_VERSION_ERROR_MSG;
                        return;
                    }
                    $scope.errormessages = $rootScope.INCIDENT_SAVE_ERROR_MSG;
                });
        }
    };

    $scope.postIncidentSave = function (incident) {
        $scope.getGroup(incident.id);
        $scope.messages = "Incident ID " + incident.id + " has been saved.";
        $scope.errormessages = null;
        $scope.incident.version++;
        $scope.disableButton = true;
        $scope.groupModel.selectedNewGroup = null;
    }

    // just do this for required fields that are not defaulted dropdown fields.
    $scope.enforceRequiredFields = function () {
        if ($scope.incident.description !== undefined &&
            $scope.incident.description !== null &&
            $scope.incident.description.trim() === "")
            $scope.incident.description = null;
    }

    $scope.submit = function () {
        $scope.clearDisplayMessages();

        // generate tag if tag is empty and products and start-time exist.. 
        if (!$scope.incident.tag) {

            $scope.generateTag();
            if (!$scope.incident.tag) {
                $scope.errormessages = "INCIDENT_SAVE_FAILURE - Tag field not generated yet! Please fill in Start Date Time, Description and Products fields.";
                return;
            }
        }

        // dates are currently in UTC format.. reset them to local timezone format for saving.. 
        var startTimeValue = new Date($scope.incident.startTime);
        if ($scope.incident.endTime) {
            var endTimeValue = new Date($scope.incident.endTime);
        }

        if (endTimeValue) {
            if (startTimeValue > endTimeValue) {
                $scope.errormessages = "INCIDENT_SAVE_FAILURE - End Date Time needs to be set after Start Date Time.";
                return;
            }
        }

        if (!$scope.incident.description) {
            $scope.incident.errormessages = "INCIDENT_SAVE_FAILURE - Please fill in Description field.";
            return;
        }

        var summary = "";
        if ($scope.incident.summary) {
            summary = $scope.incident.summary;
        } else {
            summary = " ";
        }

        var group = null;
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
            "reportOwner": $scope.incident.reportOwner,
            "summary": summary,
            "recordedBy": $scope.user.username,
            "status": $scope.incident.status.value,
            "emailRecipents": $scope.incident.emailRecipents.value,
            "correctiveAction": $scope.incident.correctiveAction
        };

        document.body.style.cursor = "wait";
        IncidentService.saveIncident(incident).then(
            function success(response) {
                document.body.style.cursor = "default";
                if (response) {
                    $scope.messages = "New Incident has been saved.";
                    console.log("New Incident has been saved = " + JSON.stringify(response));
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                }
            },
            function error() {
                document.body.style.cursor = "default";
                $scope.errormessages = $rootScope.INCIDENT_SAVE_ERROR_MSG;
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
                    $scope.deleteI($scope.incident.id);
                }
            });
        });
    };

    $scope.deleteI = function (id) {
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

    $scope.cancel = function (option) {
        switch (option) {
            case "incident":
                if ($routeParams.sourceLocation === "fromsearchbygroup") {
                    $location.path('/incident/groupsearch/' + $scope.currentGroupId);
                }
                if ($routeParams.sourceLocation === "fromsearch") {
                    $location.path('/incident/globalsearch');
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

    $scope.select = function (option, object) {
        switch (option) {
            case "incident":
                var sourceLocation = "fromsearch";
                var incident = object;
                $location.path('/incident/edit/' + sourceLocation + '/' + incident.id);
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
        $scope.errormessages = null;
        $scope.chronmessages = null;
        $scope.chronerrormessages = null;
    };

});