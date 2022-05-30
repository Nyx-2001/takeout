package com.starsofocean.reggie.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

public class SendMsg {
    public static void sendMsg(String phoneNumber,String code) {
// 短信应用 SDK AppID
        int appid = 140000000; // SDK AppID 以1400开头
// 短信应用 SDK AppKey
        String appkey = "**********";
// 需要发送短信的手机号码
        String[] phoneNumbers = {phoneNumber};
// 短信模板 ID，需要在短信应用中申请
        int templateId =78390; // NOTE: 这里的模板 ID`7839`只是示例，真实的模板 ID 需要在短信控制台中申请
// 签名
        String smsSign = "********"; // NOTE: 签名参数使用的是`签名内容`，而不是`签名ID`。这里的签名"腾讯云"只是示例，真实的签名需要在短信控制台申请

        try {
            String[] params = {code};
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    templateId, params, smsSign, "", "");
            System.out.println(result);
        }
        catch (Exception e){
            e.printStackTrace();
        }
//        catch (HTTPException e) {
//            // HTTP 响应码错误
//            e.printStackTrace();
//        } catch (JSONException e) {
//            // JSON 解析错误
//            e.printStackTrace();
//        } catch (IOException e) {
//            // 网络 IO 错误
//            e.printStackTrace();
//        }
    }
}


