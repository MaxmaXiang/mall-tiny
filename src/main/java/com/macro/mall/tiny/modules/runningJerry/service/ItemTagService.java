package com.macro.mall.tiny.modules.runningJerry.service;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.ItemTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author macro
 * @since 2024-08-20
 */
public interface ItemTagService extends IService<ItemTag> {

    CommonResult<ItemTag> insert(ItemTag itemTag);

    CommonResult<ItemTag> myUpdate(ItemTag itemTag);

    CommonResult<List<ItemTag>> myList(ItemTag itemTag);
}
