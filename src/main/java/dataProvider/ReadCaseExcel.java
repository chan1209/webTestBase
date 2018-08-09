package dataProvider;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* Created by huangjingqing on 16/9/10.
*/
public class ReadCaseExcel {


    public static List<Map<String, String>> readXlsx(String fileName,String sheetName,String caseStr) {

        XSSFWorkbook xssfWorkbook=null;
        try {
            xssfWorkbook = new XSSFWorkbook(fileName);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        XSSFSheet xssfSheet = xssfWorkbook.getSheet(sheetName);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        XSSFRow rowTitleRow =xssfSheet.getRow(0);


        boolean endFlag=false;
        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {

            Row row = xssfSheet.getRow(rowNum);

            String str=getMergedRegionValue(xssfSheet, row.getRowNum(), 0);
            String beginStr=(str!=null)?str:getValue(xssfSheet.getRow(rowNum).getCell(0));

            if(beginStr!=null&&beginStr.split("\\n|\\r\\n|\\r")[0].trim().equals(caseStr)){
                endFlag=true;
            } else if(endFlag){
                break;
            }


            if(endFlag){
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);

                Map<String, String> map = new HashMap<String, String>();

                for (int cellNum = 1; cellNum <rowTitleRow.getLastCellNum(); cellNum++) {
                    XSSFCell xssfCell = xssfRow.getCell(cellNum);
                    XSSFCell xssfCellTitleCell = rowTitleRow.getCell(cellNum);

                    map.put(getValue(xssfCellTitleCell), getValue(xssfCell));
                }
                list.add(map);
            }else{

            }

        }

        return list;


    }
    @SuppressWarnings("static-access")
    private static String getValue(XSSFCell xssfCell) {
        if (xssfCell == null) {
            return "";
        }
        if (xssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfCell.getNumericCellValue()).split("\\.")[0];
        } else {
            String s = String.valueOf(xssfCell.getStringCellValue());
            if (s.startsWith("\"")) {
                s = s.replaceFirst("\"", "");
            }
            if (s.endsWith("\"")) {
                s = s.substring(0, s.length() - 1);
            }
            return s;
        }
    }
    public static String getMergedRegionValue(Sheet sheet , int row , int column){

           int sheetMergeCount = sheet.getNumMergedRegions();

           for (int i = 0; i < sheetMergeCount; i++) {
               CellRangeAddress ca = sheet.getMergedRegion(i);
               int firstColumn = ca.getFirstColumn();
               int lastColumn = ca.getLastColumn();
               int firstRow = ca.getFirstRow();
               int lastRow = ca.getLastRow();

               if (row >= firstRow && row <= lastRow) {

                   if (column >= firstColumn && column <= lastColumn) {
                       Row fRow = sheet.getRow(firstRow);
                       Cell fCell = fRow.getCell(firstColumn);
                       return getCellValue(fCell);
                   }
               }
           }

        return null ;
    }

    private static boolean isMergedRow(Sheet sheet,int row ,int column) {

        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row == firstRow && row == lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }


    private static boolean isMergedRegion(Sheet sheet,int row ,int column) {

        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {

            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }


    public static String getCellValue(Cell cell){

        if(cell == null) {
            return "";
        }

        if(cell.getCellType() == Cell.CELL_TYPE_STRING){


            return cell.getStringCellValue();

        }else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){

            return String.valueOf(cell.getBooleanCellValue());

        }else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){

            return cell.getCellFormula() ;

        }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){

            return String.valueOf(cell.getNumericCellValue());

        }
        return "";
    }

}
