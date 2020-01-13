package com.andy.demo;

import com.andy.demo.ui.MainWindow;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.andy.demo.Const.*;
import static java.util.stream.Collectors.toList;

public class FileCopy {


    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String MV_DB_PATH_KEY = "mv_db_path";
    private static final String EXCEL_PATH_KEY = "excel_path";
    private static final String TARGET_PATH_KEY = "target_path";

    private static String mvDbPath;
    private static String excelFilePath;
    private static String targetPath;

    private static List<File> mvList = new ArrayList<>();
    private static List<String> mvNameList = new ArrayList<>();

    private static List<TargetObject> targetObjectList = new ArrayList<>();

    private static Map<String, FileMap> mvMap;

    public static void main(String[] args) throws IOException {

        initPathAndConfig();

        initMvList();

        initTargetList();

        copyFiles();

    }

    public static void copy() throws Exception {
        initMvList();

        initTargetList();

        copyFiles();
    }

    private static void copyFiles() throws IOException {
        for (TargetObject targetObject : targetObjectList) {
            List<String> toList = new ArrayList<>();
            if (CollectionUtils.isEmpty(targetObject.targetList)) {
                System.out.println("需要复制到的文件路径为空");
                return;
            }

            for (String mvFileName : targetObject.targetList) {
                if (mvNameList.contains(mvFileName)) {
                    int i = mvNameList.indexOf(mvFileName);
                    toList.add(mvNameList.get(i));
                }
            }

            String tPath = targetPath + File.separator + targetObject.excelName;

//            FileCopyThreadPoolExecutor.executor.execute(() -> {
                copyFiles(tPath, toList);
//            });
        }
    }

    private static void initPathAndConfig() throws IOException {
        String path = FileCopy.class.getProtectionDomain().getCodeSource().getLocation().getFile();
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

    private static void copyFiles(String targetPath, List<String> toList) {
        long start = System.currentTimeMillis();
        if (StringUtils.isEmpty(targetPath)) {
            return;
        }
        if (CollectionUtils.isEmpty(toList)) {
            MainWindow.log(String.format("需要复制到文件列表 %s 的 mp4 文件为空", targetPath));
            return;
        }
        MainWindow.log(String.format("开始复制 mp4 文件到路径 %s ; 总共有 %s 个文件需要复制", targetPath, toList.size()));
        for (String name : toList) {
            try {
                FileUtils.copyFile(new File(mvDbPath + File.separator + name),
                        new File(targetPath + File.separator + name));
            } catch (IOException e) {
                e.printStackTrace();
                MainWindow.log(String.format("复制文件 %s 到路径 %s 失败！", name, targetPath));
            }
        }
        long end = System.currentTimeMillis();
        long period = end - start;

        MainWindow.log(String.format("成功复制文件到路径 %s 消耗时间: %s 秒\n ", targetPath, TimeUnit.MILLISECONDS.toSeconds(period)));
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
        if (excelFileList == null || excelFileList.length == 0) {
            MainWindow.log(excelFilePath + " 中没有找到 excel 文件.");
            throw new RuntimeException(excelFilePath + " 中没有找到 excel 文件.");
        }
        for (File file : excelFileList) {
            List<String> strings = ExcelReader.readExcel(file.getAbsolutePath());
            String excelName = file.getName().substring(0, file.getName().lastIndexOf("."));
            list.add(new TargetObject(excelName, strings));
        }
        targetObjectList = list;
    }

    private static void initMvList() {
        MainWindow.log(String.format("开始加载歌曲数据库 %s 中的文件。", mvDbPath));
        File dir = new File(mvDbPath);
        File[] mvArray = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().contains(".mp4");
            }
        });
        if (mvArray == null || mvArray.length == 0) {
            throw new RuntimeException(mvDbPath + " 中没有找到 mp4 文件.");
        }
        MainWindow.log("加载完成。歌曲数据库中 mp4 文件数量为:" + mvArray.length);
        List<File> fileList = Arrays.asList(mvArray);
        mvNameList = fileList.stream().map(File::getName).collect(toList());
//        mvMap = mvList.stream()
//                .collect(toMap(FileCopy::getNameWithoutOrderNumber, f -> {
//                    int order = getOrder(f);
//                    return new FileMap(order, getNameWithoutOrderNumber(f), f.getName());
//                }));
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


    public static void copy(String inputMvDbPath, String inputExcelPath, String inputTargetPath) throws Exception {
//        TimeUnit.SECONDS.sleep(2);
        verifyPaths(inputMvDbPath, inputExcelPath, inputTargetPath);
        mvDbPath = inputMvDbPath;
        excelFilePath = inputExcelPath;
        targetPath = inputTargetPath;
        copy();
    }

    private static void verifyPaths(String mvDbPath, String excelPath, String targetPath) {
        verifyPath(mvDbPath, MV_DB_PATH_CHINESE_NAME);
        verifyPath(excelPath, EXCEL_PATH_CHINESE_NAME);
        verifyPath(targetPath, TARGET_PATH_CHINESE_NAME);
    }

    private static void verifyPath(String mvDbPath, String pathName) {
        File f = new File(mvDbPath);
        boolean exists = f.exists();
        if (exists) {
            if (!f.isDirectory()) {
                MainWindow.log(pathName + " " + mvDbPath + " 不是文件夹路径，请重新选择。");
                throw new RuntimeException(pathName + " " + mvDbPath + " 不是文件夹路径，请重新选择。");
            }
        } else {
            MainWindow.log(pathName + " " + mvDbPath + " 不存在，请重新选择。");
            throw new RuntimeException(pathName + " " + mvDbPath + " 不存在，请重新选择。");
        }
    }
}


