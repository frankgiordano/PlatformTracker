app.controller('IncidentReportController', function ($rootScope, $scope, localStorageService, OwnersService, IncidentService, ResolutionService, helperService) {

    $scope.pageno = 1; // initialize page num to 1
    $scope.searchTag = "";
    $scope.searchDesc = "";
    $scope.assignee = "";
    $scope.searchAssignee = "";
    $scope.totalCount = 0;
    $scope.itemsPerPage = 10;

    $scope.init = function () {
        localStorageService.remove("incidentCreateButtonClicked");
        localStorageService.remove("incidentEditMode");
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

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
                $scope.assignees = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "OWNERS_GET_FAILURE",
                    message: "Error retrieving owners."
                });
            });
    })();

    $scope.getGroup = function (id) {
        $scope.clearMsg();
        if (id == null)
            return;
        IncidentService.getGroup(id).then(
            function success(response) {
                if (response) {
                    $scope.groupID = response.id;
                    console.log("Group retrieved for Incident ID " + id);
                } else {
                    console.error("Unable to retrieve group for Incident ID " + id);
                }
            },
            function error() {
                $scope.errorMessages = "GROUP_GET_FAILURE - Group may not exist, please try again.";
                // $rootScope.errors.push({ code: "GROUP_GET_FAILURE", message: "Group may not exist, please try again." });
            });
    };

    $scope.select = function (object) {
        selectedIncident = object;

        // use chain promises to get products, group id, resolutions, and chronologies before we fill in the doc template
        IncidentService.getProducts(selectedIncident.id)
            .then(function success(response) {
                console.log("Products " + JSON.stringify(response));
                $scope.selectedProducts = response;
                return IncidentService.getChronologies(selectedIncident.id);
            })
            .then(function success(response) {
                console.log("Chronologies " + JSON.stringify(response));
                $scope.selectedChronologies = response;
                return IncidentService.getGroup(selectedIncident.id);
            })
            .then(function success(response) {
                console.log("Group " + JSON.stringify(response));
                $scope.selectedGroupID = response.id;
                return ResolutionService.getGroupResolutions($scope.selectedGroupID);
            })
            .then(function success(response) {
                console.log("Resolutions " + JSON.stringify(response));
                $scope.selectedResolutions = response;

                // convert from UTC to local times for viewing.. remember date time comes in as UTC from back-end.. 
                var startDate = moment(selectedIncident.startTime).format('MM-DD-YYYY');
                var startTime = moment(selectedIncident.startTime).format('MM-DD-YYYY HH:mm');
                var endTime;
                if (selectedIncident.endTime) {
                    endTime = moment(selectedIncident.endTime).format('MM-DD-YYYY HH:mm');
                } else {
                    endTime = "";
                }

                // loop through chronologies and convert from UTC to local times. 
                for (var index in $scope.selectedChronologies) {
                    var dateTime = moment($scope.selectedChronologies[index].dateTime).format('MM-DD-YYYY HH:mm');
                    $scope.selectedChronologies[index].dateTime = dateTime;
                }

                // sort chronologies by Date if there are any
                if ($scope.selectedChronologies.length > 0) {
                    $scope.selectedChronologies.sort(helperService.compare("chronology"));
                }

                // sort resolutions by Date if there are any
                if ($scope.selectedResolutions.length > 0) {
                    $scope.selectedResolutions.sort(helperService.compare("resolution"));
                }

                // calculate minutes duration
                if (endTime && startTime) {
                    var end = moment(endTime);
                    var start = moment(startTime);
                    $scope.outageMinutes = (end.hour() * 60 + end.minute()) - (start.hour() * 60 + start.minute());
                } else {
                    $scope.outageMinutes = "";
                }

                // blank out falsely values for visual output otherwise it would show undefined instead
                if (!selectedIncident.name) {
                    selectedIncident.name = "";
                }

                if (!selectedIncident.summary) {
                    selectedIncident.summary = "";
                }

                if (!selectedIncident.owner) {
                    selectedIncident.owner = "";
                }

                if (!selectedIncident.customerImpact) {
                    selectedIncident.customerImpact = "";
                }

                if (!selectedIncident.issue) {
                    selectedIncident.issue = "";
                }

                if (!selectedIncident.correctiveAction) {
                    selectedIncident.correctiveAction = "";
                }

                var splitActions = [];
                var currentActions = selectedIncident.relatedActions;
                if (selectedIncident.relatedActions != null) {
                    var actionsList = currentActions.split("|");
                    for (var i in actionsList) {
                        splitActions.push({
                            id: currentActions.length + 1,
                            name: actionsList[i],
                            isNew: true
                        });
                    }
                }

                var loadFile = function (url, callback) {
                    JSZipUtils.getBinaryContent(url, callback);
                };

                loadFile("resources/doctemplates/incidentReportTemplate.docx", function (err, content) {
                    if (err) { throw e };
                    doc = new Docxgen(content);
                    doc.setData({
                        "Incident.Incident_ID": selectedIncident.id,
                        "Incident.Name": selectedIncident.name,
                        "Incident.Summary": selectedIncident.summary,
                        "Incident.Description": selectedIncident.description,
                        "Incident.Report Owner": selectedIncident.reportOwner,
                        "Incident.Start Date": startDate,
                        "Incident.Customer Impact": selectedIncident.customerImpact,
                        "products": $scope.selectedProducts,
                        "chronologies": $scope.selectedChronologies,
                        "Incident.Start Time": startTime,
                        "Incident.End Time": endTime,
                        "resolutions": $scope.selectedResolutions,
                        "Incident.Outage": $scope.outageMinutes,
                        "Incident.Issue": selectedIncident.issue,
                        "Incident.CorrectiveAction": selectedIncident.correctiveAction,
                        "relatedActions": splitActions
                    }) //set the templateVariables
                    doc.render() //apply them (replace all occurrences of {first_name} by Hipp, ...)
                    out = doc.getZip().generate({ type: "blob" }) //Output the document using Data-URI
                    saveAs(out, "IncidentReport_" + selectedIncident.tag + ".docx")
                });
            });
    };

});
