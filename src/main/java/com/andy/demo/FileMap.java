package com.andy.demo;

class FileMap {
    Integer order;
    Integer originOrder;
    String name;
    String fileOriginName;

    public FileMap(Integer originOrder, String name, String fileOriginName) {
        this.originOrder = originOrder;
        this.name = name;
        this.fileOriginName = fileOriginName;
    }

    @Override
    public String toString() {
        return "FileMap{" +
                "order=" + order +
                ", originOrder=" + originOrder +
                ", name='" + name + '\'' +
                ", fileOriginName='" + fileOriginName + '\'' +
                '}';
    }
}
