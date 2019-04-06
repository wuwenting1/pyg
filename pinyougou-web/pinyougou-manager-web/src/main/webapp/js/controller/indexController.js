app.controller('indexController', function($scope, $controller, baseService){
    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});
    $scope.showLoginName = baseService.sendGet("/showLoginName").then(function (response) {
       $scope.showLoginName = response.data.loginName;
   });
});