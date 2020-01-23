app.controller('ProductController', function($http, $rootScope, $filter, $scope, ProductService, ngTableParams, platforms, ModalService) {
    $scope.init = function() {
        ProductService.getProducts().then(
            function success(response) {
                $scope.products = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "GROUPS_GET_FAILURE",
                    message: "Oooooops something went wrong, please try again"
                });
            });
    };

    // defaults
    $scope.platforms = platforms;
    $scope.selectedPlatform = $scope.platforms[5];
    $scope.disableButton = false;
    
    $scope.select = function(product) {
        $scope.selectedProduct = product;
        $scope.disableButton = false;
        
    };
    
    $scope.cancelP = function () {
    	$scope.selectedProduct = null;    	
    };
    
    $scope.clearMsg = function() {
        $scope.messages = null;
        $scope.errormessages = null;
    };
    
    clear = function() {
        $scope.incidentName = null;
        $scope.selectedPlatform = $scope.platforms[5];
        $scope.clientName = null;
        $scope.shortName = null;
        $scope.owner = null;
        $scope.startTime = null;
        $scope.endTime = null;
        $scope.maxWeeklyUptime = null;
    };
    
    $scope.showOnDelete = function() {
        ModalService.showModal({
            templateUrl: 'modal.html',
            controller: "ModalController"
        }).then(function(modal) {
            modal.element.modal();
            modal.close.then(function(result) {
                if (result == 'Yes') {
                    $scope.deleteP($scope.selectedProduct.id);
                }
                $scope.message = "You said " + result;
            });
        });
    };
    
    $scope.refreshData = function() {
        var newData = $scope.init();
        $scope.rowCollection = newData;
    };
    
    $scope.deleteP = function(id) {
        $scope.clearMsg();
        ProductService.deleteProduct(id).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Product " + id + " has been deleted.";
                    console.info("Product " + id + " has been deleted.");
                    $scope.refreshData();
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                } else {
                    $scope.errormessages = "Delete operation failure, check logs or invalid product.";
                    console.error("Product " + id + " was unable to be deleted.")
                }
            },
            function error() {
                $scope.errormessages = "Delete operation failure, check logs or invalid product.";
                //              $rootScope.errors.push({ code: "INCIDENT_DELETE_FAILURE", message: "Delete operation failed, check logs or invalid incident." });
            });
    };
    
    $scope.updateInSearch = function() {
        $scope.clearMsg();
        
        var	revenueValue = $scope.selectedProduct.revenue; 
        var usersValue = $scope.selectedProduct.users;
        
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
            "revenue":  $scope.selectedProduct.revenue,
        	"users": $scope.selectedProduct.users
        };

        console.log(product);
        console.log(JSON.stringify(product));
        ProductService.saveProduct(product).then(
                function success(response) {
                    if (response) {
                        $scope.messages = "Product with incident name " + product.incidentName + " has been saved.";
                        console.info("Product with incident name " + product.incidentName + " has been saved.");
                        clear();
                    } else {
                        console.error("Product with incident name " + product.incidentName + " was unable to be saved.")
                    }
                },
                function error() {
                    $rootScope.errors.push({
                        code: "PRODUCT_SAVE_FAILURE",
                        message: "Save operation failure, make sure the following required fields are filled: Platform, Incident Name, Client Name, Short Name (10 character field), Start Date and Max Weekly Uptime, please try again"
                    });
                });

    };

    $scope.submit = function(form) {
        // Trigger validation flag.
        $scope.submitted = true;

        // If form is invalid, return and let AngularJS show validation errors.
        //    if (form.$invalid) { alert("invalid");
        //  	  return;
        //    }

        var	revenueValue = $scope.revenue; 
        var usersValue = $scope.users;
        
        var product = {
            "incidentName": $scope.incidentName,
            "platform": $scope.selectedPlatform.value,
            "clientName": $scope.clientName,
            "shortName": $scope.shortName,
            "owner": $scope.owner,
            "startDate": $scope.startDate,
            "endDate": $scope.endDate,
            "maxWeeklyUptime": $scope.maxWeeklyUptime,
            "revenue": $scope.revenue,
        	"users": $scope.users
        };
 
        console.log(JSON.stringify(product));
        ProductService.saveProduct(product).then(
            function success(response) {
                if (response) {
                    $scope.messages = "New product with incident name " + product.incidentName + " has been saved.";
                    console.info("New product with incident name " + product.incidentName + " has been saved.");
                    clear();
                } else {
                    console.error("New Product with incident name " + product.incidentName + " was unable to be saved.")
                }
            },
            function error() {
                $scope.messages = "Save operation failure, make sure the following required fields are filled: Platform, Incident Name, Client Name, Short Name (10 character field), Start Date and Max Weekly Uptime, please try again";
            });
    };
});