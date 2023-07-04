package com.lzb.wechat;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 微信群机器人
 */
public class WeChatBotUtils {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    // 配置的群机器人Webhook地址
    private String botUrl;
    // 配置代理服务器
    private String hostname;
    private int port;

    // 需要使用代理时调用的构造函数
    public WeChatBotUtils(String botUrl, String hostname, int port) {
        this.botUrl = botUrl;
        this.hostname = hostname;
        this.port = port;
    }

    // 直接从配置中获取代理信息
    public WeChatBotUtils(String botUrl, boolean byProxy) {
        this.botUrl = botUrl;
        if (byProxy) {
            hostname = System.getProperty("proxyHost");
            port = Integer.valueOf(System.getProperty("proxyPort"));
        }
    }

    public static void main(String[] args) throws Exception {

        String botUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=6b87ebc0-0df8-416c-8084-d089f55255ff";
        WeChatBotUtils weChatBot = new WeChatBotUtils(botUrl, false);

        // 发送文本消息
        weChatBot.sendTextMsg("测试消息\n" + "hello world!");

        // 发送图片消息
        // weChatBot.sendImgMsg("C:/User/Desktop/test.jpg");

        // 发送MarkDown消息
        String markdownMsg =
                "测试：您的会议室已经预定，稍后会同步到`邮箱` \n" +
                        "    >**事项详情** \n" +
                        "    >事　项：<font color=\"info\">开会</font> \n" +
                        "    >组织者：@李占斌 \n" +
                        "    >参与者：@李占斌 \n" +
                        "    >\n" +
                        "    >会议室：<font color=\"info\">广州sie 22楼 2201</font>\n" +
                        "    >日　期：<font color=\"warning\">2023年6月6日</font>\n" +
                        "    >时　间：<font color=\"comment\">上午9:00-11:00</font>\n" +
                        "    >\n" +
                        "    >请准时参加会议。\n";
        weChatBot.sendMarKDownMsg(markdownMsg);

        // 发送文件消息
        // weChatBot.sendFileMsg("C:/User/Desktop/test.xlsx");
    }

    /**
     * 发送文字消息
     *
     * @param msg 需要发送的消息
     * @return
     * @throws Exception
     */
    public String sendTextMsg(String msg) throws Exception {
        JSONObject text = new JSONObject();
        text.put("content", msg);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "text");
        reqBody.put("text", text);
        reqBody.put("safe", 0);

        return callWeChatBot(reqBody.toString());
    }

    /**
     * 发送图片消息，需要对图片进行base64编码并计算图片的md5值
     *
     * @param path 需要发送的图片路径
     * @return
     * @throws Exception
     */
    public String sendImgMsg(String path) throws Exception {

        String base64 = "";
        String md5 = "";

        // 获取Base64编码
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] bs = new byte[inputStream.available()];
            inputStream.read(bs);
            base64 = Base64.getEncoder().encodeToString(bs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取md5值
        try {
            FileInputStream inputStream = new FileInputStream(path);
            byte[] buf = new byte[inputStream.available()];
            inputStream.read(buf);
            md5 = DigestUtils.md5Hex(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject image = new JSONObject();
        image.put("base64", base64);
        image.put("md5", md5);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "image");
        reqBody.put("image", image);
        reqBody.put("safe", 0);

        return callWeChatBot(reqBody.toString());
    }

    /**
     * 发送MarKDown消息
     *
     * @param msg 需要发送的消息
     * @return
     * @throws Exception
     */
    public String sendMarKDownMsg(String msg) throws Exception {
        JSONObject markdown = new JSONObject();
        markdown.put("content", msg);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "markdown");
        reqBody.put("markdown", markdown);
        reqBody.put("safe", 0);

        return callWeChatBot(reqBody.toString());
    }

    /**
     * 发送文件消息，需要先将文件上传到企业微信临时素材，再根据获取的media_id调用群机器人
     *
     * @param path 需要发送的文件路径
     * @return
     * @throws Exception
     */
    public String sendFileMsg(String path) throws Exception {
        File file = new File(path);

        // 构造RequestBody对象，用来携带要提交的数据；需要指定MediaType，用于描述请求/响应 body 的内容类型
        MediaType contentType = MediaType.parse("application/form-data; boundary");
        RequestBody body = RequestBody.create(contentType, file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), body)
                .build();

        // 上传到临时素材
        String key = botUrl.substring(botUrl.indexOf("key="));
        System.out.println(key);
        String mediaUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/upload_media?type=file&" + key;
        log.info("将文件" + path + "上传到临时素材：" + mediaUrl);
        String respMsg = okHttp(requestBody, mediaUrl);

        // 获取临时素材id
        JSONObject result = JSONObject.parseObject(respMsg);
        String media_id = result.getString("media_id");

        JSONObject fileJson = new JSONObject();
        fileJson.put("media_id", media_id);
        JSONObject reqBody = new JSONObject();
        reqBody.put("msgtype", "file");
        reqBody.put("file", fileJson);
        reqBody.put("safe", 0);

        // 调用群机器人发送消息
        return callWeChatBot(reqBody.toString());
    }

    /**
     * 调用群机器人
     *
     * @param reqBody 接口请求参数
     * @throws Exception 可能有IO异常
     */
    public String callWeChatBot(String reqBody) throws Exception {
        log.info("请求参数：" + reqBody);

        // 构造RequestBody对象，用来携带要提交的数据；需要指定MediaType，用于描述请求/响应 body 的内容类型
        MediaType contentType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(contentType, reqBody);

        // 调用群机器人
        String respMsg = okHttp(body, botUrl);

        if ("0".equals(respMsg.substring(11, 12))) {
            log.info("向群发送消息成功！");
        } else {
            log.info("请求失败！");
            // 发送错误信息到群
            sendTextMsg("群机器人推送消息失败，错误信息：\n" + respMsg);
        }
        return respMsg;
    }

    /**
     * @param body 携带需要提交的数据
     * @param url  请求地址
     * @return
     * @throws Exception
     */
    public String okHttp(RequestBody body, String url) throws Exception {
        // 构造和配置OkHttpClient
        OkHttpClient client;
        if (hostname != null && port != 0) {
            client = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port))) // 内网使用代理，不需要可注释
                    .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS) // 设置读取超时时间
                    .build();
        } else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS) // 设置读取超时时间
                    .build();
        }

        // 构造Request对象
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("cache-control", "no-cache") // 响应消息不缓存
                .build();

        // 构建Call对象，通过Call对象的execute()方法提交异步请求
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 请求结果处理
        byte[] datas = response.body().bytes();
        String respMsg = new String(datas);
        log.info("返回结果：" + respMsg);

        return respMsg;
    }

}
