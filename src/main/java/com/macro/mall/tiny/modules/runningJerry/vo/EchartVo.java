package com.macro.mall.tiny.modules.runningJerry.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * echartsVo
 *
 * @author 马翔
 * @date 2024/08/08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EchartVo {
    private String chartName;
    private List<String> xList;
    private List<ChartYVo> yVoList;

}
