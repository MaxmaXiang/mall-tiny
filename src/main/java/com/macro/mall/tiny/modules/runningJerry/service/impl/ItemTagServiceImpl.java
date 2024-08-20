package com.macro.mall.tiny.modules.runningJerry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.runningJerry.model.ItemTag;
import com.macro.mall.tiny.modules.runningJerry.mapper.ItemTagMapper;
import com.macro.mall.tiny.modules.runningJerry.service.ItemTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-08-20
 */
@Service
public class ItemTagServiceImpl extends ServiceImpl<ItemTagMapper, ItemTag> implements ItemTagService {

    @Autowired
    private ItemTagMapper itemTagMapper;


    @Override
    public CommonResult<ItemTag> insert(ItemTag itemTag) {
        int insert = itemTagMapper.insert(itemTag);
        return  CommonResult.success(itemTag);
    }

    @Override
    public CommonResult<ItemTag> myUpdate(ItemTag itemTag) {
        int i = itemTagMapper.updateById(itemTag);
        return CommonResult.success(itemTag);
    }

    @Override
    public CommonResult<List<ItemTag>> myList(ItemTag itemTag) {
        QueryWrapper<ItemTag> itemTagQueryWrapper = new QueryWrapper<>();
        itemTagQueryWrapper.lambda().eq(ItemTag::getUserName,itemTag.getUserName());
        List<ItemTag> itemTags = itemTagMapper.selectList(itemTagQueryWrapper);
        return CommonResult.success(itemTags);
    }
}
