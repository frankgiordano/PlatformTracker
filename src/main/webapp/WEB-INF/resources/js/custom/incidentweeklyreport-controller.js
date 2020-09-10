app.controller('IncidentWeeklyReportController', function ($rootScope, $scope, localStorageService, IncidentService, helperService, ProductService) {
    
    localStorageService.remove("incidentCreateButtonClicked");
    localStorageService.remove("incidentEditMode");
    $scope.hideDuringLoading = false;

    // getProducts() gets all the products even those that are not active.. this returns data back unsorted and
    // we use helperService function to sort.
    (function () {
        ProductService.getProducts().then(
            function success(response) {
                response = helperService.sortByKey(response, 'shortName');
                $scope.myProducts = response;
            },
            function error() {
                $rootScope.errors.push({
                    code: "PRODUCTS_GET_FAILURE",
                    message: "Error retrieving products."
                });
            });
    }());

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

    $scope.submit = function () {

        var emailAddress;

        if ($scope.recipent) {
            emailAddress = {
                "address": $scope.recipent
            };
            $scope.errorMessages = null;
        } else {
            $scope.errorMessages = "Specify an email recipent.";
            return;
        }

        $scope.waiting(true);
        IncidentService.generateWeeklyIncidentReport(emailAddress).then(
            function success(response) {
                if (response === "true") {
                    $scope.messages = "Request processed successfully.";
                    $scope.errorMessages = null;
                } else {
                    $scope.errorMessages = "Failure - Request unsuccessful.";
                    $scope.messages = null;
                }
                $scope.waiting(false);
            },
            function error() {
                $scope.waiting(false);
                $scope.errorMessages = "Failure - Request unsuccessful.";
                $scope.messages = null;
            });
    };

    $scope.submitByProduct = function () {

        var incidentReport;
        var products = "";

        if ($scope.recipent) {
            $scope.errorMessages = null;
        } else {
            $scope.errorMessages = "Specify an email recipent.";
            return;
        }

        if ($scope.selectedProducts) {
            $scope.errorMessages = null;
        } else {
            $scope.errorMessages = "Specify a Product.";
            return;
        }

        if ($scope.startDate) {
            $scope.errorMessages = null;
        } else {
            $scope.errorMessages = "Specify a Start Date.";
            return;
        }

        if ($scope.endDate) {
            $scope.errorMessages = null;
        } else {
            $scope.errorMessages = "Specify a End Date.";
            return;
        }

        for (var i = 0; i < $scope.selectedProducts.length; i++) {
            products += $scope.selectedProducts[i].incidentName + ",";
        }

        incidentReport = {
            "products": products,
            "address": $scope.recipent,
            "startDate": $scope.startDate,
            "endDate": $scope.endDate
        }

        $scope.waiting(true);
        IncidentService.generateIncidentReportByProduct(incidentReport).then(
            function success(response) {
                if (response === "true") {
                    $scope.messages = "Request processed successfully.";
                    $scope.errorMessages = null;
                } else {
                    $scope.errorMessages = "Failure - Request unsuccessful.";
                    $scope.messages = null;
                }
                $scope.waiting(false);
            },
            function error() {
                $scope.waiting(false);
                $scope.errorMessages = "Failure - Request unsuccessful.";
                $scope.messages = null;
            });
    };

});
