package com.macro.mall.tiny.modules.runningJerry.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author macro
 * @since 2024-08-20
 */
@Getter
@Setter
@TableName("item_tag")
@ApiModel(value = "ItemTag对象", description = "ItemTag对象")
public class ItemTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("项id")
    private Integer itemId;

    @ApiModelProperty("标签名称")
    private String tagName;

    @ApiModelProperty("用户名称")
    private String userName;
}
