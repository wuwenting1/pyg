/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/goods/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function () {
        /** 获取富文本编辑器的内容 */
        //alert(JSON.stringify(editor.html()));
        $scope.goods.goodsDesc.introduction = editor.html();
        var url = "save";
        /** 发送post请求 */
        baseService.sendPost("/goods/" + url, $scope.goods)
            .then(function (response) {
                if (response.data) {
                    /** 保存成功 */
                    alert("保存成功!!")
                    $scope.goods = {};
                    /** 清空富文本编辑器 */
                    editor.html('');
                } else {
                    alert("操作失败！");
                }
            });
    };
    $scope.uploadFile = function () {
         baseService.uploadFile().then(function (response) {
             if (response.data.status == 200){
               /**设置图片访问地址 */
               $scope.picEntity.url = response.data.url;
             }else {
                 alert("上传失败!!!")
             }
        });
    };

    /** 定义数据存储结构 */
    $scope.goods = { goodsDesc : { itemImages : [],specificationItems :[]}};
    /** 添加图片到数组 */
    $scope.addPic = function(){
        //alert($scope.picEntity);
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    };
    /** 数组中移除图片 */
    $scope.removePic = function(index){
        $scope.goods.goodsDesc.itemImages.splice(index, 1);
    };
    /** 定义修改规格选项方法 */
    $scope.updateSpecAttr = function ($event,specName,optionName) {
        /** 根据json对象的key到json数组中搜索该key值对应的对象 */
        var json = $scope.findJsonByKey($scope.goods.goodsDesc.specificationItems,specName);
        /** 判断对象是否为空 */
        if (json) {
            /** 判断checkbox是否选中 */
           if ($event.target.checked) {
                /** 添加该规格选项到数组中*/
                json.attributeValue.push(optionName);
            } else {
                /** 取消勾选，从数组中删除该规格选项 */
                json.attributeValue.splice(json.attributeValue.indexOf(optionName), 1);
                /** 如果选项都取消了，将此条记录删除*/
                if (json.attributeValue.length == 0) {
                    $scope.goods.goodsDesc.specificationItems.splice($scope.goods.goodsDesc.specificationItems.indexOf(json),1);
                }
            }
        }else {
            /** 如果为空，则新增数组元素 */
            $scope.goods.goodsDesc.specificationItems.push({attributeValue:[optionName],attributeName:specName});
        }
};
    /** 从json数组中根据key查询指定的json对象 */
    $scope.findJsonByKey = function (jsonArr,specName) {
        /** 迭代json数组 */
        for(var i = 0; i < jsonArr.length; i++){
            var json = jsonArr[i];
            if(json.attributeName == specName){
                return json;
            }
        }
        return null;
    };

       /** 创建SKU商品方法 */
       $scope.createItems = function () {
           /** 定义SKU数组变量，并初始化 */
           $scope.goods.items = [{ spec:{}, price: 0, num: 9999, status: '0', isDefault: '0'}];
           /** 定义选中的规格选项数组 */
          // alert($scope.goods.items);
           var specItems = $scope.goods.goodsDesc.specificationItems;
           //alert(specItems);
           /** 扩充原SKU数组方法 */
           for(var i = 0; i < specItems.length; i++ ){
               var json = specItems[i];
              // alert(json.attributeName);
               $scope.goods.items = swapItems($scope.goods.items,json.attributeName,json.attributeValue);
              // alert(goods.items);
           }
       };
    /** 扩充SKU数组方法 */
      var swapItems = function (items,attributeName,attributeValue) {
          /** 创建新的SKU数组 */
          //alert(attributeName);
          var newItems = [];
          /** 迭代旧的SKU数组，循环扩充 */
          for (var i = 0;i < items.length ;i++){
              /** 获取一个SKU商品 */
              var item = items[i];
              /** 迭代规格选项值数组 */
              for(var j = 0; j < attributeValue.length; j++){
                  var newItem = JSON.parse(JSON.stringify(item));
                  /** 增加新的key与value */
                  newItem.spec[attributeName] = attributeValue[j];
                  /** 添加到新的SKU数组 */
                  newItems.push(newItem);
              }
          }
          return newItems;
      };

    /** 查询一级分类*/
    $scope.findItemCatByParentId = function (parentId,name) {
           baseService.sendGet("/itemCat/findItemCatByParentId?parentId=" +  parentId).
           then(function (response) {
               $scope[name] = response.data;
           });
    };
    /** 监控 goods.category1Id 变量，查询二级分类 */
    $scope.$watch('goods.category1Id',function (newVal,oldVal) {
        /** 根据选择的值查询二级分类 */
        if (newVal){
            $scope.findItemCatByParentId(newVal,'itemCatList2');
        }else {
            $scope.itemCatList2 =[];
        }
    });
    /** 监控 goods.category2Id 变量，查询三级分类 */
    $scope.$watch('goods.category2Id',function (newVal,oldVal) {
        /** 根据选择的值查询三级分类 */
        if (newVal){
            $scope.findItemCatByParentId(newVal,'itemCatList3');
        }else {
            $scope.itemCatList3 =[];
        }
    });
    /** 监控 goods.category3Id 变量，查询模板ID*/
    $scope.$watch('goods.category3Id',function (newVal,oldVal) {
        /** 根据选择的值查询模板ID */
        if (newVal){
            for (i = 0;i < $scope.itemCatList3.length ; i++){
                // 取一个数组元素 {}
                var itemCat = $scope.itemCatList3[i];
                if (itemCat.id == newVal){
                    $scope.goods.typeTemplateId = itemCat.typeId;
                }
            }
        }else {
            $scope.goods.typeTemplateId = null;
        }
    });
    /** 监控 goods.typeTemplateId 变量，查询品牌 */
    $scope.$watch('goods.typeTemplateId',function (newVal,oldVal) {
        /** 根据选择的值查询二级分类 */
        if (newVal){
            baseService.sendGet("/typeTemplate/findOne?id=" + newVal).then(function (response) {
                $scope.brandIds = JSON.parse(response.data.brandIds);
                $scope.goods.goodsDesc.customAttributeItems = JSON.parse(response.data.customAttributeItems);
            });

            baseService.sendGet("/typeTemplate/findSpecByTemplateId?id=" + newVal).then(function (response) {
                $scope.specList = response.data;
            });
        }else {
            $scope.brandIds = [];
            $scope.goods.goodsDesc.customAttributeItems = [];
        }
    });

    $scope.updateMarketable = function (status) {
        baseService.sendGet("/goods/updateMarketable?status=" + status + "&ids=" +$scope.ids).then(function (response) {
            if(response.data){
                $scope.reload();
                $scope.ids= [];
            }else {
                alert("请选择商品！")
            }
        });
    };
    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.reload();
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };

    $scope.status = ['未审核','审核通过','已驳回'];
});