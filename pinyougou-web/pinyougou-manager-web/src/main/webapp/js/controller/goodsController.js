/** 定义控制器层 */
app.controller('goodsController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/goods/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.updateStatus = function(status){
        baseService.sendGet("/goods/updateStatus?ids=" + $scope.ids + "&status=" + status)
            .then(function(response){
                if (response.data){
                    /** 重新加载数据 */
                    $scope.reload();
                    //清空ids
                    $scope.ids = [];
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.sendGet("/goods/delete?ids="+ $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.reload();
                        $scope.ids = [];
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };
});