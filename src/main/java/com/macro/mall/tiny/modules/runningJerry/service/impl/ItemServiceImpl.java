package com.macro.mall.tiny.modules.runningJerry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.Item;
import com.macro.mall.tiny.modules.runningJerry.mapper.ItemMapper;
import com.macro.mall.tiny.modules.runningJerry.service.ItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.runningJerry.vo.ItemVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-08-07
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    public CommonResult<List<ItemVo>> queryTree(Item item){
        List<ItemVo> itemVos = initItemVoList();
        QueryWrapper<Item> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Item::getAdminId, item.getAdminId());
        List<Item> list = list(wrapper);
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
