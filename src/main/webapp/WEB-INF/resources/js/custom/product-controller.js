app.controller('ProductController', function ($rootScope, $scope, ProductService, platforms, ModalService) {

    $scope.hideduringloading = false;
    $scope.loading = true;

    $scope.init = function () {
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

    $scope.select = function (product) {
        $scope.selectedProduct = product;
        $scope.selectedProduct.startDate = moment($scope.selectedProduct.startDate).format('YYYY-MM-DD');
        if ($scope.selectedProduct.endDate)
            $scope.selectedProduct.endDate = moment($scope.selectedProduct.endDate).format('YYYY-MM-DD');
        $scope.disableButton = false;
    };

    $scope.cancelP = function () {
        $scope.selectedProduct = null;
    };

    $scope.clearMsg = function () {
        $scope.messages = null;
        $scope.errormessages = null;
    };

    clear = function () {
        $scope.incidentName = null;
        $scope.selectedPlatform = $scope.platforms[5];
        $scope.clientName = null;
        $scope.shortName = null;
        $scope.owner = null;
        $scope.startDate = null;
        $scope.endDate = null;
        $scope.maxWeeklyUptime = null;
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
                    $scope.deleteP($scope.selectedProduct.id);
                }
            });
        });
    };

    $scope.refreshData = function () {
        var newData = $scope.init();
        $scope.rowCollection = newData;
    };

    $scope.deleteP = function (id) {
        $scope.clearMsg();

        ProductService.deleteProduct(id).then(
            function success(response) {
                if (response) {
                    $scope.messages = "Product ID " + id + " has been deleted.";
                    console.log("Product has been deleted = " + JSON.stringify(response));
                    $scope.refreshData();
                    $scope.errormessages = null;
                    $scope.disableButton = true;
                }
            },
            function error() {
                $scope.errormessages = "PRODUCT_DELETE_FAILURE - Check logs or invalid Product.";
            });
    };

    $scope.updateInSearch = function () {
        $scope.clearMsg();
        $scope.waiting(true);

        $scope.enforceRequiredFields();

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
                    $scope.refreshData();
                    $scope.waiting(false);
                }
            },
            function error() {
                $scope.errormessages = $rootScope.PRODUCT_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    // just do this for required fields that are not defaulted dropdown fields.
    $scope.enforceRequiredFields = function () {
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
    }

    $scope.submit = function (form) {
        $scope.clearMsg();
        $scope.waiting(true);
        // Trigger validation flag.
        $scope.submitted = true;

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
                $scope.errormessages = $rootScope.PRODUCT_SAVE_ERROR_MSG;
                $scope.waiting(false);
            });
    };

    $scope.waiting = function (value) {
        if (value == true) {
            $scope.hideduringloading = true;
            $scope.loading = false;
            document.body.style.cursor = "wait";
        } else {
            $scope.hideduringloading = false;
            $scope.loading = true;
            document.body.style.cursor = "default";
        }
    };

});