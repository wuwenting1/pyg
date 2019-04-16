/** 定义控制器层 */
app.controller('indexController', function($scope,baseService){
    $scope.showName = function () {
        baseService.sendGet("/user/showName").then(function (response) {
           $scope.loginName = response.data.loginName;
        });
    };
});