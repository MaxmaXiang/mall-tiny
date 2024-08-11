package com.macro.mall.tiny.modules.runningJerry.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * echartsinVo
 *
 * @author 马翔
 * @date 2024/08/08
 */
@Getter
@Setter
public class EchartsInVo {
    /**
     * 1-月，2-年
     */
    private Integer period;
    private Integer year;
    @ApiModelProperty("用户id")
    private String userName;
}
