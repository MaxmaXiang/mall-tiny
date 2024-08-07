package com.macro.mall.tiny.modules.runningJerry.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.Item;
import com.macro.mall.tiny.modules.runningJerry.service.ItemService;
import com.macro.mall.tiny.modules.runningJerry.vo.ItemVo;
import com.macro.mall.tiny.modules.ums.dto.UmsAdminParam;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author macro
 * @since 2024-08-07
 */
@RestController
@RequestMapping("/runningJerry/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Item> insert(@Validated @RequestBody Item item) {
        item.setCreateTime(new Date());
        item.setIfDelete(0);
        boolean save = itemService.save(item);
        return CommonResult.success(item);
    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Item> update(@Validated @RequestBody Item item) {
        item.setUpdateTime(new Date());
        boolean save = itemService.updateById(item);
        return CommonResult.success(item);
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Item> delete(@Validated @RequestBody Item item) {
        item.setCreateTime(new Date());
        item.setIfDelete(1);
        boolean save = itemService.updateById(item);
        return CommonResult.success(item);
    }

    @ApiOperation(value = "查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<ItemVo>> query(@Validated @RequestBody Item item) {

        List<ItemVo> itemVos = initItemVoList();
        QueryWrapper<Item> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Item::getAdminId, item.getAdminId());
        List<Item> list = itemService.list(wrapper);
        if (!CollectionUtils.isEmpty(list)) {
            Map<Integer, List<Item>> collect = list.stream().collect(Collectors.groupingBy(Item::getItemType));
            itemVos.forEach(itemVo -> {
                List<Item> items = collect.get(itemVo.getItemType());
                if (!CollectionUtils.isEmpty(items)) {
                    itemVo.setItemList(collectTree(items));
                }
            });
        }

        return CommonResult.success(itemVos);
    }

    /**
     * @param items 项list
     * @return {@link List}<{@link Item}>
     */
    private static List<Item> collectTree(List<Item> items) {
        Map<Integer, Item> itemMap = new HashMap<>();
        List<Item> roots = new ArrayList<>();

        // Create a map of items by their id
        for (Item item : items) {
            itemMap.put(item.getId(), item);
        }

        // Build the tree
        for (Item item : items) {
            Integer parentId = item.getParentId();
            if (parentId == null || !itemMap.containsKey(parentId)) {
                // No parent, so it's a root item
                roots.add(item);
            } else {
                // Add to parent's children
                Item parent = itemMap.get(parentId);
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(item);
            }
        }

        return roots;
    }

    /**
     * inititemVolist
     *
     * @return {@link List}<{@link ItemVo}>
     */
    private List<ItemVo> initItemVoList() {
        List<ItemVo> itemVos = new ArrayList<>();
        itemVos.add(new ItemVo("收入", 1));
        itemVos.add(new ItemVo("支出", 2));
        itemVos.add(new ItemVo("资产", 3));
        itemVos.add(new ItemVo("负债", 4));
        return itemVos;
    }
}

