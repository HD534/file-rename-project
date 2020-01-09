package com.andy.demo;

import java.util.List;

public class TargetObject{
    String targetName;
    List<String> targetList;

    public TargetObject(String targetName, List<String> targetList) {
        this.targetName = targetName;
        this.targetList = targetList;
    }

    @Override
    public String toString() {
        return "TargetObject{" +
                "targetName='" + targetName + '\'' +
                ", targetList=" + targetList +
                '}';
    }
}
