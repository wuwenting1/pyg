/** 定义控制器层 */
app.controller('typeTemplateController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/typeTemplate/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        var url = "save";
        if ($scope.entity.id){
            url = "update";
        }
        /** 发送post请求 */
        baseService.sendPost("/typeTemplate/" + url, $scope.entity)
            .then(function(response){
                if (response.data){
                    /** 重新加载数据 */
                    $scope.reload();
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        $scope.entity = JSON.parse(JSON.stringify(entity));

        $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);

        $scope.entity.specIds = JSON.parse($scope.entity.specIds);
        $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/typeTemplate/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };
   // $scope.brandList = {data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};
    $scope.findBrandList = function () {
        baseService.sendGet("/brand/findBrandList").then(function (response) {
            $scope.brandList = {data:response.data};
        });
    };
    $scope.findSpecList = function () {
        baseService.sendGet("/specification/findSpecList").then(function (response) {
            $scope.specList = {data:response.data};
        });
    };

    $scope.addTableRow = function () {
        $scope.entity.customAttributeItems.push({});
    }

    $scope.deleteTableRow = function (idx) {
        $scope.entity.customAttributeItems.splice(idx,1);
    }
    /** 提取数组中json某个属性，返回拼接的字符串(逗号分隔) */
    $scope.jsonArr2Str = function (jsonArrStr,key) {
        // 把jsonArrStr转化成JSON数组对象
        var jsonArr = JSON.parse(jsonArrStr);
        // 定义新数组
       var res = [];
       // 迭代json数组
      for (var i = 0;i < jsonArr.length;i++){
      // 取数组中的一个元素
      var json = jsonArr[i];
      // 把json对象的值添加到新数组
      res.push(json[key]);
      }
       // 返回数组中的元素用逗号分隔的字符串
        return res.join(',')
    };
});