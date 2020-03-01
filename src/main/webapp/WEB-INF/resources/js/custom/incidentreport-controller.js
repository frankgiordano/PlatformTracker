app.controller('IncidentReportController', function ($http, $rootScope, $filter, $scope, IncidentService, IncidentGroupService, ResolutionService, ngTableParams, helperService) {
	var data = [];

	$scope.init = function () {
		IncidentService.getIncidents().then(
			function success(response) {
				data = response;
				$scope.tableParams.total(data.length);
				$scope.tableParams.reload();
				$scope.tableParams.sorting({ startTime: 'desc' });
			},
			function error() {
				$rootScope.errors.push({
					code: "INCIDENTS_GET_FAILURE",
					message: "Error retrieving incidents."
				});
			});
	};

	$scope.getGroup = function (id) {
		$scope.clearMsg();
		if (id == null)
			return;
		IncidentService.getGroup(id).then(
			function success(response) {
				console.log(JSON.stringify(response));
				if (response) {
					$scope.groupID = response.id;
					console.log("Group retrieved for Incident ID " + id);
				} else {
					console.error("Unable to retrieve group for Incident ID " + id);
				}
			},
			function error() {
				$scope.errormessages = "GROUP_GET_FAILURE - Group may not exist, please try again.";
				// $rootScope.errors.push({ code: "GROUP_GET_FAILURE", message: "Group may not exist, please try again." });
			});
	};

	$scope.select = function (object) {
		console.log("Generate Incident Report");
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

				if (!selectedIncident.reportOwner) {
					selectedIncident.reportOwner = "";
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
					console.log("actions " + JSON.stringify(splitActions));
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
					}
					) //set the templateVariables
					doc.render() //apply them (replace all occurrences of {first_name} by Hipp, ...)
					out = doc.getZip().generate({ type: "blob" }) //Output the document using Data-URI
					saveAs(out, "IncidentReport_" + selectedIncident.tag + ".docx")
				});
			});
	};

	// this method is not currently being used
	$scope.incidentSearch = function () {
		console.log("inside changedGroup, group id " + $scope.selectedGroup.id);
		IncidentGroupService.getGroupIncidents($scope.selectedGroup.id).then(
			function success(response) {
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

});
