app.controller('ProductController', function ($rootScope, $scope, localStorageService, ProductService, $location, $routeParams, platforms, ModalService, OwnersService) {

    $scope.hideDuringLoading = false;
    $scope.disableButton = false;
    $scope.platforms = platforms;
    $scope.selectedPlatform = $scope.platforms[5];
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
        ProductService.search(search, pageno).then(
            function success(response) {
                $scope.data = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "PRODUCTS_GET_FAILURE",
                    message: "Error retrieving products."
                });
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
        $location.path('/product/search');
    };

    $scope.setSearchOwner = function () {
        for (var i in $scope.assignees) {
            if ($scope.assignees[i].userName === $scope.searchAssignee) {
                $scope.assignees[i].ticked = true;
                break;  // remove when we need to handle multiple owners one day..
            }
        }
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

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
                $scope.owners = response;
                $scope.setRouteSearchParms();
            },
            function error() {
                $rootScope.errors.push({
                    code: "OWNERS_GET_FAILURE",
                    message: "Error retrieving owners."
                });
            });
    })();

    (function () {
        OwnersService.getOwners().then(
            function success(response) {
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

    $scope.select = function (product) {
        $scope.selectedProduct = product;

        $scope.selectedProduct.startDate = moment($scope.selectedProduct.startDate).format('YYYY-MM-DD');
        if ($scope.selectedProduct.endDate)
            $scope.selectedProduct.endDate = moment($scope.selectedProduct.endDate).format('YYYY-MM-DD');

        $scope.disableButton = false;

        var owners = $scope.selectedProduct.owner;
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
    };

    $scope.cancel = function () {
        $scope.clearMsg();
        $scope.selectedProduct = null;
        $scope.refreshData();
    };

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errorMessages = null;
    };

    $scope.refreshData = function () {
        $scope.getData($scope.pageno);
    };

    $scope.showOnDelete = function () {
        var title = "Product";
        var name = "Product Detail ID " + $scope.selectedProduct.id;

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
                if (result.answer == 'Yes') {
                    deleteProduct($scope.selectedProduct.id);
                }
            });
        });
    };

    // only used for showOnDelete dont't expose this method in $scope for wider use
    var deleteProduct = function (id) {
        $scope.clearMsg();

        ProductService.deleteProduct(id).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Product ID " + id + " has been deleted.";
                    console.log("Product has been deleted = " + JSON.stringify(response));
                    $scope.refreshData();
                    $scope.errorMessages = null;
                    $scope.disableButton = true;
                }
            },
            function error() {
                $scope.errorMessages = "PRODUCT_DELETE_FAILURE - Check logs or invalid Product.";
            });
    };

    $scope.updateInSearch = function () {
        $scope.clearMsg();
        $scope.waiting(true);

        // Trigger validation flag.
        $scope.submitted = true;
        if ($scope.selectedProduct.incidentName === undefined ||
            $scope.selectedProduct.incidentName === null ||
            $scope.selectedProduct.incidentName.trim() === "") {
            $scope.incidentNameRequired = true;
            $scope.productForm.incidentName.$invalid = true;
        }
        if ($scope.selectedProduct.startDate === undefined ||
            $scope.selectedProduct.startDate === null ||
            $scope.selectedProduct.startDate.trim() === "") {
            $scope.startDateRequired = true;
            $scope.productForm.startDate.$invalid = true;
        }
        if ($scope.selectedProduct.clientName === undefined ||
            $scope.selectedProduct.clientName === null ||
            $scope.selectedProduct.clientName.trim() === "") {
            $scope.clientNameRequired = true;
            $scope.productForm.clientName.$invalid = true;
        }
        if ($scope.selectedProduct.shortName === undefined ||
            $scope.selectedProduct.shortName === null ||
            $scope.selectedProduct.shortName === "") {
            $scope.shortNameRequired = true;
            $scope.productForm.shortName.$invalid = true;
        }
        // End of validation

        enforceRequiredFields();

        if ($scope.ownerList != null && $scope.ownerList.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerList.length; i++) {
                owners = owners + "|" + $scope.ownerList[i].userName;
            }
            if (owners.length > 1)
                $scope.selectedProduct.owner = owners.substring(1, owners.length);
        }

        var product = {
            "id": $scope.selectedProduct.id,
            "incidentName": $scope.selectedProduct.incidentName,
            "shortName": $scope.selectedProduct.shortName,
            "owner": $scope.selectedProduct.owner,
            "clientName": $scope.selectedProduct.clientName,
            "startDate": $scope.selectedProduct.startDate,
            "endDate": $scope.selectedProduct.endDate,
            "maxWeeklyUptime": $scope.selectedProduct.maxWeeklyUptime,
            "platform": $scope.selectedProduct.platform,
            "revenue": $scope.selectedProduct.revenue,
            "users": $scope.selectedProduct.users
        };

        ProductService.saveProduct(product).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Product ID " + product.id + " has been saved.";
                    console.log("Product has been saved = " + JSON.stringify(response));
                    $scope.disableButton = true;
                    $scope.waiting(false);
                }
            },
            function error() {
                $scope.errorMessages = $rootScope.PRODUCT_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    // only used for updateInSearch dont't expose this method in $scope for wider use
    // just do this for required fields that are not defaulted dropdown fields.
    var enforceRequiredFields = function () {
        if ($scope.selectedProduct.incidentName !== undefined &&
            $scope.selectedProduct.incidentName !== null &&
            $scope.selectedProduct.incidentName.trim() === "")
            $scope.selectedProduct.incidentName = null;
        if ($scope.selectedProduct.shortName !== undefined &&
            $scope.selectedProduct.shortName !== null &&
            $scope.selectedProduct.shortName.trim() === "")
            $scope.selectedProduct.shortName = null;
        if ($scope.selectedProduct.maxWeeklyUptime !== undefined &&
            $scope.selectedProduct.maxWeeklyUptime !== null)
            $scope.selectedProduct.maxWeeklyUptime = null;
        if ($scope.selectedProduct.clientName !== undefined &&
            $scope.selectedProduct.clientName !== null &&
            $scope.selectedProduct.clientName.trim() === "")
            $scope.selectedProduct.clientName = null;
    };

    $scope.submit = function (form) {
        var platform;
        $scope.clearMsg();
        $scope.waiting(true);

        // Trigger validation flag.
        $scope.submitted = true;
        if ($scope.incidentName === null ||
            $scope.incidentName === undefined ||
            $scope.incidentName.trim() === "") {
            $scope.incidentNameRequired = true;
            $scope.productForm.incidentName.$invalid = true;
        }
        if ($scope.selectedPlatform === null ||
            $scope.selectedPlatform === undefined) {
            $scope.platformRequired = true;
            $scope.productForm.platform.$invalid = true;
        }
        if ($scope.startDate === null ||
            $scope.startDate === undefined) {
            $scope.startDateRequired = true;
            $scope.productForm.startDate.$invalid = true;
        }
        if ($scope.clientName === null ||
            $scope.clientName === undefined ||
            $scope.clientName.trim() === "") {
            $scope.clientNameRequired = true;
            $scope.productForm.clientName.$invalid = true;
        }
        if ($scope.shortName === null ||
            $scope.shortName === undefined ||
            $scope.shortName.trim() === "") {
            $scope.shortNameRequired = true;
            $scope.productForm.shortName.$invalid = true;
        }
        // End of validation

        if ($scope.selectedPlatform === null || $scope.selectedPlatform === undefined) {
            platform = null;
        } else {
            platform = $scope.selectedPlatform.value;
        }

        if ($scope.ownerList !== null && $scope.ownerList.length > 0) {
            var owners = "";
            for (i = 0; i < $scope.ownerList.length; i++) {
                owners = owners + "|" + $scope.ownerList[i].userName;
            }
            if (owners.length > 1)
                $scope.owner = owners.substring(1, owners.length);
        }

        var product = {
            "incidentName": $scope.incidentName,
            "platform": platform,
            "clientName": $scope.clientName,
            "shortName": $scope.shortName,
            "owner": $scope.owner,
            "startDate": $scope.startDate,
            "endDate": $scope.endDate,
            "maxWeeklyUptime": $scope.maxWeeklyUptime,
            "revenue": $scope.revenue,
            "users": $scope.users
        };

        ProductService.saveProduct(product).then(
            function success(response) {
                if (response) {
                    $scope.messages = "New Product has been saved.";
                    console.log("New Product has been saved = " + JSON.stringify(response));
                    $scope.disableButton = true;
                    $scope.waiting(false);
                }
            },
            function error() {
                $scope.errorMessages = $rootScope.PRODUCT_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    $scope.new = function () {
        $location.path('/product/create' + '/' + $scope.pageno + '/' + $scope.searchName + '/' + $scope.searchAssignee);
    };

    $scope.cancelCreate = function () {
        $location.path('/product/search' + '/' + $scope.pageno + '/' + $scope.searchName + '/' + $scope.searchAssignee);
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
