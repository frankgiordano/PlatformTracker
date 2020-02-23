app.controller('ProductController', function($http, $rootScope, $filter, $scope, ProductService, ngTableParams, platforms, ModalService) {
    $scope.init = function() {
        ProductService.getProducts().then(
            function success(response) {
                $scope.products = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "PRODUCTS_GET_FAILURE",
                    message: "Error retrieving products."
                });
            });
    };

    // defaults
    $scope.platforms = platforms;
    $scope.selectedPlatform = $scope.platforms[5];
    $scope.disableButton = false;
    
    $scope.select = function(product) {
        $scope.selectedProduct = product;
        $scope.selectedProduct.startDate = moment($scope.selectedProduct.startDate).format('YYYY-MM-DD'); 
        if ($scope.selectedProduct.endDate)
            $scope.selectedProduct.endDate = moment($scope.selectedProduct.endDate).format('YYYY-MM-DD'); 
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
        $scope.startDate = null;
        $scope.endDate = null;
        $scope.maxWeeklyUptime = null;
    };

    $scope.showOnDelete = function() {
        var title = "Product";
        var name = "Product Detail ID " + $scope.selectedProduct.id;

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
                    $scope.deleteP($scope.selectedProduct.id);
                }
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
                    $scope.messages = "Product ID " + id + " has been deleted.";
                    console.info("Product ID " + id + " has been deleted.");
                    $scope.refreshData();
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                } else {
                    $scope.errormessages = "PRODUCT_DELETE_FAILURE - Check logs or invalid Product.";
                    console.error("Product ID " + id + " was unable to be deleted.");
                }
            },
            function error() {
                $scope.errormessages =  "PRODUCT_DELETE_FAILURE - Check logs or invalid Product.";
                // $rootScope.errors.push({ code: "PRODUCT_DELETE_FAILURE", message: "Check logs or invalid Product." });
            });
    };
    
    $scope.updateInSearch = function() {
        $scope.clearMsg();
        
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
                        $scope.messages = "Product ID " + product.id + " has been saved.";
                        console.info("Product with Incident Name " + product.incidentName + " has been saved.");
                        $scope.disableButton = true;
                        $scope.refreshData();
                    } else {
                        console.error("Product with Incident Name " + product.incidentName + " was unable to be saved.")
                    }
                },
                function error() {
                    $scope.errormessages = $rootScope.PRODUCT_SAVE_ERROR_MSG;
                    // $rootScope.errors.push({
                    //     code: "PRODUCT_SAVE_FAILURE",
                    //     message: $rootScope.REQUIRED_FIELDS_PRODUCT_MSG
                    // });
                });
    };

    $scope.submit = function(form) {
        $scope.clearMsg();
        // Trigger validation flag.
        $scope.submitted = true;

        // If form is invalid, return and let AngularJS show validation errors.
        //    if (form.$invalid) { alert("invalid");
        //  	  return;
        //    }

        var platform;

        if ($scope.selectedPlatform == null || $scope.selectedPlatform == undefined) {
            platform = null;
        } else {
            platform = $scope.selectedPlatform.value;
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
 
        console.log(JSON.stringify(product));
        ProductService.saveProduct(product).then(
            function success(response) {
                if (response) {
                    $scope.messages = "New Product has been saved.";
                    console.info("New Product with incident name " + product.incidentName + " has been saved.");
                    $scope.disableButton = true;
                } else {
                    console.error("New Product with incident name " + product.incidentName + " was unable to be saved.")
                }
            },
            function error() {
                $scope.errormessages = $rootScope.PRODUCT_SAVE_ERROR_MSG;
            });
    };
});