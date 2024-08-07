package com.macro.mall.tiny.modules.runningJerry.vo;

import com.macro.mall.tiny.modules.runningJerry.model.Item;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * itemVo
 *
 * @author 马翔
 * @date 2024/08/07
 */
@Getter
@Setter
@Api("itemVo")
@NoArgsConstructor
public class ItemVo {
    @ApiModelProperty("项类型str")
    private String itemTypeStr;

    @ApiModelProperty("项类型")
    private Integer itemType;

    @ApiModelProperty("item列表")
    private List<Item> itemList;

    public ItemVo(String itemTypeStr,Integer itemType){
        this.itemType=itemType;
        this.itemTypeStr=itemTypeStr;
        this.itemList=new ArrayList<>(1);
    }
}
