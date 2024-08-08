package com.macro.mall.tiny.modules.runningJerry.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.Item;
import com.macro.mall.tiny.modules.runningJerry.mapper.ItemMapper;
import com.macro.mall.tiny.modules.runningJerry.service.ItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.runningJerry.vo.ChartYVo;
import com.macro.mall.tiny.modules.runningJerry.vo.EchartsInVo;
import com.macro.mall.tiny.modules.runningJerry.vo.EchartVo;
import com.macro.mall.tiny.modules.runningJerry.vo.ItemVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-08-07
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    public CommonResult<List<ItemVo>> queryTree(Item item) {
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
     * 查询图表
     *
     * @param inVo inVo
     * @return {@link CommonResult}<{@link EchartVo}>
     */
    public CommonResult<List<EchartVo>> queryEcharts(EchartsInVo inVo) {
//        List<EchartVo> echartVos = initEchartVoList(inVo);
        List<EchartVo> echartVos = new ArrayList<>();
        //月
        if (inVo.getPeriod().equals(1)) {
            QueryWrapper<Item> wrapper = new QueryWrapper<>();

            DateTime parse = DateUtil.parse(inVo.getYear() + "-01-01");
            wrapper.lambda()
                    .eq(Item::getAdminId, inVo.getAdminId())
                    .eq(Item::getIfDelete, 0).between(Item::getDate,
                            DateUtil.beginOfYear(parse),
                            DateUtil.endOfYear(parse));
            List<Item> list = list(wrapper);
            //按月分组
            if (!CollectionUtils.isEmpty(list)) {
                List<String> months = IntStream.rangeClosed(1, 12)
                        .mapToObj(i -> i + "月")
                        .collect(Collectors.toList());

                //收入
                Map<Date, Double> income = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(1))
                        .collect(Collectors.groupingBy(Item::getDate,
                                Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
                ChartYVo incomeChartYVo = new ChartYVo();
                incomeChartYVo.setLineName("收入");
                List<String> incomeValueStrings = income.values().stream()
                        .map(String::valueOf).collect(Collectors.toList());
                incomeChartYVo.setYList(incomeValueStrings);
                //支出
                Map<Date, Double> expend = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(2))
                        .collect(Collectors.groupingBy(Item::getDate,
                                Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
                ChartYVo expendChartYVo = new ChartYVo();
                expendChartYVo.setLineName("支出");
                List<String> expendValueStrings = expend.values().stream()
                        .map(String::valueOf).collect(Collectors.toList());
                expendChartYVo.setYList(expendValueStrings);
                List<ChartYVo> chartYVos = new ArrayList<>();
                chartYVos.add(incomeChartYVo);
                chartYVos.add(expendChartYVo);
                echartVos.add(new EchartVo("收入支出表", months,chartYVos));


                //资产
                Map<Date, Double> property = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(3))
                        .collect(Collectors.groupingBy(Item::getDate,
                                Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
                ChartYVo propertyChartYVo = new ChartYVo();
                propertyChartYVo.setLineName("资产");
                List<String> propertyValueStrings = property.values().stream()
                        .map(String::valueOf).collect(Collectors.toList());
                propertyChartYVo.setYList(propertyValueStrings);
                //负债
                Map<Date, Double> debt = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(4))
                        .collect(Collectors.groupingBy(Item::getDate,
                                Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
                ChartYVo debtChartYVo = new ChartYVo();
                debtChartYVo.setLineName("负债");
                List<String> debtValueStrings = debt.values().stream()
                        .map(String::valueOf).collect(Collectors.toList());
                debtChartYVo.setYList(debtValueStrings);
                List<ChartYVo> debtChartYVos = new ArrayList<>();
                debtChartYVos.add(propertyChartYVo);
                debtChartYVos.add(debtChartYVo);
                echartVos.add(new EchartVo("资产负债表", months,debtChartYVos));


                echartVos.add(new EchartVo("资产收益表", months, new ArrayList<>(1)));
                echartVos.add(new EchartVo("资产二阶导表", months, new ArrayList<>(1)));

            }
        }
        //年
        if (inVo.getPeriod().equals(2)) {

        }
        return CommonResult.success(echartVos);
    }

    /**
     * initechartVolist
     *
     * @return {@link List}<{@link EchartVo}>
     */
    private List<EchartVo> initEchartVoList(EchartsInVo inVo) {
        List<EchartVo> echartVos = new ArrayList<>();
        if (inVo.getPeriod().equals(1)) {
            List<String> months = IntStream.rangeClosed(1, 12)
                    .mapToObj(i -> i + "月")
                    .collect(Collectors.toList());
            echartVos.add(new EchartVo("收入支出表", months, new ArrayList<>(1)));
            echartVos.add(new EchartVo("资产负债表", months, new ArrayList<>(1)));
            echartVos.add(new EchartVo("资产收益表", months, new ArrayList<>(1)));
            echartVos.add(new EchartVo("资产二阶导表", months, new ArrayList<>(1)));
        }
        return echartVos;
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
