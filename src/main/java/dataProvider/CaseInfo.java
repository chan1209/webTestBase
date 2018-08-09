package dataProvider;

import java.util.Map;

/**
* Created by huangjingqing on 16/9/10.
*/
public class CaseInfo {

        private Map<String,String> caseParam;

        private Map<String,String> caseInfo;

        private Map<String,String> caseDesc;

        private Map<String,String> casePreset;


        public Map<String, String> getCaseParam() {
            return caseParam;
        }

        public void setCaseParam(Map<String, String> caseParam) {
            this.caseParam = caseParam;
        }

        public Map<String, String> getCaseInfo() {
        return caseInfo;
    }

        public void setCaseInfo(Map<String, String> caseInfo) {
        this.caseInfo = caseInfo;
    }

        public Map<String, String> getCaseDesc() {
            return caseDesc;
        }

        public void setCaseDesc(Map<String, String> caseDesc) {
            this.caseDesc = caseDesc;
        }

        public Map<String, String> getCasePreset() {
            return casePreset;
        }

        public void setCasePreset(Map<String, String> casePreset) {
            this.casePreset = casePreset;
        }


}
