package com.macro.mall.tiny.modules.runningJerry.controller;


import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.Item;
import com.macro.mall.tiny.modules.runningJerry.model.ItemTag;
import com.macro.mall.tiny.modules.runningJerry.service.ItemService;
import com.macro.mall.tiny.modules.runningJerry.service.ItemTagService;
import com.macro.mall.tiny.modules.runningJerry.vo.ItemVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author macro
 * @since 2024-08-20
 */
@RestController
@RequestMapping("/runningJerry/itemTag")
public class ItemTagController {
    @Autowired
    private ItemTagService itemTagService;


    @ApiOperation(value = "新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ItemTag> insert(@Validated @RequestBody ItemTag itemTag) {
        return itemTagService.insert(itemTag);

    }

    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ItemTag> update(@Validated @RequestBody ItemTag itemTag) {
        return itemTagService.myUpdate(itemTag);
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ItemTag> delete(@Validated @RequestBody ItemTag itemTag) {
        itemTagService.removeById(itemTag);
        return CommonResult.success(itemTag);
    }

    @ApiOperation(value = "查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<ItemTag>> query(@Validated @RequestBody ItemTag itemTag) {
        return itemTagService.myList(itemTag);
    }
}

