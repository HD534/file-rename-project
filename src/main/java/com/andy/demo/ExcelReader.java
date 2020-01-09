package com.andy.demo;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {


    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";


    public static void main(String[] args) {

    }

    /**
     * 根据文件后缀名类型获取对应的工作簿对象
     *
     * @param inputStream 读取文件的输入流
     * @param fileType    文件后缀名类型（xls或xlsx）
     * @return 包含文件数据的工作簿对象
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    public static List<String> readExcel(String fileName) {

        // 获取Excel后缀名
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        // 获取Excel文件
        File excelFile = new File(fileName);
        if (!excelFile.exists()) {
            System.out.println("指定的Excel文件不存在！");
            return new ArrayList<>();
        }

        // 获取Excel工作簿
        try (Workbook workbook = getWorkbook(
                new FileInputStream(excelFile), fileType)
        ) {
            // 读取excel中的数据
            return parseExcel(workbook);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private static List<String> parseExcel(Workbook workbook) {
        List<String> resultList = new ArrayList<>();
        Sheet firstSheet = workbook.getSheetAt(0);
        // 校验sheet是否合法
        if (firstSheet == null) {
            return resultList;
        }


        int physicalNumberOfRows = firstSheet.getPhysicalNumberOfRows();
        for (int i = 0; i < physicalNumberOfRows; i++) {
            Row row = firstSheet.getRow(i);

            if (null == row) {
                continue;
            }

            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }

            String cellValue = cell.getStringCellValue();
            if (StringUtils.isNoneEmpty(cellValue)) {
                resultList.add(cellValue);
            }
        }
        return resultList;
    }

}
