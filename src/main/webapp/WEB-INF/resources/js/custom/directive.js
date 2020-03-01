app.directive('myDatePickerStart', function () {
    return {
        restrict: 'E',
        templateUrl: 'resources/html/templates/dateStart.html',
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelCtrl) {
            element.datetimepicker({
                //language:  'en',
                weekStart: 1,
                todayBtn: 1,
                autoclose: 1,
                todayHighlight: 1,
                startView: 2,
                forceParse: 0,
                showMeridian: 1,
                startDate: '2013-01-01'
            }).on('changeDate', function (e) {
                ngModelCtrl.$setViewValue(moment.utc(e.date).format('MM-DD-YYYY HH:mm'));
                scope.$apply();
            });
        }
    };
})

app.directive('myDatePickerEnd', function () {
    return {
        restrict: 'E',
        templateUrl: 'resources/html/templates/dateEnd.html',
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelCtrl) {
            element.datetimepicker({
                //language:  'en',
                weekStart: 1,
                todayBtn: 1,
                autoclose: 1,
                todayHighlight: 1,
                startView: 2,
                forceParse: 0,
                showMeridian: 1,
                startDate: '2013-01-01'
            }).on('changeDate', function (e) {
                ngModelCtrl.$setViewValue(moment.utc(e.date).format('MM-DD-YYYY HH:mm'));
                scope.$apply();
            });
        }
    };
})

app.directive('myDatePickerTime', function () {
    return {
        restrict: 'E',
        templateUrl: 'resources/html/templates/dateTime.html',
        require: 'ngModel',
        link: function (scope, element, attrs, ngModelCtrl) {
            element.datetimepicker({
                //language:  'en',
                weekStart: 1,
                todayBtn: 1,
                autoclose: 1,
                todayHighlight: 1,
                startView: 2,
                forceParse: 0,
                showMeridian: 1,
                startDate: '2013-01-01'
            }).on('changeDate', function (e) {
                ngModelCtrl.$setViewValue(moment.utc(e.date).format('MM-DD-YYYY HH:mm'));
                scope.$apply();
            });
        }
    };
})

app.directive('bsSelect', function () {

    return {
        restrict: 'E',
        require: '^ngModel',
        scope: {
            items: '=',
            textField: '@',
            valueField: '@',
            ngModel: '='
        },
        template: '<div class="btn-group"><button class="btn button-label btn-info">{{currentItemLabel}}</button><button class="btn btn-info dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>' + '<ul class="dropdown-menu" style="max-height:300px;overflow-y:scroll"><li ng-repeat="item in items" ng-click="cancelClose($event)" ng-class="{red: hover,blue:setCheckboxChecked(item)}" ng-mouseenter="hover = true" ng-mouseleave="hover = false">' +
            '<div class="input-group"><input type="checkbox" ng-checked="setCheckboxChecked(item)"  ng-click="selectVal(item,$index)"> <a href=""  tabindex="-1" > {{item[textField]}}</a></div>' +
            '</li></ul>',
        link: function (scope, element, attrs, ngModelCtrl) {
            //added a watch to update the text of the multiselect
            scope.$watch('ngModel', function (v) {
                scope.setLabel();
            }, true);
            //
            var valueField = scope.valueField.toString().trim();
            var textField = scope.textField.toString().trim();
            var modelIsValid = false;
            var selectedItemIsValid = false;

            scope.checkModelValidity = function (items) {
                if (typeof (items) == "undefined" || !items) return false;
                if (items.length < 1) return false;
                return true;
            };
            modelIsValid = scope.checkModelValidity(scope.ngModel);
            scope.setFormValidity = function () {
                if (typeof (attrs.required) != "undefined") {
                    return modelIsValid; //modelIsValid must be set before we setFormValidity
                }
                return true;
            };
            ngModelCtrl.$setValidity('noItemsSet!', scope.setFormValidity());
            scope.checkSelectedItemValidity = function (item) {
                if (!item) return false;
                if (!item[valueField]) return false;
                if (!item[valueField].toString().trim()) return false;
                return true;
            };

            scope.getItemName = function (item) {
                return item[textField];
            };


            scope.setLabel = function () {
                if (typeof (scope.ngModel) == "undefined" || !scope.ngModel || scope.ngModel.length < 1) {

                    scope.currentItemLabel = attrs.defaultText;

                } else {
                    var allItemsString = '';
                    var selectedItemsCount = scope.ngModel.length;
                    if (selectedItemsCount < 3) {
                        angular.forEach(scope.ngModel, function (item) {
                            allItemsString += item[textField].toString() + ', ';
                        });
                    } else {
                        allItemsString = selectedItemsCount + " selected!";
                    }
                    scope.currentItemLabel = allItemsString;
                }
            };
            scope.setLabel();
            scope.setCheckboxChecked = function (_item) {
                var found = false;
                angular.forEach(scope.ngModel, function (item) {
                    if (!found) {
                        if (_item[valueField].toString() === item[valueField].toString()) {
                            found = true;
                        }
                    }
                });
                return found;
            };
            scope.selectVal = function (_item) {
                var found = false;
                if (typeof (scope.ngModel) != "undefined" && scope.ngModel) {
                    for (var i = 0; i < scope.ngModel.length; i++) {
                        if (!found) {
                            if (_item[valueField].toString() === scope.ngModel[i][valueField].toString()) {
                                found = true;
                                var index = scope.ngModel.indexOf(scope.ngModel[i]);
                                scope.ngModel.splice(index, 1);
                            }
                        }
                    }
                } else {
                    scope.ngModel = [];
                }
                if (!found) {
                    scope.ngModel.push(_item);
                }
                modelIsValid = scope.checkModelValidity(scope.ngModel);
                selectedItemIsValid = scope.checkSelectedItemValidity(_item);
                ngModelCtrl.$setValidity('noItemsSet!', scope.setFormValidity() && selectedItemIsValid);
                scope.setLabel();
                ngModelCtrl.$setViewValue(scope.ngModel);
            };

            scope.cancelClose = function ($event) {
                $event.stopPropagation();
            };
        }
    };
});

app.directive("myQtip", function () {

    return function (scope, element, attrs) {

        /******* This is what's different from myQtip *********/

        var text = attrs['qtipContent'];
        var title = attrs['qtipTitle'];

        /******************************************************/

        scope.qtipSkin = (attrs.skin ? "qtip-" + attrs.skin : "qtip-blue");

        element.qtip({
            content: {
                text: text,
                title: title
            },
            style: {
                classes: scope.qtipSkin + " qtip-rounded qtip-shadow "
            },
            show: {
                event: 'click mouseover',
                solo: true
            },
            hide: {
                event: (scope.closeButton == true ? "false" : "click mouseleave"),
                delay: 300,
                fixed: (($(this).hover || attrs.fixed) ? true : false), //prevent the tooltip from hiding when set to true
                leave: false
            },
            position: {
                viewport: $(window), // Keep it on-screen at all times if possible
                target: (attrs.target ? attrs.target : "event"),
                my: "bottom center",
                at: "top center"
            }
        });

        scope.$on("$destroy", function () {
            $(element).qtip('destroy', true); // Immediately destroy all tooltips belonging to the selected elements
        });

        $('[my-qtip]').css("display", "inline-block");
    }
});