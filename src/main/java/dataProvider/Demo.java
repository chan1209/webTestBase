package dataProvider;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import webTestUtils.RequestUtil;
import webTestUtils.vo.HttpResult;

/**
 * Created by chenpei on 2018-05-24.
 */

public class Demo extends TestProvider {
    RequestUtil requestUtil = new RequestUtil();

    @BeforeClass
    public void beforeClass() {

    }

    @DataSource(fileName = "data.xlsx",sheetName = "EcashPort")
    @Test(dataProvider="DataProvider")
    public void acctQuery(CaseInfo c) throws Exception {
        System.out.println(c.getCaseDesc());
        HttpResult httpResult = requestUtil.executePost("https://ecash.meizu.com/oauth/acct/query",null,HttpResult.class);
        System.out.println(httpResult);

    }



}
