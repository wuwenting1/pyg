package cn.itcast.sms;

import com.pinyougou.common.util.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;

public class SmsSendTest {
    public static void main(String[] args) {
        HttpClientUtils httpClientUtils = new HttpClientUtils(false);
        Map<String,String> params = new HashMap<>();
        params.put("phone","15766389027");
        params.put("signName","五子连珠");
        params.put("templateCode","SMS_11480310");
        params.put("templateParam","{\"number\":\"" + 888888 + "\"}");
        httpClientUtils.sendPost("http://sms.pinyougou.com/sms/sendSms",params);
    }
}
