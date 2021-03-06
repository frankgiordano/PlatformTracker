app.filter('percentage', ['$filter', function ($filter) {
    return function (input, decimals) {
        return $filter('number')(input, decimals) + '%';
    };
}]);

app.filter('addEllipsis', function () {
    return function (input) {
        if (input && input.length > 50) {
            return input.substring(0, 50) + '...';
        } else return input;
    }
});

app.filter('closed', function () {
    return function (input) {
        if (input == "Close") {
            return "Closed";
        } else return input;
    }
});

app.filter('dateFormat', function () {
    return function (input) {
        if (input === null) {
            return "";
        }
        return moment(input).format("MM-DD-YYYY HH:mm");
    }
});

app.filter('dateFormatMinusTime', function () {
    return function (input) {
        if (input === null || input === undefined) {
            return null;
        }
        return moment(input).format("MM-DD-YYYY");
    }
});