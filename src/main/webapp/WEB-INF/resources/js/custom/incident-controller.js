app.controller('IncidentController', function($http, $q, $rootScope, $scope, $log, $timeout, $filter, IncidentService, ReferenceDataService, ProductService, limitToFilter, IncidentGroupService, locuss, alerted_bys, options, statuss, incidentstatuss, recipents, ModalService, ChronologyService, helperService) {
    $scope.init = function() {
        IncidentService.getIncidents().then(
            function success(response) {
                //            	console.log(JSON.stringify(response));
                $scope.incidents = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "INCIDENTS_GET_FAILURE",
                    message: "Error retrieving incidents."
                });
            });
    };
    
    // This was using getProducts() before but changed it to getActiveProducts() call which is a bit
    // more optimized. It returns data sorted and hence no need to sort in JavaScript level with the
    // helperService I created.  getActiveProducts() is used because this only returns active products, 
    // not all products, which is required now for the dropdown in incident. 
    (function() {
        ProductService.getActiveProducts().then(
            function success(response) {
                // console.log(JSON.stringify(response));
            	// response = helperService.sortByKey(response, 'shortName');
                $scope.myProducts = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "PRODUCTS_GET_FAILURE",
                    message: "Error retrieving products."
                });
            });
    }());
    
    (function() {
        IncidentService.getErrorConditions().then(
            function success(response) {
            	// console.log("getErrorConditions = " + JSON.stringify(response));
            	$scope.errors = response;
            	$scope.selectedError = $scope.errors[0];  // default error dropdown for create screen
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
                    code: "APPLICATION_STATUSES_GET_FAILURE",
                    message: "Error retrieving application statuses."
                });
            });
    }());

    function getGroups() {
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
    getGroups();

    $scope.showOnDelete = function() {
        var title = "Incident";
        var name = "Incident Detail ID " + $scope.selectedIncident.id;

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
                if (result.answer == 'Yes') {
                    $scope.deleteI($scope.selectedIncident.id);
                }
            });
        });
    };

    $scope.refreshData = function() {
        var newData = $scope.init();
        $scope.rowCollection = newData;
    };
    $scope.selectedIncident = null;
    
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
    
    // paid attention here.. this object is used for the ng-include directive which creates its own child scope.
    // since it relies on scope inheritance to resolve bindings, then a ng-model reference should have a '.' in it to
    // resolve properly. otherwise, selectedNewGroup used as a single variable outside an object for the drop down
    // list, it will create a shadow variable on the child scope that is a copy of the variable in the parent scope.
    // This breaks the model binding.. 
    $scope.groupModel = {
    		currentGroupName: null,
    		selectedNewGroup: null	
    };
       
    $scope.select = function(option, object) {
        switch (option) {
        	case "incident":
        		console.log("inside selected incident");
                $scope.selectedIncident = object;
                $scope.selectedIncident.error = $scope.selectedIncident.errorName;
                $scope.selectedIncident.applicationStatus = $scope.selectedIncident.applicationStatusName;
                console.log(JSON.stringify($scope.selectedIncident));
                getRelatedProducts($scope.selectedIncident.id);
                getRelatedChronologies($scope.selectedIncident.id);
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
                
                $scope.getGroup($scope.selectedIncident.id);  
        		break;
            case "chronology":
            	console.log("inside selected chronology");
            	$scope.createChronology = new Object();	
                break;
        }
        $scope.clearMsg();
    };

    // get selected Incident's related products for display
    var getRelatedProducts = function(id) {
        IncidentService.getProducts(id).then(
            function success(response) {
                console.log(JSON.stringify(response));
                $scope.selectedProducts = response;
                console.log("PRODUCTS " + response);
            },
            function error() {
                $rootScope.errors.push({
                    code: "PRODUCTS_GET_FAILURE",
                    message: "Error retrieving products."
                });
            });
    };
    
    // get selected Incident's related chronologies for display
    var getRelatedChronologies = function(id) {
        IncidentService.getChronologies(id).then(
            function success(response) {
                console.log("Chronologies " + JSON.stringify(response));
                $scope.selectedChronologies = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "CHRONOLOGIES_GET_FAILURE",
                    message: "Error retrieving chronologies."
                });
            });
    };

    // load in array constants 
    $scope.options = options;
    $scope.locuss = locuss;
    $scope.alerted_bys = alerted_bys;
    // $scope.errors = errors;   // uses the hard coded errors constant
    $scope.errors = $scope.errorNames;
    $scope.statuss = statuss;
    $scope.incidentstatuss = incidentstatuss;
    $scope.recipents = recipents;
    // set defaults
    $scope.selectedOption = $scope.options[0];
    $scope.selectedLocus = $scope.locuss[2];
    $scope.selectedAlertedBy = $scope.alerted_bys[1];
    // $scope.selectedError = $scope.errors[8];   // uses the hard coded errors constant for setting default for create screen
    $scope.selectedGroupStatus = $scope.statuss[0];
    $scope.selectedStatus = $scope.incidentstatuss[0];
    $scope.selectedRecipents = $scope.recipents[1];
    $scope.callsReceived = 0;
    $scope.usersImpacted = 100;
    $scope.transactionIdsImpacted = 0;
    $scope.disableButton = false;
    if ($scope.user) {
    	$scope.recordedBy = $scope.user.username;
    }
    $scope.showResolution = false;
    
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
    
    $scope.generateTag = function() {
    	var tag = null;	
    	if ($scope.startTime && $scope.selectedProducts) {
        	var d = new Date($scope.startTime);
        	tag = moment(d).format('MMDDYYYY_HHmm');
        	switch ($scope.selectedProducts.length) {
        		case 0:
        			$scope.tag = null;
        			break;
        		case 1:
        			$scope.tag = $scope.selectedProducts[0].shortName + "_" + tag;
        			break;
        		default:
        			$scope.tag = "MULTI_" + tag;
        	}
    	}	
    };
    
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
    
    $scope.cancel = function(option) {
        switch (option) {
            case "createChronology":
            	$scope.createChronology = null;
                break;
            case "selectedIncident":
            	$scope.selectedIncident = null;
            	$scope.createChronology = null;
                break;
        }
        $scope.clearMsg();
    };
    
    $scope.clearMsg = function() {
        $scope.messages = null;
        $scope.errormessages = null;
        $scope.messages2 = null;
        $scope.errormessages2 = null;
        $scope.chronmessages = null;
        $scope.chronerrormessages = null;
    };
    
    $scope.clear = function(option) {
        switch (option) {
            case "chronology":
                $scope.createChronology.chronDescription = null;
                break;
            case "incident":
                $scope.tag = null;
                $scope.selectedOption = $scope.options[0];
                $scope.selectedLocus = $scope.locuss[2];
                $scope.selectedAlertedBy = $scope.alerted_bys[1];
                $scope.selectedError = $scope.errors[2];
                $scope.selectedApplicationStatus = $scope.applicationStatuses[0];
                $scope.transactionIdsImpacted = 0;
                $scope.incidentGroup = null;
                $scope.callsReceived = 0;
                $scope.usersImpacted = 100;
                $scope.startTime = null;
                $scope.vDateStart = null;
                $scope.endTime = null;
                $scope.vDateEnd = null;
                $scope.description = null;
                $scope.incidentReport = null;
                $scope.selectedProducts = null;
                $scope.customerImpact = null;
                $scope.name = null;
                $scope.reportOwner = null;
                $scope.summary = null;
                $scope.recordedBy = null;
                $scope.selectedStatus = $scope.incidentstatuss[0];
                $scope.selectedRecipents = $scope.recipents[1];
                $scope.correctiveAction = null;
                break;
            case "group":
                $scope.selectedGroupName = null;
                $scope.selectedGroupDescription = null;
                // $scope.$scope.selectedGroupStatus = $scope.statuss[0];
                break;
        }  
    };
    
    // START OF WATCHES SECTION   
    // these next two watches are for the date time fields on the create incident page.
    $scope.$watch("startTime", function(val) {
    	if ($scope.startTime) {  // this needs to be a truthy test
    		$scope.vDateStart = moment($scope.startTime).format('MM-DD-YYYY HH:mm'); 
    	}
    }, true);
    
    $scope.$watch("endTime", function(val) {
    	if ($scope.endTime) {// this needs to be a truthy test 	
    		$scope.vDateEnd = moment($scope.endTime).format('MM-DD-YYYY HH:mm');
    	}
    }, true);
    
    // these next two watches are for the date time fields when displaying an incident detail
    // from incident list
    $scope.$watch("selectedIncident.startTime", function(val) {
    	if ($scope.selectedIncident) { // this needs to be a truthy test
    		$scope.vDateStart = moment($scope.selectedIncident.startTime).format('MM-DD-YYYY HH:mm');  		
    	}
    }, true);
    
    $scope.$watch("selectedIncident.endTime", function(val) {
    	if ($scope.selectedIncident) {// this needs to be a truthy test
			$scope.vDateEnd = moment($scope.selectedIncident.endTime).format('MM-DD-YYYY HH:mm');
		}
    }, true);
    
    // this watch is for the date time field for the chronology section of an incident detail
    $scope.$watch("createChronology.chronologyDateTime", function(val) {
    	if ($scope.createChronology) {// this needs to be a truthy test 	
    		$scope.vDateTime = moment($scope.createChronology.chronologyDateTime).format('MM-DD-YYYY HH:mm');
    	}
    }, true);
    
    $scope.$watch("selectedStatus", function(val) {
    	if ($scope.selectedStatus.name === "Closed") {// this needs to be a truthy test 	
    		$scope.showResolution = true;
    	} else {
    		$scope.showResolution = false;
    	}
    }, true);
    // END OF WACTHES SECTION
    
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
                    console.info("Group retrieved for incident " + id);
                } else {
                    console.error("Unable to retrieve group for incident " + id);
                }
            },
            function error() {
                $scope.errormessages = "Search operation failure, Group may not exist, please try again";
                // $rootScope.errors.push({ code: "GROUP_GET_FAILURE", message: "Operation failure, Group may not exist, please try again" });
            });
    };

    $scope.getIncident = function(id) {
        $scope.clearMsg();
        IncidentService.getIncident(id).then(
            function success(response) {
                // console.log(JSON.stringify(response));
                if (response) {
                    $scope.incident = response;
                    console.info("Incident retrieved for incident " + id);
                } else {
                    console.error("Unable to retrieve incident for incident " + id);
                }
            },
            function error() {
                $scope.errormessages = "Search operation failure, Incident may not exist, please try again";
                // $rootScope.errors.push({ code: "INCIDENT_GET_FAILURE", message: "Search operation failure, Incident may not exist, please try again" });
            });
    };

    $scope.deleteI = function(id) {
        $scope.clearMsg();
        IncidentService.deleteIncident(id).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Incident ID " + id + " has been deleted.";
                    console.info("Incident ID " + id + " has been deleted.");
                    $scope.refreshData();
                    $scope.errormessages = null;
                } else {
                    $scope.errormessages = "Delete operation failure, check logs or invalid incident.";
                    console.error("Incident ID " + id + " was unable to be deleted.")
                }
                $scope.disableButton = true;
            },
            function error() {
                $scope.errormessages = "Delete operation failure, check logs or invalid incident.";
                // $rootScope.errors.push({ code: "INCIDENT_DELETE_FAILURE", message: "Delete operation failed, check logs or invalid incident." });
            });
    };

    $scope.save = function(incident) {
        console.log("save incident = " + JSON.stringify(incident));
        IncidentService.saveIncident(incident).then(
            function success(response) {
                if (response) {
                    console.info("Incident " + incident.id + " has been saved.")
                } else {
                    console.error("Incident " + incident.id + " was unable to be saved.")
                }
            },
            function error() {
                $rootScope.errors.push({
                    code: "INCIDENT_SAVE_FAILURE",
                    message: "Error saving incident."
                });
            });
    };

    $scope.updateInSearch = function() {
        $scope.clearMsg();
        // Default values for the request.
        
        // generate tag again just in case the products and start-time were changed
        $scope.generateUpdatedTag();
        if (!$scope.selectedIncident.tag) {	
                $scope.errormessages = "Save operation failure, problem with generating tag. Please fill in Products and Start Time fields.";
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
					}
					else {
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
						$scope.getGroup(incident.id);
						$scope.messages = "Incident tag " + incident.tag + " with id " + incident.id + " has been saved with newly created group " + groupCurrentORNew.name + ".";
						console.info("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with newly created group " + groupCurrentORNew.name + ".");
						$scope.refreshData(); 
						$scope.errormessages = null;
                        $scope.errormessages2 = null;
                        $scope.selectedIncident.version++; 
                        $scope.disableButton = true; 
                        $scope.groupModel.selectedNewGroup = null;
					} else {
						$scope.errormessages = "Save operation failure, make sure the following required fields are filled: Description, Locus, Error Condition, and Start Time, please try again";
						console.error("Incident tag " + incident.tag + " with id " + incident.id +  " was unable to be saved with newly created group " + groupCurrentORNew.name + ".");
					}
				}, function(response) {
					$scope.errormessages2 = "Save operation failure, make sure the following required fields are filled: Description, Locus, Error Condition, and Start Time, please try again";
					console.error("Incident tag " + incident.tag + " with id " + incident.id +  " was unable to be saved with newly created group " + groupCurrentORNew.name + ".");
				});
        } else {
        	// An existing current group is still there with no new group specified.
        	// As such, go ahead and save the incident.. 
        	console.log("inside updateInSearch with existing group " +  JSON.stringify(incident));
        	IncidentService.saveIncident(incident).then(
        			function success(response) {
        				if (response) {
        					$scope.getGroup(incident.id);
        					$scope.messages = "Incident tag " + incident.tag + " with id " + incident.id + " has been saved with group " + groupCurrentORNew + ".";
        					console.info("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with group " + groupCurrentORNew + ".");
        					$scope.refreshData();
        					$scope.errormessages = null;
                            $scope.errormessages2 = null;
                            $scope.selectedIncident.version++;
                            $scope.disableButton = true; 
                            $scope.groupModel.selectedNewGroup = null;
        				} else {
        					$scope.errormessages = "Save operation failure, make sure the following required fields are filled: Description, Locus, Error Condition, and Start Time, please try again";
        					console.error("Incident tag " + incident.tag + " with id " + incident.id +  " has been saved with group " + groupCurrentORNew + ".");
        				}
        			},
        			function error(data) {
                        if (data.includes("OptimisticLockException")) {
                            $scope.errormessages = "Save operation failure, incident detail has been updated by another user since you have opened this incident for editing, please reload incident detail from search page and try again.";
                            $scope.refreshData();
                            return;
                        }
        				$scope.errormessages = "Save operation failure, make sure the following required fields are filled: Description, Locus, Error Condition, and Start Time, please try again";
             		    // $rootScope.errors.push({ code: "INCIDENT_SAVE_FAILURE", message: "Save operation failure, make sure the following required fields are filled: Description, Locus, Error Condition, and Start Time, please try again" });
        			});
        }
    };

    $scope.submit = function() {
        $scope.clearMsg();
        
        // generate tag if tag is empty and products and start-time exist.. 
        if (!$scope.tag) {
        	
        	$scope.generateTag();
        	if (!$scope.tag) {	
                	$scope.errormessages = "Save operation failure, Tag field not generated yet! Please fill in Products, Description and Start Time fields.";
                	return;   				
        	} 
        }
        
        // dates are currently in UTC format.. reset them to local timezone format for saving.. 
        var startTimeValue = new Date($scope.startTime);
        if ($scope.endTime) {
        	var endTimeValue = new Date($scope.endTime);
        }
        
        if (endTimeValue) {
        	if (startTimeValue > endTimeValue) {
        		$scope.errormessages = "End Date Time needs to be set after Start Date Time.";
        		return;
        	}
        } 	

        if (!$scope.description) {
            $scope.errormessages = "Save operation failure, please fill in description field.";
            return;   	
        }

        var summary = "";
        if ($scope.summary) {
        	summary = $scope.summary;
        } else {
        	summary = " ";
        }
        
        var group = null;
        if ($scope.incidentGroup) {
        	group = {
                "name": $scope.incidentGroup,
                "description": $scope.description + " " + summary,
            }	
        } else {
        	var myNameString = $scope.description + " " + summary;
        	group = {
                    "name": myNameString.substring(0,120),
                    "description": $scope.description + " " + summary,
            }
        }
        
        var incident = {
            "tag": $scope.tag,
            "severity": $scope.selectedOption.value,
            "locus": $scope.selectedLocus.value,
            "description": $scope.description,
            "usersImpacted": $scope.usersImpacted,
            "alertedBy": $scope.selectedAlertedBy.value,
            "error": {
            	"id": $scope.selectedError.id,
            	"name": $scope.selectedError.name
            },
            "applicationStatus": $scope.selectedApplicationStatus,
            "transactionIdsImpacted": $scope.transactionIdsImpacted,
            "startTime": startTimeValue,
            "endTime": endTimeValue,
            "incidentGroup": group,
            "incidentReport": $scope.incidentReport,
            "callsReceived": $scope.callsReceived,
            "products": $scope.selectedProducts,
            "customerImpact": $scope.customerImpact,
            "name": $scope.name,
            "reportOwner": $scope.reportOwner,
            "summary" : summary,
            "recordedBy": $scope.user.username,
            "status": $scope.selectedStatus.value,
            "emailRecipents": $scope.selectedRecipents.value,
            "correctiveAction": $scope.correctiveAction
        };

        console.log("inside submit " + JSON.stringify(incident));
        document.body.style.cursor = "wait";
        IncidentService.saveIncident(incident).then(
            function success(response) {
            	document.body.style.cursor = "default";
                if (response) {
                    $scope.messages = "Incident with tag " + incident.tag + " created.";
                    console.info("New Incident tag " + incident.tag + " has been saved.");
                    // $scope.clear('incident');
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                } else {
                    $scope.errormessages = "Save operation failure, make sure the following required fields are filled: Technical Description, Locus, Error Condition, and Start Time, please try again";
                    console.error("New Incident tag " + incident.tag + " was unable to be saved.")
                }
            },
            function error() {
            	document.body.style.cursor = "default";
                $scope.errormessages = "Save operation failure, make sure the following required fields are filled: Technical Description, Locus, Error Condition, and Start Time, please try again";
            //   $rootScope.errors.push({ code: "INCIDENT_SAVE_FAILURE", message: "Save operation failure, make sure the following required fields are filled: Description, Locus, Error Condition, and Start Time, please try again" });
            });
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
       
       document.body.style.cursor = "wait";
       console.log("inside submitChronology " + JSON.stringify(chronology));
       ChronologyService.saveChronology(chronology).then(
           function success(response) {
        	   document.body.style.cursor = "default";
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
        	   document.body.style.cursor = "default";
               $scope.chronerrormessages = "Save operation failure, check logs or try again.";
               $scope.chronmessages = null;
            //  $rootScope.errors.push({ code: "CHRONOLOGY_SAVE_FAILURE", message: "Save operation failure, check logs and make sure the following required fields are filled: Date Time and Description." });
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
            //    $rootScope.errors.push({ code: "CHRONOLOGY_SAVE_FAILURE", message: "Save operation failure, check logs and make sure the following required fields are filled: Date Time and Description." });
           });
   };

});