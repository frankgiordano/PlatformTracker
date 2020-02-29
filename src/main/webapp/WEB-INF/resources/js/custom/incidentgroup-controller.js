app.controller('IncidentGroupController', function($rootScope, $filter, $scope, IncidentGroupService, IncidentService, ResolutionService, ReferenceDataService, ngTableParams, locuss, alerted_bys, options, statuss, incidentstatuss, recipents, ModalService, RcaService, ProductService, ChronologyService, helperService) {
    $scope.init = function() {
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
    };
    
    // This was using getProducts before but changed it to getActiveProducts() call which is a bit
    // more optimized. It returns data sorted and hence no need to sort in JavaScript level with the
    // helperService I created.  getActiveProducts() is used because this only returns active products 
    // not all products which is required now for the dropdown in incident. 
    (function() {
        ProductService.getActiveProducts().then(
            function success(response) {
                // console.log("getActiveProducts " + JSON.stringify(response));
            	// response = helperService.sortByKey(response, 'shortName');
                $scope.myProducts = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "GROUPS_GET_FAILURE",
                    message: "Error retrieving products."
                });
            });
    }());
    
    (function() {
        IncidentService.getErrorConditions().then(
            function success(response) {
            	// console.log("getErrorConditions " + JSON.stringify(response));
            	$scope.errors = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "ERROR_CONDITION_GET_FAILURE",
                    message: "Error retrieving error conditions."
                });
            });
    }());
    
    (function() {
    	ReferenceDataService.getApplicationStatus().then(
            function success(response) {
            	// console.log("getIncidentStatus = " + JSON.stringify(response));
                $scope.applicationStatuses = response;
                $scope.selectedApplicationStatus = $scope.applicationStatuses[0];
            },
            function error() {
                $rootScope.errors.push({
                    code: "APPLICATION_STATUS_GET_FAILURE",
                    message: "Error retrieving application statuses."
                });
            });
    }());
    
    // get selected Incident's related products for display
    var getRelatedProducts = function(id) {
        IncidentService.getProducts(id).then(
            function success(response) {
                // console.log(JSON.stringify(response));
                $scope.selectedProducts = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "RELATED_PRODUCTS_GET_FAILURE",
                    message: "Error retrieving related products."
                });
            });
    };
    
    // get selected Incident's related chronologies for display
    var getRelatedChronologies = function(id) {
        IncidentService.getChronologies(id).then(
            function success(response) {
                // console.log("Chronologies " + JSON.stringify(response));
                $scope.selectedChronologies = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "RELATED_CHRONOLOGIES_GET_FAILURE",
                    message: "Error retrieving related chronologies."
                });
            });
    };
    
    // get selected Incident's Application Status for display
    var getRelatedApplicationStatus = function(id) {
        IncidentService.getApplicationStatus(id).then(
                function success(response) {
                    // console.log("Application Status " + JSON.stringify(response));
                    $scope.selectedIncident.applicationStatus = response.displayName;
                },
                function error() {
                    $rootScope.errors.push({
                        code: "RELATED_APPLICATION_STATUS_GET_FAILURE",
                        message: "Error retrieving related application status."
                    });
                });
    };
    
    // get selected Incident's Error Code for display
    var getRelatedErrorCode = function(id) {
        IncidentService.getErrorCode(id).then(
                function success(response) {
                    // console.log("Error Code " + JSON.stringify(response));
                    $scope.selectedIncident.error = response.name;
                },
                function error() {
                    $rootScope.errors.push({
                        code: "RELATED_ERROR_CODE_GET_FAILURE",
                        message: "Error retrieving related error code."
                    });
                });
    };

    $scope.types1 = ReferenceDataService.getTypes().then(
        function success(response) {
            $scope.types = response;
            // console.log("getTypes " + JSON.stringify($scope.types));
            $scope.selectedType = $scope.types[2];
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
            // console.log("getStatus " + JSON.stringify($scope.status));
            $scope.selectedResStatus = $scope.status[0];
        },
        function error() {
            $rootScope.errors.push({
                code: "STATUS_GET_FAILURE",
                message: "Error retrieving status."
            });
        });

    $scope.horizons1 = ReferenceDataService.getHorizons().then(
        function success(response) {
            $scope.horizons = response;
            // console.log("getHorizons " + JSON.stringify($scope.horizons));
            $scope.selectedHorizon = $scope.horizons[2];
        },
        function error() {
            $rootScope.errors.push({
                code: "HORIZONS_GET_FAILURE",
                message: "Error retrieving horizons."
            });
        });

    $scope.categories1 = ReferenceDataService.getCategories().then(
        function success(response) {
            $scope.categories = response;
            // console.log("getCategories " + JSON.stringify($scope.categories));
            $scope.selectedCategory = $scope.categories[15];
        },
        function error() {
            $rootScope.errors.push({
                code: "CATEGORIES_GET_FAILURE",
                message: "Error retrieving categories."
            });
        });

    $scope.showOnDelete = function(type) {
        var title = '';
        var name = '';
        if (type === "incident") {
            title = "Incident";
            name = "Incident Detail ID " + $scope.selectedIncident.id;
        } else if (type === "group") {
            title = "Group";
            name = "Group Detail ID " + $scope.selectedGroup.id;
        }

        if (title === '' || name === '') {
            console.error("No valid modal type input provided.");
            return;
        }

        ModalService.showModal({
            templateUrl: "resources/html/templates/complex.html",
            controller: "ComplexController",
            inputs: {
                title: "Delete " + title + " Confirmation:",
                name: name
            }
        }).then(function(modal) {
            modal.element.modal({backdrop: 'static'});
            modal.close.then(function(result) {
                if (result.answer === 'Yes' && type === "incident") {
                    $scope.deleteI($scope.selectedIncident.id);
                } else if (result.answer === 'Yes' && type === "group") {
                    $scope.deleteG($scope.selectedGroup.id);
                }
            });
        });
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

    // load array constants
    $scope.options = options;
    $scope.locuss = locuss;
    $scope.alerted_bys = alerted_bys;
    // $scope.errors = errors;  // uses the hard coded errors constant
    $scope.statuss = statuss;
    $scope.incidentstatuss = incidentstatuss;
    $scope.recipents = recipents;
    // set the defaults
    $scope.selectedStatus = $scope.statuss[0]; // This is for status field in group detail..
    $scope.selectedGroupStatus = $scope.statuss[0];  // This is for the New Group button in incident detail..
    $scope.selectedIncident = null;
    $scope.selectedGroup = null;
    $scope.createResolution = null; // this variable handles the display of the resolution creation sub form
    $scope.createRootCA = null; // this variable handles the display of the RCA creation sub form
    $scope.disableButton = false;

    $scope.refreshData = function() {
        var newData = $scope.init();
        $scope.rowCollection = newData;
    };
    
    // these next two watches are for the date time picker fields when displaying an incident detail
    // from incident list filtered by groups
    // these next two watches are for the date time fields when displaying an incident detail
    // from incident list
    $scope.$watch("selectedIncident.startTime", function(val) {
    	if ($scope.selectedIncident) { // this needs to be a truthy test
    	    $scope.vDateStart = moment($scope.selectedIncident.startTime).format('MM-DD-YYYY HH:mm UTC');
    	}
    }, true);
    
    $scope.$watch("selectedIncident.endTime", function(val) {
    	if ($scope.selectedIncident) {// this needs to be a truthy test
			$scope.vDateEnd = moment($scope.selectedIncident.endTime).format('MM-DD-YYYY HH:mm UTC');
		}
    }, true);
    
    // this watch is for the date time field for the chronology section of an incident detail
    $scope.$watch("createChronology.chronologyDateTime", function(val) {
    	if ($scope.createChronology) {// this needs to be a truthy test 	
    		$scope.vDateTime = moment($scope.createChronology.chronologyDateTime).format('MM-DD-YYYY HH:mm');
    	}
    }, true);
    
    $scope.cancel = function(option) {
        switch (option) {
        	case "selectedIncident":
        		$scope.selectedIncident = null;
        		$scope.createChronology = null;
        		$scope.changedGroup();
        		break;
        	case "createChronology":
        		$scope.createChronology = null;
        		break;
            case "resolution":
                $scope.createResolution = false;
                $scope.disableButton = false;
                break;
            case "group":
                $scope.selectedGroup = false;
                break;
        }
        $scope.clearMsg();
    };
    
    $scope.clear = function(option) {
    	switch (option) {
        	case "chronology":
                $scope.createChronology.chronDescription = null;
                break;
        	case 'resolution':
        		$scope.selectedOwner = null;
        		$scope.selectedSriArtifact = null;
        		$scope.selectedEstCompletionDate = null;
        		$scope.selectedActualCompletionDate = null;
        		$scope.selectedHorizon = $scope.horizons[2];
        		$scope.selectedDescription = null;
        		$scope.selectedType = $scope.types[2];
        		$scope.selectedResStatus = $scope.status[0];
        		break;
    	}
    };
    
    $scope.select = function(option, object) {
        switch (option) {
        	case "incident":
        		console.log("inside selected incident");
                $scope.selectedIncident = object;
                console.log(JSON.stringify($scope.selectedIncident));
                getRelatedProducts($scope.selectedIncident.id);
                getRelatedChronologies($scope.selectedIncident.id);
                getRelatedErrorCode($scope.selectedIncident.id);
                getRelatedApplicationStatus($scope.selectedIncident.id);
                $scope.disableButton = false;
                
                // store relatedActions for viewing
                var actions = $scope.selectedIncident.relatedActions;
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
                
                $scope.groupModel.currentGroupName = $scope.selectedGroup.name;
                $scope.show = true;
        		break;
        	case "group":
        		console.log("inside selected Group");
                $scope.selectedGroup = object;
                $scope.disableButton = false;
        		break;
            case "chronology":
            	console.log("inside selected Chronology");
            	$scope.createChronology = new Object();	
                break;
        }
        $scope.clearMsg();
    };

    $scope.clearMsg = function() {
        $scope.messages = null;
        $scope.errormessages = null;
        $scope.chronmessages = null;
        $scope.chronerrormessages = null;
    };
    
    $scope.submitChronology = function() {
        $scope.clearMsg();
        
        // dates are currently in UTC format.. reset them to local timezone format for saving.. 
        var dateTimeValue = new Date($scope.createChronology.chronologyDateTime);
        
        var chronology = {
            "dateTime": dateTimeValue,
            "description": $scope.createChronology.chronDescription,
            "recordedBy": $scope.user.username,
            "incident": { id: $scope.selectedIncident.id }
        };

        console.log("inside submitChronology " + JSON.stringify(chronology));
        ChronologyService.saveChronology(chronology).then(
            function success(response) {
                if (response) {
                    $scope.chronmessages = "Chronology timeline for Incident tag " + $scope.selectedIncident.tag + " created.";
                    console.log("Chronology for Incident tag " + $scope.selectedIncident.tag + " created = " + JSON.stringify(response));
                    $scope.clear('chronology');
                    $scope.chronerrormessages = null;
                    getRelatedChronologies($scope.selectedIncident.id);
                } 
            },
            function error() {
                $scope.chronerrormessages = $rootScope.INCIDENT_CHRONOLOGY_SAVE_ERROR_MSG;
                $scope.chronmessages = null;
            });
    };
    
    $scope.remove = function(item) {
 	    console.log("inside remove " + JSON.stringify(item));
        ChronologyService.deleteChronology(item.id).then(
            function success(response) {
                if (response) {
                    $scope.chronmessages = "Chronology timeline for Incident tag " + $scope.selectedIncident.tag + " with id " + item.id + " deleted.";
                    console.log("Chronology timeline for Incident tag " + $scope.selectedIncident.tag + " with Chronology timeline id " + item.id + " deleted.");
                    $scope.chronerrormessages = null;
                    getRelatedChronologies($scope.selectedIncident.id);
                } 
            },
            function error() {
                $scope.chronerrormessages = "CHRONOLOGY_DELETE_FAILURE - Check logs or try again.";
                $scope.chronmessages = null;
            }); 
    };

    var data = [];

    $scope.changedGroup = function() {
        console.log("inside changedGroup, Group id " + $scope.selectedGroup.id);
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
        getData: function($defer, params) {
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

    // start - new datetimepicker stuff - https://github.com/Gillardo/bootstrap-ui-datetime-picker  
    $scope.open = {
    	startTime: false,
    	endTime: false,
        dateTime: false,
    	date4: false,
    	date5: false,
    };
    	  
    // Disable weekend selection
    $scope.disabled = function(date, mode) {
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
    	  
    $scope.openCalendar = function(e, date) {
    	 e.preventDefault();
    	 e.stopPropagation();

    	 $scope.open[date] = true;
    };
    // end - new datetimepicker stuff - https://github.com/Gillardo/bootstrap-ui-datetime-picker  	  
    
    $scope.generateUpdatedTag = function() {
    	var tag = null;	
    	if ($scope.selectedIncident.startTime && $scope.selectedProducts) {
        	var d = new Date($scope.selectedIncident.startTime);
        	tag = moment(d).format('MMDDYYYY_HHmm');
        	switch ($scope.selectedProducts.length) {
        		case 0:
        			$scope.selectedIncident.tag = null;
        			break;
        		case 1:
        			$scope.selectedIncident.tag = $scope.selectedProducts[0].shortName + "_" + tag;
        			break;
        		default:
        			$scope.selectedIncident.tag = "MULTI_" + tag;
        	}
    	}	
    };

    $scope.updateInSearch = function() {
        $scope.clearMsg();
        // Default values for the request.
        
        // generate tag again just in case the products and start-time were changed
        $scope.generateUpdatedTag();
        if (!$scope.selectedIncident.tag) {	
                $scope.errormessages = "INCIDENT_SAVE_FAILURE - problem with generating tag. Please fill in Start Date Time and Products.";
                return;   				 
        }
        
        var actions = $scope.actions.map(function(x) {
            return x.name
        }).join('|');
        
        var usersImpactedValue = $scope.selectedIncident.usersImpacted; 
        // dates are currently in UTC format.. reset them to local timezone format for saving.. 
        var startTimeValue = new Date($scope.selectedIncident.startTime);
        var endTimeValue;
        if ($scope.selectedIncident.endTime) {
        	endTimeValue = new Date($scope.selectedIncident.endTime);
        }
        
        // this is to protect the existing group details from being overwritten in case the user specifies an existing group to be reassigned too
        // don't change its description and status.. 
        var newGroupSpecified = false;
        if ($scope.groupModel.selectedNewGroup) {
        	if(!helperService.search($scope.groups, $scope.groupModel.selectedNewGroup)) {
        		newGroupSpecified = true;
            }	
        }
        
        var groupCurrentORNew = null;
        if ($scope.groupModel.selectedNewGroup && newGroupSpecified) {
        	groupCurrentORNew =  {
                    "name": $scope.groupModel.selectedNewGroup,
                    "description": $scope.selectedIncident.description + " " + $scope.selectedIncident.summary,
                    "status": $scope.selectedGroupStatus.value
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

        console.log("groupCurrentORNew " + groupCurrentORNew);
        
        var errorCode;
        Object.keys($scope.errors).forEach(function(key) {
        	if ($scope.errors[key].name === $scope.selectedIncident.error) {
        		errorCode = {
        						"id": $scope.errors[key].id,
        						"name": $scope.errors[key].name
        					}
        	}
        });
        
        var applicationStatus;
        Object.keys($scope.applicationStatuses).forEach(function(key) {
        	if ($scope.applicationStatuses[key].displayName === $scope.selectedIncident.applicationStatus) {
        		applicationStatus = {
        							"id": $scope.applicationStatuses[key].id,
        							}
        	}
        });

        $scope.enforceRequiredFields();
        
        var incident = {
            "id": $scope.selectedIncident.id,
            "version": $scope.selectedIncident.version,
            "tag": $scope.selectedIncident.tag,
            "severity": $scope.selectedIncident.severity,
            "locus": $scope.selectedIncident.locus,
            "description": $scope.selectedIncident.description,
            "usersImpacted": usersImpactedValue,
            "alertedBy": $scope.selectedIncident.alertedBy,
            "error": errorCode,
            "applicationStatus": applicationStatus,
            "transactionIdsImpacted": $scope.selectedIncident.transactionIdsImpacted,
            "startTime": startTimeValue,
            "endTime": endTimeValue,
            "incidentGroup": groupCurrentORNew,
            "incidentReport": $scope.selectedIncident.incidentReport,
            "callsReceived": $scope.selectedIncident.callsReceived,
            "products": $scope.selectedProducts,
            "customerImpact": $scope.selectedIncident.customerImpact,
            "name": $scope.selectedIncident.name,
            "reportOwner": $scope.selectedIncident.reportOwner,
            "summary" : $scope.selectedIncident.summary,
            "recordedBy": $scope.selectedIncident.recordedBy,
            "status": $scope.selectedIncident.status,
            "emailRecipents": $scope.selectedIncident.emailRecipents,
            "reviewedBy": $scope.selectedIncident.reviewedBy,
            "issue": $scope.selectedIncident.issue,
            "correctiveAction": $scope.selectedIncident.correctiveAction,
            "relatedActions": actions
        };
        
        if ($scope.groupModel.selectedNewGroup && newGroupSpecified) {  
        	// A new group was specified, so go ahead and create it and then save the incident with the new group 
        	// associated with it. This is using the chain promises technique. 
        	IncidentGroupService.saveGroup(groupCurrentORNew)
				.then(function (response) {
					if (response === "true") {
						console.log("inside updateInSearch with new Group " +  JSON.stringify(incident));
						$scope.errormessages = null;
						$scope.errormessages2 = null;
						return IncidentService.saveIncident(incident);
					} else {
						$scope.errormessages = "GROUP_SAVE_FAILURE - Creating new Group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.";
						console.error("GROUP_SAVE_FAILURE - Creating new Group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.");
						return $q.reject(); 
					}
				}, function(response) {
					$scope.errormessages = "GROUP_SAVE_FAILURE - Creating new Group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.";
					console.error("GROUP_SAVE_FAILURE - Creating new Group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.");
					return $q.reject(); 
				})
				.then(function (response) {
					if (response) {
                        $scope.getGroup(incident.id);
						$scope.messages = "Incident ID " + incident.id + " has been saved.";
						console.log("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with newly created Group " + groupCurrentORNew.name + ".");
                        $scope.refreshData(); 
						$scope.errormessages = null;
                        $scope.errormessages2 = null;
                        $scope.selectedIncident.version++; 
                        $scope.disableButton = true; 
                        $scope.groupModel.selectedNewGroup = null;
                        $scope.changedGroup();
					} else {
						$scope.errormessages = $rootScope.INCIDENT_SAVE_ERROR_MSG;
						console.error("Incident tag " + incident.tag + " with id " + incident.id +  " was unable to be saved with newly created Group " + groupCurrentORNew.name + ".");
					}
				}, function(response) {
					$scope.errormessages2 = $rootScope.INCIDENT_SAVE_ERROR_MSG;
					console.error("Incident tag " + incident.tag + " with id " + incident.id +  " was unable to be saved with newly created Group " + groupCurrentORNew.name + ".");
				});
        } else {
        	// An existing current group is still there with no new group specified.
        	// As such, go ahead and save the incident.. 
        	console.log("inside updateInSearch with existing Group " +  JSON.stringify(incident));
        	IncidentService.saveIncident(incident).then(
        			function success(response) {
        				if (response) {
                            $scope.getGroup(incident.id);
        					$scope.messages = "Incident ID " + incident.id + " has been saved.";
        					console.log("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with Group " + groupCurrentORNew + ".");
                            $scope.refreshData(); 
                            $scope.errormessages = null;
                            $scope.errormessages2 = null;
                            $scope.selectedIncident.version++; 
                            $scope.disableButton = true; 
                            $scope.groupModel.selectedNewGroup = null;
                            $scope.changedGroup();
        				} 
        			},
        			function error() {
        				$scope.errormessages = $rootScope.INCIDENT_SAVE_ERROR_MSG;
        			});
        }
    };

    // just do this for required fields that are not defaulted dropdown fields.
    $scope.enforceRequiredFields = function() {
        if ($scope.selectedIncident.description !== undefined && 
            $scope.selectedIncident.description !== null &&
            $scope.selectedIncident.description.trim() === "")
            $scope.selectedIncident.description = null;
    }

    $scope.getGroup = function(id) {
        $scope.clearMsg();
        if (id == null) return;
        IncidentService.getGroup(id).then(
            function success(response) {
                console.log("Group detail " + JSON.stringify(response));
                if (response) {
                    $scope.groupModel.currentGroupName = response.name;
                    // paid attention this is used for the ng-if on the ng-include div.. we need to wait for this callback to complete
                    // before displaying the included form.. as the included form creates a child scope.
                    // ng-include will copy at this moment all the data in the scope and set it to the sub\child scope.. 
                    // otherwise, current group field will be blank even though group is retrieved later on.. with this async call
                    $scope.show = true;  
                    console.log("Group retrieved for Incident ID " + id);
                } else {
                    console.error("Unable to retrieve Group for Incident ID " + id);
                }
            },
            function error() {
                $scope.errormessages = "GROUP_GET_FAILURE - Group may not exist, please try again.";
                // $rootScope.errors.push({ code: "GROUP_GET_FAILURE", message: "Group may not exist, please try again." });
            });
    };

    $scope.updateInGroupSearch = function() {
        $scope.clearMsg();
        // Default values for the request.
        var group = {
            "id": $scope.selectedGroup.id,
            "name": $scope.selectedGroup.name,
            "description": $scope.selectedGroup.description,
            "status": $scope.selectedGroup.status
        };

        console.log(group);
        console.log(JSON.stringify(group));
        IncidentGroupService.saveGroup(group).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Group ID " + group.id + " has been saved.";
                    console.log("Group ID " + group.id + " has been saved.");
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                } else {
                    $scope.errormessages = $rootScope.INCIDENT_GROUP_SAVE_ERROR_MSG;
                    console.error("Group ID " + group.id + " was unable to be saved.")
                }
            },
            function error() {
                $scope.errormessages = $rootScope.INCIDENT_GROUP_SAVE_ERROR_MSG;
                // $rootScope.errors.push({ code: "GROUP_SAVE_FAILURE", message: $rootScope.REQUIRED_FILEDS_GROUP_MSG });
            });
    };

    $scope.createRes = function() {
        $scope.clearMsg();
        $scope.clear('resolution');
        $scope.createResolution = true;
    };

    $scope.createRCA = function() {
        $scope.clearMsg();
        $scope.createRootCA = true;
    };

    $scope.submitRes = function() {
    	
        var resolution = {
            "description": $scope.selectedDescription,
            "status": $scope.selectedResStatus,
            "owner": $scope.selectedOwner,
            "sriArtifact": $scope.selectedSriArtifact,
            "estCompletionDate": $scope.selectedEstCompletionDate,
            "actualCompletionDate": $scope.selectedActualCompletionDate,
            "type": $scope.selectedType,
            "horizon": $scope.selectedHorizon,
            "incidentGroup": $scope.selectedGroup
        };
        // console.log(JSON.stringify(resolution));
        ResolutionService.saveResolution(resolution).then(
            function success(response) {
                if (response) {
                    $scope.messages = $scope.selectedHorizon.displayName + " Resolution created for Group " + '"' + $scope.selectedGroup.name + '".';
                    $scope.clear('resoluton');
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                } 
            },
            function error() {
                $scope.errormessages = $rootScope.RESOLUTION_SAVE_ERROR_MSG;
            });
    };

    $scope.deleteI = function(id) {
        $scope.clearMsg();
        IncidentService.deleteIncident(id).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Incident ID " + id + " has been deleted.";
                    console.log("Incident has been deleted = " + JSON.stringify(response));
                    $scope.changedGroup();
                } 
                $scope.disableButton = true;
            },
            function error() {
                $scope.errormessages = "INCIDENT_DELETE_FAILURE - Check logs or invalid Incident.";
            });
    };

    $scope.deleteG = function(id) {
        $scope.clearMsg();
        IncidentGroupService.deleteGroup(id).then(
            function success(response) {
            	console.log(response);
                if (response) {
                	if (response === "false") {              		
                		// group was not deleted a false was returned..
                		$scope.errormessages = "GROUP_DELETE_FAILURE - Check logs or child associated entities still exist.";
                		console.error("Group ID " + id + " was unable to be deleted.");
                		return;               		
                	}
                    $scope.messages = "Group ID " + id + " has been deleted.";
                    console.log("Group ID " + id + " has been deleted.");
                    $scope.disableButton = true;
                    $scope.refreshData();
                } else {
                    console.error("Group ID " + id + " was unable to be deleted.");
                }
            },
            function error() {
                $scope.errormessages = "GROUP_DELETE_FAILURE - Check logs or child associated entities still exist.";
                // $rootScope.errors.push({ code: "GROUP_DELETE_FAILURE", message: "Check logs or child associated incidents still exist." });
            });
    };
    
    $scope.deleteAllGroupOrphans = function() {
        $scope.clearMsg();
        document.body.style.cursor = "wait";
        IncidentGroupService.deleteAllGroupOrphans().then(
            function success(response) {
            	console.log(response);
                if (response) {
                	if (response === "false") {              		
                		// group was not deleted a false was returned..
                		$scope.errormessages = "GROUP_ORPHANS_DELETE_FAILURE - Check logs, or no orphan groups to delete or problem deleting existing orphan groups.";
                		console.error("GROUP_ORPHANS_DELETE_FAILURE - Check logs, or no orphan groups to delete or problem deleting existing orphan groups.");
                		document.body.style.cursor = "default";
                		return;               		
                	}
                    $scope.messages = "All orphan groups have been deleted.";
                    console.log("All orphan groups have been deleted.");
                    $scope.disableButton = true;
                    document.body.style.cursor = "default";
                    $scope.refreshData();
                } 
            },
            function error() {
                $scope.errormessages = "GROUP_ORPHANS_DELETE_FAILURE - Check logs, or no orphan groups to delete or problem deleting existing orphan groups.";
                document.body.style.cursor = "default";
            });
    };
    
});

