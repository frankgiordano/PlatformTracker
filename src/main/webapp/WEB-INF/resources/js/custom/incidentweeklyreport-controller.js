app.controller('IncidentWeeklyReportController', function ($rootScope, $scope, IncidentService, IncidentGroupService, helperService, ProductService) {

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

    $scope.submit = function () {

        var emailAddress;

        if ($scope.recipent) {
            emailAddress = {
                "address": $scope.recipent
            };
            $scope.errormessages = null;
        } else {
            $scope.errormessages = "Specify an email recipent.";
            return;
        }

        document.body.style.cursor = "wait";
        IncidentService.generateWeeklyIncidentReport(emailAddress).then(
            function success(response) {
                document.body.style.cursor = "default";
                if (response === "true") {
                    $scope.messages = "Request processed successfully.";
                    $scope.errormessages = null;
                } else {
                    $scope.errormessages = "Failure - Request unsuccessful.";
                    $scope.messages = null;
                }
            },
            function error() {
                document.body.style.cursor = "default";
                $scope.errormessages = "Failure - Request unsuccessful.";
                $scope.messages = null;
            });
    };

    $scope.submitByProduct = function () {

        var incidentReport;
        var products = "";

        if ($scope.recipent) {
            $scope.errormessages = null;
        } else {
            $scope.errormessages = "Specify an email recipent.";
            return;
        }

        if ($scope.selectedProducts) {
            $scope.errormessages = null;
        } else {
            $scope.errormessages = "Specify a Product.";
            return;
        }

        if ($scope.startDate) {
            $scope.errormessages = null;
        } else {
            $scope.errormessages = "Specify a Start Date.";
            return;
        }

        if ($scope.endDate) {
            $scope.errormessages = null;
        } else {
            $scope.errormessages = "Specify a End Date.";
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

        document.body.style.cursor = "wait";
        IncidentService.generateIncidentReportByProduct(incidentReport).then(
            function success(response) {
                document.body.style.cursor = "default";
                if (response === "true") {
                    $scope.messages = "Request processed successfully.";
                    $scope.errormessages = null;
                } else {
                    $scope.errormessages = "Failure - Request unsuccessful.";
                    $scope.messages = null;
                }
            },
            function error() {
                document.body.style.cursor = "default";
                $scope.errormessages = "Failure - Request unsuccessful.";
                $scope.messages = null;
            });
    };

});
