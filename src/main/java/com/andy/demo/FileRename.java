package com.andy.demo;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class FileRename {


    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String MV_DB_PATH_KEY = "mv_db_path";
    private static final String EXCEL_PATH_KEY = "excel_path";
    private static final String TARGET_PATH_KEY = "target_path";

    private static String mvDbPath = "/Users/andy/work_space/file_space/mv_db";
    private static String excelFilePath;
    private static String targetPath;

    private static List<File> mvList = new ArrayList<>();

    private static List<TargetObject> targetObjectList = new ArrayList<>();

    private static Map<String, FileMap> mvMap;

    public static void main(String[] args) throws IOException {
        //1. 读取整个文件列表 /Users/andy/work_space/file_space/mv_db
        // 将文件列表各文件名字写入一个 list
        //2. 从excel文件中读取 所需的文件名字进入list
        //3. 按顺序从所需文件list 中获取名字，在所有文件名字 list 中匹配。
        //4. 找到文件后将文件重新命名，添加序号，并新增到特定到文件目录中，
        // e.g.场所序号1001    /Users/andy/work_space/file_space/demo_space/1001

        initPathAndConfig();

        initMvList();

        initTargetList();

        copyFiles();

        System.out.println("完成.");
    }

    private static void copyFiles() throws IOException {
        for (TargetObject targetObject : targetObjectList) {
            System.out.println("-------------------");
            List<FileMap> toList = new ArrayList<>();

            for (String fileName : targetObject.targetList) {
                if (mvMap.containsKey(fileName)) {
                    toList.add(mvMap.get(fileName));
                }
            }
            toList.sort(new Comparator<FileMap>() {
                @Override
                public int compare(FileMap o1, FileMap o2) {
                    return o1.originOrder.compareTo(o2.originOrder);
                }
            });
            for (int i = 0; i < toList.size(); i++) {
                toList.get(i).order = i + 1;
            }
            String tPath = targetPath + File.separator + targetObject.targetName;
            copyFiles(tPath, toList);
            System.out.println("-------------------");
        }
    }

    private static void initPathAndConfig() throws IOException {
        String path = FileRename.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File jarPath = new File(path);

        Properties pro = new Properties();
        FileInputStream in = new FileInputStream(jarPath.getParent() + File.separator + CONFIG_FILE_NAME);
        pro.load(in);
        in.close();

        if (!pro.containsKey(MV_DB_PATH_KEY)) {
            System.out.println("配置文件中没有指定 mv_db_path");
            throw new RuntimeException("配置文件中没有指定 mv_db_path");
        }

        if (!pro.containsKey(EXCEL_PATH_KEY)) {
            System.out.println("配置文件中没有指定 excel_path");
            throw new RuntimeException("配置文件中没有指定 excel_path");
        }

        if (!pro.containsKey(TARGET_PATH_KEY)) {
            System.out.println("配置文件中没有指定 target_path");
            throw new RuntimeException("配置文件中没有指定 target_path");
        }

        mvDbPath = pro.getProperty(MV_DB_PATH_KEY);
        excelFilePath = pro.getProperty(EXCEL_PATH_KEY);
        targetPath = pro.getProperty(TARGET_PATH_KEY);
        System.out.println("mv_db_path: " + mvDbPath);
        System.out.println("excel_path: " + excelFilePath);
        System.out.println("target_path: " + targetPath);

    }

    private static void copyFiles(String targetPath, List<FileMap> toList) throws IOException {
        if (StringUtils.isEmpty(targetPath)) {
            System.err.println("需要复制到的文件路径为空");
            return;
        }
        if (CollectionUtils.isEmpty(toList)) {
            System.out.println("需要复制到文件列表为空");
            return;
        }
        System.out.println(String.format("开始复制文件到路径 %s ",targetPath));
        System.out.println(String.format("总共有 %s 个文件需要复制",toList.size()));
        for (FileMap fileMap : toList) {
            FileUtils.copyFile(new File(mvDbPath + File.separator + fileMap.fileOriginName),
                    new File(targetPath + File.separator + fileMap.order + "、" + fileMap.name));
        }
        System.out.println(String.format("成功复制文件到路径 %s ",targetPath));
    }

    private static void initTargetList() {
        List<TargetObject> list = new ArrayList<>();
        File excelFolder = new File(excelFilePath);
        File[] excelFileList = excelFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(".xlsx") || pathname.getName().contains(".xls");
            }
        });
        if (excelFileList == null) {
            throw new RuntimeException(excelFilePath+" 中没有找到 excel 文件.");
        }
        for (File file : excelFileList) {
            List<String> strings = ExcelReader.readExcel(file.getAbsolutePath());
            String excelName = file.getName().substring(0, file.getName().lastIndexOf("."));
            list.add(new TargetObject(excelName, strings));
        }
        targetObjectList = list;
    }

    private static void initMvList() {
        System.out.println("开始加载mv_db...");
        File dir = new File(mvDbPath);
        File[] mvArray = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().contains(".mp4");
            }
        });
        if (mvArray == null) {
            throw new RuntimeException(mvDbPath + " 中没有找到 mp4 文件.");
        }
        System.out.println("加载完成. mp4 文件数量:" + mvArray.length);
        List<File> fileList = Arrays.asList(mvArray);
        mvList = fileList.stream()
                .sorted(new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        Integer order1 = getOrder(f1);
                        Integer order2 = getOrder(f2);
                        return order1.compareTo(order2);
                    }
                })
                .collect(Collectors.toList());
        mvMap = mvList.stream()
                .collect(toMap(FileRename::getNameWithoutOrderNumber, f -> {
                    int order = getOrder(f);
                    return new FileMap(order, getNameWithoutOrderNumber(f), f.getName());
                }));
//        System.out.println(mvMap);

    }

    /**
     * 去除文件名中的序号。
     * 例如：
     * 源文件名是： 1、歌曲1.mp4 ，获取文件名：歌曲1.mp4
     * 源文件名是： 1.歌曲2.mp4 ，获取文件名：歌曲2.mp4
     * 推荐使用 1、 作为文件序号。
     *
     * @param f
     * @return
     */
    private static String getNameWithoutOrderNumber(File f) {
        String f1Name = f.getName();
        String name;
        if (f1Name.contains("、")) {
            name = f1Name.split("、")[1];
        } else if (f1Name.contains(".")) {
            int i = f1Name.indexOf(".");
            name = f1Name.substring(i + 1);
        } else {
            System.err.println(String.format("文件名 %s 不含有符号 、或者 .  ，请在文件序号后添加 、 符号", f1Name));
            return f1Name;
        }
        return name;
    }

    private static int getOrder(File f1) {
        String f1Name = f1.getName();
        int order1;
        if (f1Name.contains("、")) {
            order1 = Integer.parseInt(f1.getName().split("、")[0]);
        } else if (f1Name.contains(".")) {
            order1 = Integer.parseInt(f1.getName().split("\\.")[0]);
        } else {
            System.err.println(String.format("文件名 %s 不含有符号 、或者 .  ，请在文件序号后添加 、 符号", f1Name));
            return -1;
        }
        return order1;
    }


}


