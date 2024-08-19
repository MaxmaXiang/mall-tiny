package com.macro.mall.tiny.modules.runningJerry.mapper;

import com.macro.mall.tiny.modules.runningJerry.model.Item;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.runningJerry.vo.EchartsInVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author macro
 * @since 2024-08-07
 */
public interface ItemMapper extends BaseMapper<Item> {

    List<Item> listRecord(Item item);

    List<Item> listRecordEcharts(EchartsInVo inVo);
}
