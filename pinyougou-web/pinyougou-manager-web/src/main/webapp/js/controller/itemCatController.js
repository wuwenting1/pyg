/** 定义控制器层 */
app.controller('itemCatController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});
    /** 定义接收父Id*/
    $scope.parentId = 0;
   /** 通过父查询所有*/
   $scope.findItemCatByParentId = function (parentId) {
       $scope.parentId = parentId;
       baseService.sendGet("/itemCat/findItemCatByParentId?parentId=" + parentId).then(function (response) {
           $scope.dataList = response.data;
       });
   };

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        var url = "save";
        if ($scope.itemCat.id){
            url = "update";
        }else {
            $scope.itemCat.parentId = $scope.parentId;
        }
        /** 发送post请求 */
        baseService.sendPost("/itemCat/" + url, $scope.itemCat)
            .then(function(response){
                if (response.data){
                    /** 重新加载数据 */
                    $scope.findItemCatByParentId($scope.parentId);
                    $scope.itemCat = null;
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function(entity){
        /** 把json对象转化成一个新的json对象 */
        $scope.itemCat = entity;
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/itemCat/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.findItemCatByParentId($scope.parentId)
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };
    $scope.grade= 1;
    $scope.selectList = function (entity,grade) {
        $scope.grade = grade;
        if (grade == 1){
            $scope.ItemCat_1 = null;
            $scope.ItemCat_2 = null;
        }
        if (grade == 2){
            $scope.ItemCat_1 = entity;
        }
   if (grade == 3){
       $scope.ItemCat_2 = entity;
   }
        $scope.findItemCatByParentId(entity.id);
    };
    $scope.findTypeTemplateList = function () {
        baseService.sendGet("/typeTemplate/findTypeTemplateList").then(function (response) {
            $scope.typeTemplateList = response.data;
        });
    };
});