app.controller('RootCauseChildController', function ($rootScope, $scope, ReferenceDataService, RcaService, $filter) {

    $scope.init = function() {
        $scope.messages = null;
        $scope.errormessages = null;
        $scope.selectedProblem = null;
        $scope.selectedDueDate = null;
        $scope.selectedCompletionDate = null;
        $scope.selectedOwner = null;
        $scope.whys = null;
    };

    (function () {
        ReferenceDataService.getStatus().then(
        function success(response) {
            $scope.status = response;
            // console.log("getStatus " + JSON.stringify($scope.status));
            $scope.selectedRCAStatus = $scope.status[1];
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
                // console.log("getCategories " + JSON.stringify($scope.categories));
                $scope.selectedCategory = $scope.categories[15];
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
                // console.log("getCategories " + JSON.stringify($scope.resources));
                $scope.selectedResource = $scope.resources[11];
            },
            function error() {
                $rootScope.errors.push({
                    code: "RESOURCES_GET_FAILURE",
                    message: "Error retrieving resources."
                });
            });
    })();

    (function () {
        ReferenceDataService.getCategories().then(
            function success(response) {
                $scope.resources = response;
                // console.log("getCategories " + JSON.stringify($scope.resources));
                $scope.selectedResource = $scope.resources[11];
            },
            function error() {
                $rootScope.errors.push({
                    code: "RESOURCES_GET_FAILURE",
                    message: "Error retrieving resources."
                });
            });
    })();

    $scope.clear = function() {
        $scope.selectedProblem = null;
        $scope.selectedDueDate = null;
        $scope.selectedCompletionDate = null;
        $scope.selectedCategory = $scope.categories[15];
        $scope.selectedOwner = null;
        $scope.selectedResource = $scope.resources[11];
        $scope.selectedRCAStatus = $scope.status[1];
        $scope.whys = [];
        $scope.messages = null;
        $scope.errormessages = null;
    };

    $scope.cancel = function() {
        $scope.$parent.createRootCA = false;
        $scope.disableButton = false;
        $scope.clear();
    };

    // start - this is for rca create screen from group detail screen
    $scope.rca = {};
    $scope.whys = [];

    $scope.filterWhy = function(why) {
        return why.isDeleted !== true;
    };

    $scope.deleteWhy = function(id) {
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

    $scope.addWhy = function() {
        if ($scope.whys) {
            if ($scope.whys.length < 5) {
                $scope.whys.push({
                    id: $scope.whys.length + 1,
                    name: '',
                    isNew: true
                });
            } else {}
        }
    };
    // end - this is for rca create screen from group detail screen

    // add related actions stuff
    $scope.actions = [];

    $scope.filterAction = function(action) {
        return action.isDeleted !== true;
    };

    $scope.deleteAction = function(id) {
    	console.log("action id deleted = " + id);
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

    $scope.addAction = function() {
        if ($scope.actions.length < 10) {
            $scope.actions.push({
                id: $scope.actions.length + 1,
                name: '',
                isNew: true
            });
        } else {}
    };
    // END OF - add related actions stuff

    $scope.submit = function() {
        var whys = $scope.whys.map(function(x) {
            return x.name
        }).join('|');
        var rca = {
            "problem": $scope.selectedProblem,
            "whys": whys,
            "dueDate": $scope.selectedDueDate,
            "completionDate": $scope.selectedCompletionDate,
            "category": $scope.selectedCategory,
            "resource": $scope.selectedResource,
            "owner": $scope.selectedOwner,
            "status": $scope.selectedRCAStatus,
            "incidentGroup": $scope.$parent.selectedGroup
        };

        RcaService.saveRca(rca).then(
            function success(response) {
                if (response) {
                    $scope.messages = $scope.selectedCategory.displayName + " Root Cause created for Group " + '"' + $scope.$parent.selectedGroup.name + '".';
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                } 
            },
            function error() {
                $scope.errormessages = $rootScope.RC_SAVE_ERROR_MSG;
            });
    };

});