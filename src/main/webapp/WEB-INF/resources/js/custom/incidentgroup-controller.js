app.controller('IncidentGroupController', function($http, $rootScope, $filter, $scope, IncidentGroupService, IncidentService, ResolutionService, ReferenceDataService, ngTableParams, locuss, alerted_bys, options, statuss, incidentstatuss, recipents, ModalService, RcaService, ProductService, ChronologyService, helperService) {
    $scope.init = function() {
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
    };
    
    // This was using getProducts before but changed it to getActiveProducts() call which is a bit
    // more optimized. It returns data sorted and hence no need to sort in JavaScript level with the
    // helperService I created.  getActiveProducts() is used because this only returns active products 
    // not all products which is required now for the dropdown in incident. 
    (function() {
        ProductService.getActiveProducts().then(
            function success(response) {
//              console.log("getActiveProducts " + JSON.stringify(response));
//            	response = helperService.sortByKey(response, 'shortName');
                $scope.myProducts = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "GROUPS_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    }());
    
    (function() {
        IncidentService.getErrorConditions().then(
            function success(response) {
//            	console.log("getErrorConditions " + JSON.stringify(response));
            	$scope.errors = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "ERROR_CONDITION_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    }());
    
    (function() {
    	ReferenceDataService.getApplicationStatus().then(
            function success(response) {
//            	console.log("getIncidentStatus = " + JSON.stringify(response));
                $scope.applicationStatuses = response;
                $scope.selectedApplicationStatus = $scope.applicationStatuses[0];
            },
            function error() {
                $rootScope.errors.push({
                    code: "STATUS_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    }());
    
    // get selected Incident's related products for display
    var getRelatedProducts = function(id) {
        IncidentService.getProducts(id).then(
            function success(response) {
//              console.log(JSON.stringify(response));
                $scope.selectedProducts = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "PRODUCTS_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    };
    
    // get selected Incident's related chronologies for display
    var getRelatedChronologies = function(id) {
        IncidentService.getChronologies(id).then(
            function success(response) {
//              console.log("Chronologies " + JSON.stringify(response));
                $scope.selectedChronologies = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "CHRONOLOGIES_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    };
    
    // get selected Incident's Application Status for display
    var getRelatedApplicationStatus = function(id) {
        IncidentService.getApplicationStatus(id).then(
                function success(response) {
//                  console.log("Application Status " + JSON.stringify(response));
                    $scope.selectedIncident.applicationStatus = response.displayName;
                },
                function error() {
                    $rootScope.errors.push({
                        code: "APPLICATIONSTATUS_GET_FAILURE",
                        message: "Oooooops something went wrong, please try again"
                    });
                });
    };
    
    // get selected Incident's Error Code for display
    var getRelatedErrorCode = function(id) {
        IncidentService.getErrorCode(id).then(
                function success(response) {
//                  console.log("Error Code " + JSON.stringify(response));
                    $scope.selectedIncident.error = response.name;
                },
                function error() {
                    $rootScope.errors.push({
                        code: "ERRORCODE_GET_FAILURE",
                        message: "Oooooops something went wrong, please try again"
                    });
                });
    };

    $scope.types1 = ReferenceDataService.getTypes().then(
        function success(response) {
            $scope.types = response;
//          console.log("getTypes " + JSON.stringify($scope.types));
            $scope.selectedType = $scope.types[2];
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
//          console.log("getStatus " + JSON.stringify($scope.status));
            $scope.selectedResStatus = $scope.status[0];
            $scope.selectedRCAStatus = $scope.status[1];
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
//          console.log("getHorizons " + JSON.stringify($scope.horizons));
            $scope.selectedHorizon = $scope.horizons[2];
        },
        function error() {
            $rootScope.errors.push({
                code: "HORIZONS_GET_FAILURE",
                message: "Oooooops something went wrong, please try again"
            });
        });

    $scope.categories1 = ReferenceDataService.getCategories().then(
        function success(response) {
            $scope.categories = response;
//          console.log("getCategories " + JSON.stringify($scope.categories));
            $scope.selectedCategory = $scope.categories[15];
        },
        function error() {
            $rootScope.errors.push({
                code: "CATEGORIES_GET_FAILURE",
                message: "Oooooops something went wrong, please try again"
            });
        });

    $scope.resources1 = ReferenceDataService.getCategories().then(
        function success(response) {
            $scope.resources = response;
//          console.log("getCategories " + JSON.stringify($scope.resources));
            $scope.selectedResource = $scope.resources[11];
        },
        function error() {
            $rootScope.errors.push({
                code: "CATEGORIES_GET_FAILURE",
                message: "Oooooops something went wrong, please try again"
            });
        });
    
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
        if ($scope.whys.length < 5) {
            $scope.whys.push({
                id: $scope.whys.length + 1,
                name: '',
                isNew: true
            });
        } else {}
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
    
    $scope.showOnDelete = function(type) {
        ModalService.showModal({
            templateUrl: 'modal.html',
            controller: "ModalController"
        }).then(function(modal) {
            modal.element.modal({backdrop: 'static'});
            modal.close.then(function(result) {
                if (result === 'Yes' && type === "incident") {
                    $scope.deleteI($scope.selectedIncident.id);
                } else if (result === 'Yes' && type === "group") {
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
//  $scope.errors = errors;  // uses the hard coded errors constant
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
                break;
            case "rca":
                $scope.createRootCA = false;
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
        	case 'rca':
        		$scope.selectedProblem = null;
        		$scope.selectedDueDate = null;
        		$scope.selectedCompletionDate = null;
        		$scope.selectedCategory = $scope.categories[15];
        		$scope.selectedOwner = null;
        		$scope.selectedResource = $scope.resources[11];
        		$scope.selectedRCAStatus = $scope.status[1];
        		$scope.whys = null;
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
        		console.log("inside selected group");
                $scope.selectedGroup = object;
                $scope.disableButton = false;
        		break;
            case "chronology":
            	console.log("inside selected chronology");
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
                    $scope.chronmessages = "Chronology timeline for incident tag " + $scope.selectedIncident.tag + " created.";
                    console.info("Chronology for incident tag " + $scope.selectedIncident.tag + " created.");
                    $scope.clear('chronology');
                    $scope.chronerrormessages = null;
                    getRelatedChronologies($scope.selectedIncident.id);
                } else {
                    $scope.chronerrormessages = "Save operation failure, check logs and make sure the following required fields are filled: Date Time and Description.";
                    console.error("Chronology timeline for incident tag " + $scope.selectedIncident.tag + " was unable to be saved.");
                    $scope.chronmessages = null;
                }
            },
            function error() {
                $scope.chronerrormessages = "Save operation failure, check logs or try again.";
                $scope.chronmessages = null;
//              $rootScope.errors.push({ code: "CHRONOLOGY_SAVE_FAILURE", message: "Save operation failure, check logs and make sure the following required fields are filled: Date Time and Description." });
            });
    };
    
    $scope.remove = function(selectedChronologies, item) {
 	    console.log("inside remove " + JSON.stringify(item));
        ChronologyService.deleteChronology(item.id).then(
            function success(response) {
                if (response) {
                    $scope.chronmessages = "Chronology timeline for incident tag " + $scope.selectedIncident.tag + " with id " + item.id + " deleted.";
                    console.info("Chronology timeline for incident tag " + $scope.selectedIncident.tag + " with chronology timeline id " + item.id + " deleted.");
                    $scope.chronerrormessages = null;
                    getRelatedChronologies($scope.selectedIncident.id);
                } else {
                    $scope.chronerrormessages = "Delete operation failure, check logs or try again.";
                    console.error("Chronology timeline for incident tag " + $scope.selectedIncident.tag + " with id " + item.id + " was unable to be deleted.");
                    $scope.chronmessages = null;
                }
            },
            function error() {
                $scope.chronerrormessages = "Delete operation failure, check logs or try again.";
                $scope.chronmessages = null;
//              $rootScope.errors.push({ code: "CHRONOLOGY_SAVE_FAILURE", message: "Save operation failure, check logs and make sure the following required fields are filled: Date Time and Description." });
            }); 
    };

    var data = [];

    $scope.changedGroup = function() {
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
                    message: "Oooooops something went wrong, please try again"
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
                $scope.errormessages = "Save operation failure, problem with generating tag. Please fill in Products and Start Time.";
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
        }
        else {
        	// if an existing group specified in the Reassign Group field then use selectedNewGroup
        	// value don't form a new group variable for group creation
        	// or else if Reassign Group is empty use the current group value
        	if ($scope.groupModel.selectedNewGroup) {
        	   groupCurrentORNew = $scope.groupModel.selectedNewGroup;
        	}
        	else {
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
        
        var incident = {
            "id": $scope.selectedIncident.id,
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
						console.log("inside updateInSearch with new group " +  JSON.stringify(incident));
						$scope.errormessages = null;
						$scope.errormessages2 = null;
						return IncidentService.saveIncident(incident);
					} else {
						$scope.errormessages = "Save operation failure, creating new group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.";
						console.error("Save operation failure, creating new group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.");
						return $q.reject(); 
					}
				}, function(response) {
					$scope.errormessages = "Save operation failure, creating new group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.";
					console.error("Save operation failure, creating new group " + groupCurrentORNew.name + " failed, check logs. Incident will not be saved. Try again.");
					return $q.reject(); 
				})
				.then(function (response) {
					if (response) {
//						$scope.getGroup(incident.id);
						$scope.messages = "Incident tag " + incident.tag + " with id " + incident.id + " has been saved with newly created group " + groupCurrentORNew.name + ".";
						console.info("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with newly created group " + groupCurrentORNew.name + ".");
						$scope.errormessages = null;
						$scope.errormessages2 = null;
					
						var currentGroupSelected = {
				    			id: $scope.selectedGroup.id,
				    			name: $scope.selectedGroup.name
				    	};
						$scope.refreshData();
				    	$scope.selectedGroup.id = currentGroupSelected.id;
				    	$scope.selectedGroup.name = currentGroupSelected.name;
				
    					$scope.changedGroup();
					} else {
						$scope.errormessages = "Save operation failure, make sure the following required fields are filled: Technical Description, Locus, Error Condition, and Start Time, please try again";
						console.error("Incident tag " + incident.tag + " with id " + incident.id +  " was unable to be saved with newly created group " + groupCurrentORNew.name + ".");
					}
				}, function(response) {
					$scope.errormessages2 = "Save operation failure, make sure the following required fields are filled: Technical Description, Locus, Error Condition, and Start Time, please try again";
					console.error("Incident tag " + incident.tag + " with id " + incident.id +  " was unable to be saved with newly created group " + groupCurrentORNew.name + ".");
				});
        }
        else {
        	// An existing current group is still there with no new group specified.
        	// As such, go ahead and save the incident.. 
        	console.log("inside updateInSearch with existing group " +  JSON.stringify(incident));
        	IncidentService.saveIncident(incident).then(
        			function success(response) {
        				if (response) {
//        					$scope.getGroup(incident.id);
        					$scope.messages = "Incident tag " + incident.tag + " with id " + incident.id + " has been saved with group " + groupCurrentORNew + ".";
        					console.info("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with group " + groupCurrentORNew + ".");
        					$scope.refreshData();
        					$scope.errormessages = null;
        					$scope.errormessages2 = null;
        					$scope.changedGroup();
        				} else {
        					$scope.errormessages = "Save operation failure, make sure the following required fields are filled: Technical Description, Locus, Error Condition, and Start Time, please try again";
        					console.error("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with group " + groupCurrentORNew + ".");
        				}
        			},
        			function error() {
        				$scope.errormessages = "Save operation failure, make sure the following required fields are filled: Technical Description, Locus, Error Condition, and Start Time, please try again";
//             		    $rootScope.errors.push({ code: "INCIDENT_SAVE_FAILURE", message: "Save operation failure, make sure the following required fields are filled: Description, Locus, Error Condition, Start and End Times, please try again" });
        			});
        }
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
                    $scope.messages = "Group " + group.id + " has been saved.";
                    //                    	alert("Group " + group.id + " has been saved.");
                    console.info("Group " + group.id + " has been saved.");
                    $scope.errormessages = null;
                } else {
                    $scope.errormessages = "Save operation failure, make sure the following required fields are filled: Name and Description, please try again.";
                    console.error("Group " + group.id + " was unable to be saved.")
                }
            },
            function error() {
                $scope.errormessages = "Save operation failure, make sure the following required fields are filled: Name and Description, please try again.";
                //                  $rootScope.errors.push({ code: "INCIDENT_SAVE_FAILURE", message: "Save operation failure, make sure the following required fields are filled: Name and Description, please try again." });
            });
    };

    $scope.createRes = function() {
        $scope.clearMsg();
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
//      console.log(JSON.stringify(resolution));
        ResolutionService.saveResolution(resolution).then(
            function success(response) {
                if (response) {
                    $scope.messages = $scope.selectedHorizon.displayName + " resolution created for group " + '"' + $scope.selectedGroup.name + '".';
                    $scope.clear('resoluton');
                    $scope.errormessages = null;
                } else {
                    $scope.errormessages = "Create resolution failure, check logs or make sure the following fields are filled: Incident Group name, Horizon, Owner, Status, Description, and Estimated Comp Date.";
                    console.error("Create resolution failure, check logs or make sure the following fields are filled: ");
                }
            },
            function error() {
                $scope.errormessages = "Create resolution failure, check logs or make sure the following fields are filled: Incident Group name, Horizon, Owner, Status, Description, and Estimated Comp Date. ";
                //                  $rootScope.errors.push({ code: "RESOLUTION_CREATE_FAILURE", message: "Create resolution failure, check logs or make sure the following fields are filled: Incident Group name, Horizon, Status, Description, and Estimated Comp Date. "});
            });
    };

    $scope.submitRCA = function() {
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
            "incidentGroup": $scope.selectedGroup
        };
//      console.log(JSON.stringify(rca));
        RcaService.saveRca(rca).then(
            function success(response) {
                if (response) {
                    $scope.messages = $scope.selectedCategory.displayName + " RCA created for group " + '"' + $scope.selectedGroup.name + '".';
                    $scope.clear('rca');
                    $scope.errormessages = null;
                } else {
                    $scope.errormessages = "Create RCA failure, check logs or try again.";
                    console.error("Create resolution failure, check logs or try again.");
                }
            },
            function error() {
                $scope.errormessages = "Create resolution failure, check logs or try again.";
                //                  $rootScope.errors.push({ code: "RCA_CREATE_FAILURE", message: "Create resolution failure, check logs or try again."});
            });
    };

    $scope.deleteI = function(id) {
        $scope.clearMsg();
        IncidentService.deleteIncident(id).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Incident " + id + " has been deleted.";
                    console.info("Incident " + id + " has been deleted.")
                    $scope.changedGroup();
                } else {
                    console.error("Incident " + id + " was unable to be deleted.");
                }
                $scope.disableButton = true;
            },
            function error() {
                $scope.errormessages = "Delete operation failure, check logs or invalid incident.";
                //              $rootScope.errors.push({ code: "INCIDENT_DELETE_FAILURE", message: "Delete operation failed, check logs or invalid incident." });
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
                		$scope.errormessages = "Delete operation failure, check logs, or child associated incidents still exist.";
                		console.error("Group " + id + " was unable to be deleted.");
                		return;               		
                	}
                    $scope.messages = "Group " + id + " has been deleted.";
                    console.info("Group " + id + " has been deleted.");
                    $scope.disableButton = true;
                    $scope.refreshData();
                } else {
                    console.error("Group " + id + " was unable to be deleted.");
                }
            },
            function error() {
                $scope.errormessages = "Delete operation failure, check logs, or child associated incidents still exist.";
//              $rootScope.errors.push({ code: "GROUP_DELETE_FAILURE", message: "Delete operation failure, check logs or child associated incidents still exist." });
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
                		$scope.errormessages = "Delete operation failure, check logs, or no orphan groups to delete or problem deleting existing orphan groups.";
                		console.error("Delete operation failure, check logs, or no orphan groups to delete or problem deleting existing orphan groups.");
                		document.body.style.cursor = "default";
                		return;               		
                	}
                    $scope.messages = "All orphan groups have been deleted.";
                    console.info("All orphan groups have been deleted.");
                    $scope.disableButton = true;
                    document.body.style.cursor = "default";
                    $scope.refreshData();
                } else {
                    console.error("Delete operation failure, check logs, or no orphan groups to delete or problem deleting existing orphan groups.");
                    document.body.style.cursor = "default";
                }
            },
            function error() {
                $scope.errormessages = "Delete operation failure, check logs, or no orphan groups to delete or problem deleting existing orphan groups.";
                document.body.style.cursor = "default";
//              $rootScope.errors.push({ code: "GROUP_ORPHANS_DELETE_FAILURE", message: "Delete operation failure, check logs, or no orphan groups exist." });
            });
    };
    
});