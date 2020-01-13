package com.andy.demo;

import java.util.List;

public class TargetObject{
    String excelName;
    List<String> targetList;

    public TargetObject(String excelName, List<String> targetList) {
        this.excelName = excelName;
        this.targetList = targetList;
    }

    @Override
    public String toString() {
        return "TargetObject{" +
                "excelName='" + excelName + '\'' +
                ", targetList=" + targetList +
                '}';
    }
}
