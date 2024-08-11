package com.macro.mall.tiny.modules.runningJerry.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author macro
 * @since 2024-08-07
 */
@Getter
@Setter
@ApiModel(value = "Item对象", description = "Item对象")
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户id")
    private String userName;

    @ApiModelProperty("父id")
    private Integer parentId;

    @ApiModelProperty("1-文件夹，2-文件")
    private Integer type;

    @ApiModelProperty("1-收入，2-支出，3-资产，4-负债")
    private Integer itemType;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty("归属日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("1-已删除，0-未删除")
    private Integer ifDelete;

    @ApiModelProperty("子item列表")
    @TableField(exist = false)
    private List<Item> children;
}
