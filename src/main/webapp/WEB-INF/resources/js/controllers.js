'use strict';

/* Controllers */

angular.module('myApp.controllers', []).
  controller('UsersCtrl', ['$scope', '$http', function($scope, $http) {

        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: true
        };
        $scope.totalServerItems = 0;
        $scope.pagingOptions = {
            pageSizes: [3, 5, 10, 25, 50],
            pageSize: 3,
            currentPage: 1
        };


        $scope.setPagingData = function(data, page, pageSize){
//            var pagedData = data.slice((page - 1) * pageSize, page * pageSize);
            $scope.myData = data.content;
            $scope.totalServerItems = data.page.totalElements;
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        };


        $scope.getPagedDataAsync = function (pageSize, page, searchText) {
            setTimeout(function () {
                var data;
                if (searchText) {
                    var ft = searchText.toLowerCase();
                    $http({method: 'GET', url: '/users', params: {'limit': $scope.pagingOptions.pageSize, 'page': $scope.pagingOptions.currentPage}})
                        .success(function (largeLoad) {
                        data = largeLoad.filter(function(item) {
                            return JSON.stringify(item).toLowerCase().indexOf(ft) != -1;
                        });
                        $scope.setPagingData(data,page,pageSize);
                    });
                } else {
                    $http({method: 'GET', url: '/users', params: {'limit': $scope.pagingOptions.pageSize, 'page': $scope.pagingOptions.currentPage}})
                        .success(function (largeLoad) {
                        $scope.setPagingData(largeLoad,page,pageSize);
                    });
                }
            }, 100);
        };

        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);

        $scope.$watch('pagingOptions', function (newVal, oldVal) {
            if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
            }
        }, true);

        $scope.$watch('pagingOptions.pageSize', function (newVal, oldVal) {
            if (newVal !== oldVal) {
                $scope.pagingOptions.currentPage = 1;// if pageSize changes - start from first page
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
            }
        }, true);

        $scope.$watch('filterOptions', function (newVal, oldVal) {
            if (newVal !== oldVal) {
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
            }
        }, true);


        $scope.myColumnDefs = [{ field: 'firstName', displayName: 'First Name'},
            { field: 'lastName', displayName: 'Last Name' }];


        $scope.gridOptions = {
            data: 'myData',
            columnDefs: $scope.myColumnDefs,
            multiSelect: false,
            selectedItems: [],
            enableSorting: false, // filter and sort should be on the entire data set (server side)
            enableHighlighting: true, // enable copy of text from grid cells
            enablePaging: true,
            showFooter: true,
            totalServerItems: 'totalServerItems',
            pagingOptions: $scope.pagingOptions,
            filterOptions: $scope.filterOptions
        };



  }]);