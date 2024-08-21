package com.macro.mall.tiny.modules.runningJerry.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ChartVo {
    private String dateStr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    @ApiModelProperty("收入")
    private Double income = 0D;

    @ApiModelProperty("支出")
    private Double expend = 0D;

    @ApiModelProperty("收益")
    private Double profit = 0D;

    @ApiModelProperty("资产")
    private Double property = 0D;

    @ApiModelProperty("负债")
    private Double debt = 0D;

    @ApiModelProperty("收益变化")
    private Double propertySecondOrderAdmittance = 0D;
}
