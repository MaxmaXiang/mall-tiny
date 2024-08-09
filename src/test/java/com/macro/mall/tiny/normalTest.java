package com.macro.mall.tiny;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class normalTest {
    public static void main(String[] args) {
        List<Integer> yValues = Arrays.asList(1, 2, 4, 4, 5);
        List<Integer> firstDifferences = new ArrayList<>();
        List<Integer> secondDifferences = new ArrayList<>();

        // Calculate first differences
        for (int i = 0; i < yValues.size(); i++) {
            int previous = (i == 0) ? yValues.get(i) : yValues.get(i - 1);
            firstDifferences.add(yValues.get(i) - previous);
        }

        // Calculate second differences
        for (int i = 0; i < firstDifferences.size(); i++) {
            int previous = (i == 0) ? firstDifferences.get(i) : firstDifferences.get(i - 1);
            secondDifferences.add(firstDifferences.get(i) - previous);
        }

        System.out.println("First differences: " + firstDifferences);
        System.out.println("Second differences: " + secondDifferences);
    }
}
