package com.macro.mall.tiny.modules.runningJerry.controller;




import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.Item;

import com.macro.mall.tiny.modules.runningJerry.service.ItemService;
import com.macro.mall.tiny.modules.runningJerry.vo.ChartVo;
import com.macro.mall.tiny.modules.runningJerry.vo.EchartsInVo;
import com.macro.mall.tiny.modules.runningJerry.vo.EchartVo;
import com.macro.mall.tiny.modules.runningJerry.vo.ItemVo;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        return itemService.insert(item);

    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Item> update(@Validated @RequestBody Item item) {
        return itemService.myUpdate(item);
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Item> delete(@Validated @RequestBody Item item) {
        item.setCreateTime(new Date());
        item.setIfDelete(1);
        itemService.updateById(item);
        return CommonResult.success(item);
    }

    @ApiOperation(value = "查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<ItemVo>> query(@Validated @RequestBody Item item) {
        return itemService.queryTree(item);
    }

    @ApiOperation(value = "查询图表")
    @RequestMapping(value = "/queryEcharts", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<ChartVo>> queryEcharts(@Validated @RequestBody EchartsInVo inVo) {
        return itemService.queryEcharts(inVo);
    }

}

