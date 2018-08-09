package webTestUtils.vo;

/**
 * Created by chenpei on 2018-04-10.
 */
public class HttpResult {
    private int resultCode;
    private String responseBody;
    private long executeTime;

    public int getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public long getExecuteTime() {
        return this.executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public boolean isSuccess() {
        return this.resultCode == 200;
    }

    public HttpResult(int resultCode, String responseBody) {
        this.resultCode = resultCode;
        this.responseBody = responseBody;
    }

    public HttpResult() {
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("HttpResult{");
        sb.append("resultCode=").append(this.resultCode);
        sb.append(", responseBody=\'").append(this.responseBody).append('\'');
        sb.append(", executeTime=").append(this.executeTime);
        sb.append('}');
        return sb.toString();
    }
}