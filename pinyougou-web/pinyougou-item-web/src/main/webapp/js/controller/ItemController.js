/** 定义控制器层 */
app.controller('itemController', function ($scope) {
    $scope.addNum = function (x) {
        $scope.num = parseInt($scope.num);
        $scope.num += x;
        if ($scope.num < 1){
            $scope.num = 1;
        }
    };
    /** 记录用户选择的规格选项 */
    $scope.specItems = {};
    /** 定义用户选择规格选项的方法 */
    $scope.selectSpec = function (attributeName,attributeValue) {
        $scope.specItems[attributeName] = attributeValue;
        searchSku();
    };
    /** 判断某个规格选项是否被选中 */
    $scope.isSelected = function (attributeName,attributeValue) {
       return $scope.specItems[attributeName] == attributeValue;
    };
    $scope.loadSku = function () {
        /** 取第一个SKU */
        $scope.sku = itemList[0];
        //获取SKU商品选择的选项规格
        $scope.specItems = JSON.parse($scope.sku.spec);
    };
    /** 根据用户选中的规格选项，查找对应的SKU商品 */
    var searchSku = function () {
        /** 判断规格选项是不是当前用户选中的 */
        for(var i = 1; i < itemList.length; i++) {
            if (itemList[i].spec == JSON.stringify($scope.specItems)){
                $scope.sku = itemList[i];
                return;
            }
         }
    };
    $scope.addToCart = function () {
        alert("sku的id:" + $scope.sku.id);
    }
});