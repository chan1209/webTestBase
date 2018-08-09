package dataProvider;

import java.util.*;

/**
* Created by huangjingqing on 16/9/10.
*/
public class CaseUtils {


        public static Object[] getObjArrByMap(Map<String,String> caseExcelMap){
            Map<String,String> param = new HashMap<String,String>();
            List<String> keys = new ArrayList<String>();
            List<String> values = new ArrayList<String>();
            Map<String,String> caseParam = new HashMap<String,String>();
            Map<String,String> caseInfo =new HashMap<String,String>();
            Map<String,String> caseDesc = new HashMap<String,String>();
            Map<String,String> casePreset =new HashMap<String,String>();
            CaseInfo ci = new CaseInfo();
            for (String key : caseExcelMap.keySet()) {
                if (key.indexOf("{$d}")== 0){
                    caseDesc.put(key.replace("{$d}", ""), caseExcelMap.get(key));
                }
                else if(key.indexOf("{$p}") == 0){
                    casePreset.put(key.replace("{$p}", ""), caseExcelMap.get(key));
                }
                else if(key.indexOf("{$i}") == 0){
                    casePreset.put(key.replace("{$i}", ""), caseExcelMap.get(key));
                }
                else {
                    String strValue = caseExcelMap.get(key);
                    String splitStr=",";
                    if(strValue.contains("#")){
                        splitStr="#";
                    }
                    if(key.equals("param")) {
                       keys= Arrays.asList(strValue.split(splitStr));
                    }else if(key.equals("value")){
                       values = Arrays.asList(strValue.split(splitStr));
                    }
                }
            }
            for(int i=0;i< keys.size()&&i<values.size(); i++){

                caseParam.put(keys.get(i).trim(), values.get(i).trim());

            }
//            System.out.println("caseParam :"+caseParam);
            ci.setCaseDesc(caseDesc);
            ci.setCaseParam(caseParam);
            ci.setCasePreset(casePreset);
            ci.setCaseInfo(caseInfo);

            return new Object[]{ci};
        }

        public static Object[][] getObjArrByList(List<Map<String,String>> caseExcelList){
            List<Map<String,String>> caseExcuteList = getExcuteList(caseExcelList);
            Object[][] objArray = new Object[caseExcuteList.size()][];
            for(int i = 0;i<caseExcuteList.size();i++){
                objArray[i]=getObjArrByMap(caseExcuteList.get(i));
            }
            return objArray;

        }

        private static List<Map<String,String>> getExcuteList(List<Map<String,String>> caseExcelList){
            List<Map<String,String>> list = new ArrayList<Map<String,String>>();
            for( Map<String,String> m : caseExcelList){
                    list.add(m);
            }
            return list;
        }


}
