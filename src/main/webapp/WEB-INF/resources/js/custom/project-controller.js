    app.controller('ProjectController', function($http, $rootScope, $scope, ProjectService, limitToFilter, $location, $routeParams, OwnersService, IncidentGroupService, ReferenceDataService, ModalService) {
        $scope.project = {};

        (function() {
        	OwnersService.getOwners().then(
        			function success(response) {
        				$scope.owners = response;
        			},
        			function error() {
        				$rootScope.errors.push({
        					code: "OWNERS_GET_FAILURE",
        					message: "Oooooops something went wrong, please try again"
        				});
        			});
        })();      
        
        $scope.clearMsg = function() {
            $scope.messages = null;
            $scope.errormessages = null;
            $scope.messages2 = null;
            $scope.errormessages2 = null;
        }

        if ($routeParams.id == null) {
        	
        	 $scope.clearMsg();
        	 
            $scope.pdlcStatus1 = ReferenceDataService.getPdlcStatus().then(
                function success(response) {
                    $scope.pdlcStatus = response;
                    $scope.project.pdlcStatus = $scope.pdlcStatus[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "CATEGORIES_GET_FAILURE",
                        message: "Oooooops something went wrong, please try again"
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
                        message: "Oooooops something went wrong, please try again"
                    });
                });

            $scope.wikiTypes1 = ReferenceDataService.getWikiTypes().then(
                function success(response) {
                    $scope.wikiTypes = response;
                    $scope.project.wikiType = $scope.wikiTypes[0];
                },
                function error() {
                    $rootScope.errors.push({
                        code: "RESOURCES_GET_FAILURE",
                        message: "Oooooops something went wrong, please try again"
                    });
                });
        }
        
        
        $scope.saveFilter = function() {
        	$rootScope.resolutionFilterText = $scope.filterOptions.filterText;
        	console.log($scope.gridOptions.sortInfo);
        	$rootScope.resolutionsortInfo = $scope.gridOptions.sortInfo;
            };    

        $scope.filterOptions = {
            filterText: ''
        };
        $scope.gridOptions = {
            data: 'myData',
            filterOptions: $scope.filterOptions,
            showFilter: true,
            showFooter: true,
            pagingOptions: $scope.pagingOptions,
            enablePinning: true,
            showGroupPanel: true,
            enableColumnResize: true,
            enableColumnReordering: true,
            columnDefs: [{
                field: "description",
                displayName: 'Description',
                width: '13%',
                visible:false,
                pinned: true
            }, {
                field: "id",
                displayName: 'Id',
                width: '6%',
                cellTemplate: '<div class="ngCellText" ng-class="col.colIndex()"><a ng-click="saveFilter()" href="#/project/retrieve/{{row.getProperty(col.field)}}">{{row.getProperty(col.field)}}</a></div>',
                pinned: true
            }, {
                field: "name",
                displayName: 'Name',
                width: '25%',
                pinned: true
            }, {
                field: "owners",
                displayName: 'Owners',
                width: '13%'
            }, {
                field: "status.displayName",
                displayName: 'Status',
                width: '6%'
            }, {
                field: "conflenceId",
                displayName: 'Conflence ID',
                width: '4%'
            }, {
                field: "estcompletionDate",
                displayName: 'Due Date',
                width: '9%',
                cellFilter: "date:'yyyy-MM-dd'"
            }, {
                field: "estEffort",
                displayName: 'Estimate Effort',
                width: '6%'
            }, {
                field: "actualCompletionDate",
                displayName: 'Completion Date',
                width: '9%',
                cellFilter: "date:'yyyy-MM-dd'"
            }, {
                field: "actualEffort",
                displayName: 'Actual Effort',
                width: '6%'
            }, {
                field: "recordingDate",
                displayName: 'Recording Date',
                width: '9%',
                cellFilter: "date:'yyyy-MM-dd'"
            }, {
                field: "pdlcStatus.displayName",
                displayName: 'PDLC Status',
                width: '7%'
            }, {
                field: "statusChangeDate",
                displayName: 'Status Change Date',
                width: '9%',
                cellFilter: "date:'yyyy-MM-dd'"
            }, {
                field: "wikiType.displayName",
                displayName: 'Solution Type',
                width: '19%'
            }, {
                field: "jiraId",
                displayName: 'Jira ID',
                width: '4%'
            }, {
                field: "eceId",
                displayName: 'ECDE ID',
                width: '6%'
            } ]
        };

        $scope.myData = [];
        $scope.init = function() {
        	
        	if($rootScope.resolutionFilterText != null){
        		$scope.filterOptions.filterText = $rootScope.resolutionFilterText;
        		$scope.gridOptions.sortInfo = $rootScope.resolutionsortInfo ;
        	}
        	
        	
            ProjectService.getProjects().then(
                function success(response, status, headers, config) {
                    var i, project;
         //           console.log(JSON.stringify(response));
                    $scope.myData = response;
                },
                function error() {
                    $scope.errormessages = "Search Project failed, please try again";
                });
        };


        $scope.getProject = function() { 
            ProjectService.getProject($routeParams.id).then(
                function success(response) { 
                	console.log("getProduct response = " + JSON.stringify(response));
                    if (response) {
                    	
                     	for(var j in $scope.owners) {
                    			$scope.owners[j].ticked = false;
                    	}
                    	
                        $scope.status = response[0];
                        $scope.pdlcStatus = response[1];
                        $scope.wikiTypes = response[2];
                        $scope.project = response[3];
                        
                        response[3].actualCompletionDate =  moment(response[3].actualCompletionDate).format('YYYY-MM-DD'); 
                        response[3].estcompletionDate =  moment(response[3].estcompletionDate).format('YYYY-MM-DD'); 
                        response[3].recordingDate =  moment(response[3].recordingDate).format('YYYY-MM-DD'); 
                        response[3].statusChangeDate =  moment(response[3].statusChangeDate).format('YYYY-MM-DD'); 
                        
                        OwnersService.getOwners().then(
                    			function success(response) {
                    				$scope.owners = response;
                    				var owners = $scope.project.owners;
                                    var ownersList = [];
                                    if (owners != null) {
                                        ownersList = owners.split("|");
                                    }
                                    var newOwners = [];
                                    for (var i in ownersList) {
                                    	for(var j in $scope.owners){
                                    		if(ownersList[i] === $scope.owners[j].userName){
                                    			$scope.owners[j].ticked = true;
                                    		}	
                                    	}
                                    }                                    
                    			},
                    			function error() {
                    				$rootScope.errors.push({
                    					code: "OWNERS_GET_FAILURE",
                    					message: "Oooooops something went wrong, please try again"
                    				});
                    			});
                        
                        var statusId = $scope.status.filter(function(item) {
                            return item.id === $scope.project.status.id;
                        });
                        $scope.project.status = statusId[0];

                        var pdlcStatusId = $scope.pdlcStatus.filter(function(item) {
                            return item.id === $scope.project.pdlcStatus.id;
                        });
                        $scope.project.pdlcStatus = pdlcStatusId[0];

                        var wikiTypesId = $scope.wikiTypes.filter(function(item) {
                            return item.id === $scope.project.wikiType.id;
                        });
                        $scope.project.wikiType = wikiTypesId[0];
                    } else {
                        $scope.errormessages = "View Project failed, please try again";
                    }
                },
                function error() {
                    $scope.errormessages = "View Project failed, please try again";
                });

        };


        $scope.showComplex = function(id) {
            $scope.name = " Project " + id.name;

            ModalService.showModal({
                templateUrl: "resources/html/templates/complex.html",
                controller: "ComplexController",
                inputs: {
                    title: "Delete Resolution Project Confirmation:",
                    name: $scope.name
                }
            }).then(function(modal) {
                $scope.name = id.name;
                modal.element.modal({backdrop: 'static'});
                modal.close.then(function(result) {
                    if (result.answer === 'Yes') {
                        ProjectService.deleteProject(id).then(
                            function success(response) {
                                if (response) {
                                    $scope.messages = "Project " + id + " has been deleted.";
                                } else {
                                    $scope.messages = ("Project " + id + " was unable to be deleted.");
                                }
                                $scope.back = true;
                                return;
                            },
                            function error() {
                                $scope.errormessages = "Operation failure, Project may not exist, please try again";
                                return;
                            });
                    } else {
                        return;
                    }
                });
            });

        };


        $scope.delete = function(id) {
            $scope.showComplex(id);

        };

        $scope.update = function() {
            $scope.clearMsg();
            
            if($scope.ownerlist !=null && $scope.ownerlist.length>0){
            	var owners = "";
            	for(i = 0; i < $scope.ownerlist.length; i++){
            		owners = owners + "|" + $scope.ownerlist[i].userName;
            	}
            	if(owners.length > 1)
            		$scope.project.owners = owners.substring(1, owners.length);
            }             
            
            $scope.back = false;
            
            if ($scope.project.actualCompletionDate === "Invalid date") {
            	$scope.project.actualCompletionDate = null;
            }
            if ($scope.project.estcompletionDate === "Invalid date") {
            	$scope.project.estcompletionDate = null;
            }
            if ($scope.project.recordingDate === "Invalid date") {
            	$scope.project.recordingDate = null;
            }
            if ($scope.project.statusChangeDate === "Invalid date") {
            	$scope.project.statusChangeDate = null;
            }
            
            var saveProject = {
            		"id": $scope.project.id,
            		"name": $scope.project.name,
            		"owners": $scope.project.owners,
            		"isHighPriority": $scope.project.isHighPriority,
            		"description": $scope.project.description,
            		"eceId": $scope.project.eceId,
            		"status": $scope.project.status,
            		"estEffort": $scope.project.estEffort,
            		"actualEffort": $scope.project.actualEffort,
            		"actualCompletionDate": $scope.project.actualCompletionDate,
            		"estcompletionDate": $scope.project.estcompletionDate,
            		"pdlcStatus": $scope.project.pdlcStatus,
            		"recordingDate": $scope.project.recordingDate,
            		"statusChangeDate": $scope.project.statusChangeDate,
            		"wikiType": $scope.project.wikiType,
            		"jiraId": $scope.project.jiraId,
            		"conflenceId": $scope.project.conflenceId	
            };

            console.log("Saving Project = " + JSON.stringify(saveProject));
            ProjectService.saveProject(saveProject).then(
                function success(response) {
                    if (response) {
                        if (!$scope.project.id)
                            $scope.messages = "New Project " + $scope.project.name + "has been created.";
                        else {
                            $scope.messages = "Project " + $scope.project.name + " has been saved.";
                        }
                        $scope.back = true;


                    } else {
                        $scope.errormessages = "Project " + $scope.project.id + " was unable to be saved.";
                    }
                },
                function error() {
                    $scope.errormessages = "Save Project failed. Name, Recording Date, Owner, Description and Due Date are required.";
                });
        };

        clear = function() {
            $location.path('/project/search');
        };

        $scope.new = function() {
            $location.path('/project/create');
        };

        $scope.linkResolutions = function(project) {
            $scope.resolution = {};
            popitup('/pst/#/resolution/linkProject/' + project.id);
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
        }

        $scope.cancel = function() {
            $location.path('/project/search');
        };
    });

    app.controller('ComplexController', [
        '$scope', '$element', 'title', 'name', 'close',
        function($scope, $element, title, name, close) {
            $scope.title = title;
            $scope.name = name;

            $scope.close = function(result) {
                close({
                    name: $scope.name,
                    answer: result
                }, 500);
            };

        }
    ]);

    app.controller('ResolutionProjectLinkingController', function($http, $rootScope, $scope, ResolutionService, limitToFilter, $location, $routeParams, IncidentGroupService, ReferenceDataService) {

        $scope.filterOptions = {
            filterText: ''
        };
        $scope.resolution = {};
        $scope.selected = [];
        $scope.gridOptions = {
            data: 'myData',
            showSelectionCheckbox: true,
            selectedItems: $scope.selected,
            filterOptions: $scope.filterOptions,
            showFilter: true,
            showFooter: true,
            columnDefs: [{
                field: "description",
                displayName: 'Description',
                width: '40%',
                pinned: true
            }, {
                field: "resolutionProjectName",
                displayName: 'Resolution Project',
                width: '13%'
            }, {
                field: "incidentGroupName",
                displayName: 'Incident Group Name',
                width: '40%',
                pinned: true
            }, {
                field: "owner",
                displayName: 'Owner',
                width: '40%'
            }]
        };

        $scope.myData = [];


        $scope.linkResolutions = function() {

            ResolutionService.getResolutions().then(
                function success(response, status, headers, config) {
                    var i, resolution;
      //              console.log(JSON.stringify(response));
                    for (i = 0; i < response.length; ++i) {
                        resolution = response[i];
                        if (resolution.projectId == $routeParams.project) {
                            $scope.selected.push(resolution);
                        }
                    }
                    $scope.myData = response;
                },
                function error() {
                    $rootScope.errors.push({
                        code: "INCIDENTS_GET_FAILURE",
                        message: "Oooooops something went wrong, please try again"
                    });
                });
        };

        $scope.getIncidentResolution = function() {
            ResolutionService.getIncidentResolution($routeParams.id).then(
                function success(response) {
                    if (response) {
                        $scope.groups = response[0];
                        $scope.horizons = response[1];
                        $scope.status = response[2];
                        $scope.types = response[3];
                        $scope.resolution = response[4];

                        var statusId = $scope.status.filter(function(item) {
                            return item.id === $scope.resolution.status.id;
                        });
                        $scope.resolution.status = statusId[0];

                        var typeId = $scope.types.filter(function(item) {
                            return item.id === $scope.resolution.type.id;
                        });
                        $scope.resolution.type = typeId[0];

                        var horizonId = $scope.horizons.filter(function(item) {
                            return item.id === $scope.resolution.horizon.id;
                        });
                        $scope.resolution.horizon = horizonId[0];

                        var incidentGroupId = $scope.groups.filter(function(item) {
                            return item.id === $scope.resolution.incidentGroup.id;
                        });
                        $scope.resolution.incidentGroup = incidentGroupId[0];

                    } else {
                        console.error("Unable to retrieve resolution for resolution " + id);
                    }
                },
                function error() {
                    $rootScope.errors.push({
                        code: "INCIDENT_GET_FAILURE",
                        message: "Operation failure, Resolution may not exist, please try again"
                    });
                });



        };

        $scope.update = function() {

            var statusId = $scope.status.filter(function(item) {
                return item.id === $scope.resolution.status.id;
            });
            var typeId = $scope.types.filter(function(item) {
                return item.id === $scope.resolution.type.id;
            });
            var horizonId = $scope.horizons.filter(function(item) {
                return item.id === $scope.resolution.horizon.id;
            });
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
                "type": typeId[0]
            };
            ResolutionService.saveResolution(resolution).then(
                function success(response) {
                    if (response) {
                        if (!$scope.resolution.id)
                            alert("New Resolution has been saved.")
                        else {
                            alert("Resolution " + resolution.id + " has been saved.");
                        }

                        $location.path('/resolution/search');

                    } else {
                        console.error("Resolution " + resolution.id + " was unable to be saved.")
                    }
                },
                function error() {
                    $rootScope.errors.push({
                        code: "INCIDENT_SAVE_FAILURE",
                        message: "Save operation failure, make sure the following required fields are filled: Tag, Description, Locus, Error Condition, Start and End Times, please try again"
                    });
                });
        };

        $scope.$on('ngGridEventData', function() {
            // $scope.gridOptions.selectRow(0, true);
        });

        $scope.getSelectedRows = function() {
            var resolutions = [];
            var inputs = [];
            for (i = 0; i < $scope.myData.length; ++i) {
                var resolution = $scope.myData[i];
                if (resolution.projectId == $routeParams.project) {
                    resolutions.push(resolution);
                }
            }

            var selectedRows = $scope.gridOptions.$gridScope.selectedItems;
            for (i = 0; i < selectedRows.length; ++i) {
                var item = selectedRows[i];
                if (resolutions.indexOf(item) > -1) {
                    resolutions.splice(resolutions.indexOf(item), 1);
                    continue;
                } else {
                    inputs.push({
                        "id": selectedRows[i].id,
                        "projectId": $routeParams.project,
                        "operation": 1
                    });
                }
            }
            for (i = 0; i < resolutions.length; ++i) {
                var item = resolutions[i];
                inputs.push({
                    "id": item.id,
                    "projectId": $routeParams.project,
                    "operation": 2
                });
            }
            
        if(inputs.length == 0){
        	$scope.messages = "Nothing changed."
        		return;
        }

        ResolutionService.saveLinkedResolutions(inputs).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Project/Resolution linking saved."

                } else {
                    $scope.errormessages = "Project/Resolution linking failed."
                }
                return;
            },
            function error() {
                $rootScope.errors.push({
                    code: "INCIDENT_SAVE_FAILURE",
                    message: "Save operation failure, make sure the following required fields are filled: Tag, Description, Locus, Error Condition, Start and End Times, please try again"
                });
            });
        }
    });