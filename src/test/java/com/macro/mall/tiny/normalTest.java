package com.macro.mall.tiny;

import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class normalTest {
    public static void main(String[] args) {
//        List<Integer> yValues = Arrays.asList(1, 2, 4, 4, 5);
//        List<Integer> firstDifferences = new ArrayList<>();
//        List<Integer> secondDifferences = new ArrayList<>();
//
//        // Calculate first differences
//        for (int i = 0; i < yValues.size(); i++) {
//            int previous = (i == 0) ? yValues.get(i) : yValues.get(i - 1);
//            firstDifferences.add(yValues.get(i) - previous);
//        }
//
//        // Calculate second differences
//        for (int i = 0; i < firstDifferences.size(); i++) {
//            int previous = (i == 0) ? firstDifferences.get(i) : firstDifferences.get(i - 1);
//            secondDifferences.add(firstDifferences.get(i) - previous);
//        }
//
//        System.out.println("First differences: " + firstDifferences);
//        System.out.println("Second differences: " + secondDifferences);

        // 定义年月日
        String year = "2024";
        String month = "8";
        String day = "9";

        // 使用DateUtil.parse来解析年月日
        java.util.Date date = DateUtil.parse(year + "-" + month + "-" + day);

        // 打印结果
        System.out.println("解析的日期: " + date);
    }
}
