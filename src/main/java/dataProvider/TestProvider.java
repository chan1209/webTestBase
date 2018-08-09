package dataProvider;//import base.adminipd.MagnetoBatisBase;

import org.testng.annotations.DataProvider;
import org.testng.log4testng.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;


/**
 * Created by chenpei on 2017/3/1.
 */

public class TestProvider {



    String fileName;
    String sheetName;

    private String caseExcelPath =System.getProperty("user.dir")+"/src/main/resources/";
    public  String caseInfo = "";

    private static final Logger LOGGER = Logger.getLogger(TestProvider.class);

    public static String clazzName = "";

    public static String methodName = "";

    public static String prepareDataUrl = "";


//    private static String reportLocation = "report/ExtentReport.html";
//    protected static ExtentReports extent;
//    @BeforeSuite
//    public static void befores(){
//        extent = new ExtentReports(reportLocation, true);
//        extent.startReporter(ReporterType.DB, reportLocation);
//        extent.addSystemInfo("Host Name", "Lining");
//    }
//
//    @AfterSuite
//    public static void afters(){
//
//        extent.close();
//
//    }
//    public static ExtentReports getextent(){
//        return extent;
//    }



//    private MagnetoBatisBase broBatisBase  = new MagnetoBatisBase();


//    /**
//     * 执行测试文件之间获取完成的类路径名
//     */
//    @BeforeClass
//    public void getTestClassName() {
//        ITestResult it = Reporter.getCurrentTestResult();
//        clazzName = it.getTestClass().getName();
//    }
//
//
//    @BeforeMethod
//    public void insertTestData()throws IOException {
//
//        //插入需要关注的数据
//        if (insertAll(prepareDataUrl)) {
//            LOGGER.info("数据插入成功");
//        } else {
//            LOGGER.info("数据插入有误.请核对数据脚本与数据库中的数据!");
//        }
//    }
//
//
//    @AfterMethod
//    public void clearTestData() throws IOException {
//
//        File file = new File(prepareDataUrl);
//
//        if (!file.exists()){
//            LOGGER.info("文件不存在不需要删除数据");
//        }else{
//            BufferedReader preFile = new BufferedReader(new InputStreamReader(new FileInputStream(prepareDataUrl), "UTF-8"));
//
//            String record;
//            int deleteLines = 0;
//            int lines = 0;
//            //去掉第一行的注释信息
//            preFile.readLine();
//            //遍历读取文件中除第一行外的其他所有内容并存储在名为records的ArrayList中，每一行records中存储的对象为一个String数组
//            while ((record = preFile.readLine()) != null) {
//                String fields[] = record.split("\\|");
//                //遍历删除数据
//                int i = deleteAnyDate(fields[0],getCondition(fields[1],fields[2]));
//                if (i >0){
//                    deleteLines++;
//                }
//                lines++;
//            }
//            preFile.close();
//
//            if (deleteLines == lines){
//                LOGGER.info("数据删除满足期望");
//            }else{
//                LOGGER.info("预期删除数据" + lines + "条!实际删除"+ deleteLines +"条");
//            }
//
//
//        }
//
//    }
//
//
//    private static void creatFile(String patchUrl) throws IOException {
//
//        File file = new File(patchUrl);
//
//        //如果没有的话对应的创建一个
//        if (!file.exists()) {
//            file.getParentFile().mkdirs();
//            file.createNewFile();
//        }
//
//    }
//
//    private boolean insertAll(String dataPath) throws IOException {
//
//        boolean result = false;
//
//        File filenew = new File(dataPath);
//
//        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath), "UTF-8"));
//        //忽略读取CSV文件的标题行（第一行）
//        file.readLine();
//
//        String record;
//        if (filenew.length() < 1) {
//            LOGGER.info("没有需要插入的参数");
//            return true;
//        }
//
//        int counts = 0;
//        int lines = 0;
//        while ((record = file.readLine()) != null) {
//            String fields[] = record.split("\\|");
//            int count = insertAnyData(fields[0], fields[1], fields[2]);
//            lines++;
//            if (count == 1) {
//                counts++;
//            }
//
//        }
//
//        file.close();
//
//        if (counts == lines) {
//            LOGGER.info("插入了:" + lines + "条数据");
//            result = true;
//        } else {
//
//            LOGGER.info("应该插入:" + file.readLine().length() + "条");
//            LOGGER.info("实际插入:" + counts + "条");
//        }
//
//        return result;
//    }
//
//
//    /**
//     * 参数介绍
//     *
//     * @param tableName   表名
//     * @param columnName  字段列表
//     * @param columnValue 参数列表
//     * @return
//     */
//    private int insertAnyData(String tableName, String columnName, String columnValue) {
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("tableName", tableName);
//        map.put("columnName", columnName);
//        map.put("columnValue", columnValue);
//
//        return broBatisBase.DBexecuteInsert("AnyDataMapper.insertAnyData", map);
//
//
//    }
//
//    private int deleteAnyDate(String tableName, String columnValue){
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("tableName", tableName);
//        map.put("columnValue", columnValue);
//
//        return broBatisBase.DBexecuteInsert("AnyDataMapper.deleteAnyData", map);
//
//    }
//
//
//    /**
//     * 根据数据库中的name和value去拼装where语句
//     * @param key
//     * @param value
//     * @return
//     */
//    private String getCondition(String key,String value){
//
//        String[] keys = key.split(",");
//        String[] values = value.split(",");
//
//        String condition = "";
//
//        for (int i =0;i< keys.length;i++){
//
//            condition = condition + keys[i] +"=" +values[i] + " AND ";
//        }
//
//        //去掉最后的AND
//        condition = condition.substring(0,condition.length()-4);
//
//        return condition;
//    }



    @DataProvider(name = "DataProvider")
    protected  Object[][]  dataInfo(Method method) throws IOException {
        String methodName = method.getName();
        DataSource dataSource  = method.getAnnotation(DataSource.class);
        fileName = dataSource.fileName();
        sheetName = dataSource.sheetName();
        String[] clazzNames = clazzName.split("\\.");
        methodName=methodName.replace("do","");
        caseInfo=methodName.toLowerCase();
//      System.out.println("caseInfo is :"+caseInfo+"***");

        //获取类名和方法名拼装成测试数据文件名称
 //      String newClazzName = clazzNames[clazzNames.length - 1] + "_" + methodName;
         //获取完成的本地环境路径,并创建
  //      String localUrl = System.getProperty("user.dir") + "/src/test/resources/AutoData/" + clazzNames[clazzNames.length -1] + "/" + newClazzName;

 //       prepareDataUrl = localUrl + "_" + "pre";


   //     creatFile(prepareDataUrl);

        Object[][] myObj = null;
//        List<Map<String, String>> list = dataProvider.ReadCaseExcel.readXlsx(caseExcelPath,clazzNames[clazzNames.length - 1] ,caseInfo);
        List<Map<String, String>> list = ReadCaseExcel.readXlsx(caseExcelPath+fileName,sheetName ,caseInfo);
        myObj = CaseUtils.getObjArrByList(list);
        System.out.println("method is :"+method.getName());
        return myObj;
    }





}
