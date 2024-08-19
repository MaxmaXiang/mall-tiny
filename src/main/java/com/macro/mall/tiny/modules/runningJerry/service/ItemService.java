package com.macro.mall.tiny.modules.runningJerry.service;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.Item;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.tiny.modules.runningJerry.vo.EchartsInVo;
import com.macro.mall.tiny.modules.runningJerry.vo.EchartVo;
import com.macro.mall.tiny.modules.runningJerry.vo.ItemVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author macro
 * @since 2024-08-07
 */
public interface ItemService extends IService<Item> {

    CommonResult<List<ItemVo>> queryTree(Item item);

    CommonResult<List<EchartVo>> queryEcharts(EchartsInVo inVo);

    CommonResult<Item> insert(Item item);

    CommonResult<Item> myUpdate(Item item);
}
