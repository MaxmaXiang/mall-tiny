package com.macro.mall.tiny.modules.runningJerry.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.mapper.ItemTagRelationMapper;
import com.macro.mall.tiny.modules.runningJerry.model.Item;
import com.macro.mall.tiny.modules.runningJerry.mapper.ItemMapper;
import com.macro.mall.tiny.modules.runningJerry.model.ItemRecord;
import com.macro.mall.tiny.modules.runningJerry.model.ItemTagRelation;
import com.macro.mall.tiny.modules.runningJerry.service.ItemRecordService;
import com.macro.mall.tiny.modules.runningJerry.service.ItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.runningJerry.service.ItemTagRelationService;
import com.macro.mall.tiny.modules.runningJerry.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private ItemRecordService itemRecordService;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemTagRelationServiceImpl itemTagRelationService;

    public CommonResult<List<ItemVo>> queryTree(Item item) {
        List<ItemVo> itemVos = initItemVoList();
        List<Item> list = itemMapper.listRecord(item);

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
    public CommonResult<List<ChartVo>> queryEcharts(EchartsInVo inVo) {
        List<ChartVo> echartVos = new ArrayList<>(1);
        //月
        if (inVo.getPeriod().equals(1)) {
            DateTime parse = DateUtil.parse(inVo.getYear() + "-01-01");
            inVo.setStartDate(DateUtil.beginOfYear(parse));
            inVo.setEndDate(DateUtil.endOfYear(parse));
            List<Item> list = itemMapper.listRecordEchartsNew(inVo);

            //按月分组
            echartVos = IntStream.rangeClosed(1, 12)
                    .mapToObj(i -> {
                        ChartVo chartVo = new ChartVo();
                        chartVo.setDateStr(String.valueOf(i));
                        chartVo.setDate(DateUtil.parse(inVo.getYear() + "-" + chartVo.getDateStr() + "-01"));
                        calOneDate(list, chartVo);
                        return chartVo;
                    })
                    .collect(Collectors.toList());

            //计算收益变化
            calPropertySecondOrderAdmittance(echartVos);

            return CommonResult.success(echartVos);

//            calculateCharts(list, months, echartVos, inVo, 1);

        }
        //年
        if (inVo.getPeriod().equals(2)) {
            DateTime dateTime = new DateTime();
            inVo.setStartDate(DateUtil.beginOfYear(DateUtil.offset(dateTime, DateField.YEAR, -12)));
            inVo.setEndDate(DateUtil.endOfYear(dateTime));
            List<Item> list = itemMapper.listRecordEchartsNew(inVo);
            list.forEach(item -> item.setDate(DateUtil.beginOfYear(item.getDate())));

            echartVos = IntStream.rangeClosed(DateUtil.year(inVo.getStartDate()), DateUtil.year(inVo.getEndDate()))
                    .mapToObj(i -> {
                        ChartVo chartVo = new ChartVo();
                        chartVo.setDateStr(String.valueOf(i));
                        chartVo.setDate(DateUtil.parse(chartVo.getDateStr() + "-01-01"));
                        calOneDate(list, chartVo);
                        return chartVo;
                    })
                    .collect(Collectors.toList());

            //计算收益变化
            calPropertySecondOrderAdmittance(echartVos);

//            calculateCharts(list, months, echartVos, inVo, 2);
            return CommonResult.success(echartVos);
        }
        return CommonResult.success(echartVos);
    }

    private void calPropertySecondOrderAdmittance(List<ChartVo> echartVos) {
        for (int i = 0; i < echartVos.size(); i++) {
            double previous = (i == 0) ? echartVos.get(i).getProfit() : echartVos.get(i - 1).getProfit();
            echartVos.get(i).setPropertySecondOrderAdmittance(echartVos.get(i).getProfit()-previous);
        }
    }

    private void calOneDate(List<Item> list, ChartVo chartVo) {
        List<Item> collect = list.stream().filter(item -> item.getDate().equals(chartVo.getDate())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(collect)) {
            double sum1 = collect.stream().filter(item -> item.getItemType().equals(1)).map(Item::getValue).mapToDouble(Double::valueOf).sum();
            chartVo.setIncome(sum1);

            double sum2 = collect.stream().filter(item -> item.getItemType().equals(2)).map(Item::getValue).mapToDouble(Double::valueOf).sum();
            chartVo.setExpend(sum2);

            chartVo.setProfit(chartVo.getIncome()-chartVo.getExpend());

            double sum3 = collect.stream().filter(item -> item.getItemType().equals(3)).map(Item::getValue).mapToDouble(Double::valueOf).sum();
            chartVo.setProperty(sum3);

            double sum4 = collect.stream().filter(item -> item.getItemType().equals(4)).map(Item::getValue).mapToDouble(Double::valueOf).sum();
            chartVo.setDebt(sum4);
        }
    }

    private void calculateCharts(List<Item> list, List<String> months, List<EchartVo> echartVos, EchartsInVo inVo, Integer level) {

        //收入
        Map<Date, Double> income = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(1)
                        && StringUtils.isNotEmpty(i.getValue()))
                .collect(Collectors.groupingBy(Item::getDate,
                        Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
        ChartYVo incomeChartYVo = new ChartYVo();
        incomeChartYVo.setLineName("收入");
        List<String> incomeValueStrings = months.stream().map(month -> getYByX(month, level, income, inVo)).collect(Collectors.toList());

        incomeChartYVo.setYList(incomeValueStrings);
        //支出
        Map<Date, Double> expend = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(2)
                        && StringUtils.isNotEmpty(i.getValue()))
                .collect(Collectors.groupingBy(Item::getDate,
                        Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
        ChartYVo expendChartYVo = new ChartYVo();
        expendChartYVo.setLineName("支出");
        List<String> expendValueStrings = months.stream().map(month -> getYByX(month, level, expend, inVo)).collect(Collectors.toList());
        expendChartYVo.setYList(expendValueStrings);
        List<ChartYVo> chartYVos = new ArrayList<>();
        chartYVos.add(incomeChartYVo);
        chartYVos.add(expendChartYVo);
        echartVos.add(new EchartVo("收入支出表", months, chartYVos));


        //资产
        Map<Date, Double> property = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(3)
                        && StringUtils.isNotEmpty(i.getValue()))
                .collect(Collectors.groupingBy(Item::getDate,
                        Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
        ChartYVo propertyChartYVo = new ChartYVo();
        propertyChartYVo.setLineName("资产");
        List<String> propertyValueStrings = months.stream().map(month -> getYByX(month, level, property, inVo)).collect(Collectors.toList());
        propertyChartYVo.setYList(propertyValueStrings);
        //负债
        Map<Date, Double> debt = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(4)
                        && StringUtils.isNotEmpty(i.getValue()))
                .collect(Collectors.groupingBy(Item::getDate,
                        Collectors.summingDouble(i -> Double.parseDouble(i.getValue()))));
        ChartYVo debtChartYVo = new ChartYVo();
        debtChartYVo.setLineName("负债");
        List<String> debtValueStrings = months.stream().map(month -> getYByX(month, level, debt, inVo)).collect(Collectors.toList());
        debtChartYVo.setYList(debtValueStrings);
        List<ChartYVo> debtChartYVos = new ArrayList<>();
        debtChartYVos.add(propertyChartYVo);
        debtChartYVos.add(debtChartYVo);
        echartVos.add(new EchartVo("资产负债表", months, debtChartYVos));

        //总资产收益线
        List<ChartYVo> propertyChartYVos = new ArrayList<>();
        ChartYVo roaChartYVo = new ChartYVo();
        roaChartYVo.setLineName("总资产");
        List<Double> debtValueDoubles = debtValueStrings.stream()
                .mapToDouble(Double::valueOf) // 将字符串转换为double
                .boxed() // 将DoubleStream转换为Stream<Double>
                .collect(Collectors.toList());
        List<String> roa = getROA(debtValueDoubles);
        roaChartYVo.setYList(roa);
        propertyChartYVos.add(roaChartYVo);
        //每一个资产的收益率曲线
        Map<String, List<Item>> collect = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(3)
                        && StringUtils.isNotEmpty(i.getValue()))
                .collect(Collectors.groupingBy(Item::getName
                ));
        collect.forEach((key, value) -> {
            ChartYVo oneRoaChartYVo = new ChartYVo();
            oneRoaChartYVo.setLineName(key);
            List<Double> collect1 = value.stream().map(i -> Double.parseDouble(i.getValue())).collect(Collectors.toList());

            oneRoaChartYVo.setYList(getROA(collect1));
            propertyChartYVos.add(oneRoaChartYVo);
        });
        echartVos.add(new EchartVo("资产收益表", months, propertyChartYVos));


        //资产二阶导表
        List<ChartYVo> twoPropertyChartYVos = new ArrayList<>();
        ChartYVo twoRoaChartYVo = new ChartYVo();
        twoRoaChartYVo.setLineName("总资产");
        List<String> twoRoa = getTwoROA(debtValueDoubles);
        twoRoaChartYVo.setYList(twoRoa);
        twoPropertyChartYVos.add(twoRoaChartYVo);
        //每一个资产的收益率曲线
        Map<String, List<Item>> twoRoaCollect = list.stream().filter(i -> i.getType().equals(2) && i.getItemType().equals(3)
                        && StringUtils.isNotEmpty(i.getValue()))
                .collect(Collectors.groupingBy(Item::getName
                ));
        twoRoaCollect.forEach((key, value) -> {
            ChartYVo oneRoaChartYVo = new ChartYVo();
            oneRoaChartYVo.setLineName(key);
            List<Double> collect1 = value.stream().map(i -> Double.parseDouble(i.getValue())).collect(Collectors.toList());
            oneRoaChartYVo.setYList(getTwoROA(collect1));
            twoPropertyChartYVos.add(oneRoaChartYVo);
        });
        echartVos.add(new EchartVo("资产二阶导表", months, twoPropertyChartYVos));
    }

    private String getYByX(String month, Integer level, Map<Date, Double> income, EchartsInVo inVo) {
        if (level == 1) {
            DateTime monthDate = DateUtil.parse(inVo.getYear() + "-" + month + "-01");
            Double v = income.get(monthDate);
            if (v != null) {
                return String.valueOf(v);
            } else {
                return "0";
            }
        } else {
            DateTime yearDate = DateUtil.parse(month + "-01-01");
            Double v = income.get(yearDate);
            if (v != null) {
                return String.valueOf(v);
            } else {
                return "0";
            }
        }
    }

    @Override
    public CommonResult<Item> insert(Item item) {
        item.setCreateTime(new Date());
        item.setIfDelete(0);
        save(item);

        saveOrUpdateRecord(item);
        saveOrUpdateTagRelation(item);

        return CommonResult.success(item);
    }

    private void saveOrUpdateTagRelation(Item item) {
        //全删全加
        QueryWrapper<ItemTagRelation> itemTagRelationQueryWrapper = new QueryWrapper<>();
        itemTagRelationQueryWrapper.lambda().eq(ItemTagRelation::getItemId, item.getId());
        itemTagRelationService.remove(itemTagRelationQueryWrapper);
        if (!CollectionUtils.isEmpty(item.getTagList())) {
            itemTagRelationService.saveBatch(item.getTagList().stream().map(tag -> {
                ItemTagRelation itemTagRelation = new ItemTagRelation();
                itemTagRelation.setTagId(tag.getId());
                itemTagRelation.setItemId(item.getId());
                return itemTagRelation;
            }).collect(Collectors.toList()));
        }
    }

    @Override
    public CommonResult<Item> myUpdate(Item item) {
        item.setUpdateTime(new Date());
        updateById(item);

        saveOrUpdateRecord(item);
        saveOrUpdateTagRelation(item);

        return CommonResult.success(item);
    }

    private void saveOrUpdateRecord(Item item) {
        QueryWrapper<ItemRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ItemRecord::getItemId, item.getId()).eq(ItemRecord::getDate, item.getDate()); // 假设 unique_column 是你的唯一索引字段

        ItemRecord itemRecord = new ItemRecord();
        itemRecord.setItemId(item.getId());
        itemRecord.setDate(item.getDate());
        itemRecord.setValue(item.getValue());
        // 首先尝试更新
        boolean update = itemRecordService.update(itemRecord, queryWrapper);
        if (!update) {
            // 如果更新行数为0，说明没有找到匹配的唯一索引，执行插入操作
            itemRecordService.save(itemRecord);
        }
    }

    /**
     * 获取资产收益率
     *
     * @param originalList 资产list
     * @return {@link List}<{@link String}>
     */
    public static List<String> getROA(List<Double> originalList) {
        List<String> newList = new ArrayList<>();

        for (int i = 0; i < originalList.size(); i++) {
            double current = originalList.get(i);
            double previous = (i == 0) ? current : originalList.get(i - 1);
            if (previous == 0) {
                newList.add("0");
            } else {
                double result = (current / previous - 1) * 100;
                newList.add(String.format("%.0f%%", result));
            }
        }
        return newList;
    }

    /**
     * 获取资产收益率二阶导
     *
     * @param originalList 资产list
     * @return {@link List}<{@link String}>
     */
    public static List<String> getTwoROA(List<Double> originalList) {
        List<Double> firstDifferences = new ArrayList<>();
        List<Double> secondDifferences = new ArrayList<>();

        // Calculate first differences
        for (int i = 0; i < originalList.size(); i++) {
            double previous = (i == 0) ? originalList.get(i) : originalList.get(i - 1);
            firstDifferences.add(originalList.get(i) - previous);
        }

        // Calculate second differences
        for (int i = 0; i < firstDifferences.size(); i++) {
            double previous = (i == 0) ? firstDifferences.get(i) : firstDifferences.get(i - 1);
            secondDifferences.add(firstDifferences.get(i) - previous);
        }

        // Convert to String with percentage format
        List<String> result = new ArrayList<>();
        for (double value : secondDifferences) {
            result.add(String.format("%.0f%%", value * 100));
        }

        return result;
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
