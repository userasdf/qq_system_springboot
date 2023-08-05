package com.work.qq_system_springboot.tools;

import com.alibaba.fastjson.JSONObject;
import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.entity.User;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
//import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QQSystemUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    //MD5加密
    public static String md5(String key){
        if(org.apache.commons.lang3.StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //获取json格式的字符串
    public static String getJSONString(int code,String msg,Map<String,Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map!=null){
            for (String key : map.keySet()) {
                jsonObject.put(key,map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }


    //解析文本中的网址
    public static String getUrlFromText(String text){
        String pattern = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        String url = "";
        if(m.find()){
            url = m.group();
        }
        return url;
    }


    //获取json格式的字符串(方法重载)
    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    ////获取json格式的字符串(方法重载)
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }


    //获取当前时间
    public static String getCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }



    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getIpAddress(HttpServletRequest request){


        String ip = request.getHeader("X-Forwarded-For");



        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }



    /**
     * 解析 用户代理(User-Agent)
     * @param request 请求
     * @return "设备类型:%s,操作系统:%s,浏览器:%s,浏览器版本:%s,浏览器引擎:%s,用户代理(User-Agent):[%s]"
     * @author GongLiHai
     * @date 2020/8/25 11:12
     */
    public static OprLog getRequestInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);//解析agent字符串
        Browser browser = ua.getBrowser();//获取浏览器对象
        OperatingSystem os = ua.getOperatingSystem();//获取操作系统对象
        OprLog oprLog = new OprLog();
        oprLog.setTerminal(getDeviceInfo(request));
        oprLog.setLocation(getCityInfo(getIpAddress(request)));
        oprLog.setEquipmentType(os.getDeviceType().toString());
        oprLog.setOprSystem(os.getName());
        oprLog.setBrowerName(browser.getName());
        oprLog.setBrowerVersion(browser.getVersion(userAgent).toString());
        oprLog.setBrowerEngine(browser.getRenderingEngine().toString());
        oprLog.setUserAgent(userAgent);
        oprLog.setIpAddress(getIpAddress(request));
        oprLog.setOprTime(new Date());
        return oprLog;
    }

    public static OprLog getRequestInfo(String ipAddress) {
        OprLog oprLog = new OprLog();
        oprLog.setTerminal("websocket");
        oprLog.setLocation(getCityInfo(ipAddress));
        oprLog.setEquipmentType("websocket");
        oprLog.setOprSystem("websocket");
        oprLog.setBrowerName("websocket");
        oprLog.setBrowerVersion("websocket");
        oprLog.setBrowerEngine("websocket");
        oprLog.setUserAgent("websocket");
        oprLog.setIpAddress(ipAddress);
        oprLog.setOprTime(new Date());
        return oprLog;
    }


    /**
     * 获取请求设备信息
     * @author gaodongyang
     * @date 2020/8/11 14:19
     * @param request 请求
     * @return String 设备信息 pc端还是手机端
     **/
    private static String getDeviceInfo(HttpServletRequest request) {
        ///定义正则
        String pattern = "^Mozilla/\\d\\.\\d\\s+\\(+.+?\\)";
        String pattern2 = "\\(+.+?\\)";
        ///将给定的正则表达式编译到模式中
        Pattern r = Pattern.compile(pattern);
        Pattern r2 = Pattern.compile(pattern2);

        String userAgent = request.getHeader("User-Agent");
        ///创建匹配给定输入与此模式的匹配器
        Matcher m = r.matcher(userAgent);
        String result = null;
        if (m.find()) {
            result = m.group(0);
        }
        if(result == null){
            return null;
        }
        Matcher m2 = r2.matcher(result);
        if (m2.find()) {
            result = m2.group(0);
        }
        result = result.replace("(", "");
        result = result.replace(")", "");

        if (org.apache.commons.lang3.StringUtils.isBlank(result)) {
            return null;
        }
        result = result.replace(" U;", "");
        result = result.replace(" zh-cn;", "");

        String android = "Android";
        String iPhone = "iPhone";
        String iPad = "iPad";
        if(result.contains(android))
            return android+"端";
        if(result.contains(iPad))
            return iPad+"端";
        if(result.contains(iPhone))
            return iPhone+"端";
        return "PC"+"端";
    }





    //使用腾讯的接口通过ip拿到城市信息
    private static final String KEY = "KWOBZ-CBLR2-J4MU3-CURNN-DCXZK-IHBZR";
    public static String getCityInfo(String ip)  {
        String s = sendGet(ip, KEY);
        Map map = JSONObject.parseObject(s, Map.class);
        String message = (String) map.get("message");
        System.out.println(message);
        if("query ok".equals(message)||"Success".equals(message)){
            Map result = (Map) map.get("result");
            Map addressInfo = (Map) result.get("ad_info");
            String nation = (String) addressInfo.get("nation");
            String province = (String) addressInfo.get("province");
            String city = (String) addressInfo.get("city");
            String district = (String) addressInfo.get("district");
            String adcode = addressInfo.get("adcode").toString();
            String address = nation + "-" + province;
            if(city!=null&&city.length()>0)
                address += "-"+city;
            if(district!=null&&district.length()>0)
                address += "-"+district;
            if(adcode!=null&&adcode.length()>0)
                address += "-"+adcode;
            return address;
        }else{
            return message;
        }
    }
    //根据在腾讯位置服务上申请的key进行请求操作
    public static String sendGet(String ip, String key) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = "https://apis.map.qq.com/ws/location/v1/ip?ip="+ip+"&key="+key;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (Map.Entry entry : map.entrySet()) {
//                System.out.println(key + "--->" + entry);
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 获取客户端的真实IP地址
     *
     * @param request
     * @return
     */
    public static String getClientRealIp(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        // "***.***.***.***".length()
        if (ipAddress != null && ipAddress.length() > 15) {
            // = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }


    /**
     * @param
     * @todo 获取外网ip
     * @author xiaotao
     */
    public static String getOuterNetIp() {
        String result = "";
        URLConnection connection;
        BufferedReader in = null;
        try {
            URL url = new URL("http://www.icanhazip.com");
            connection = url.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "KeepAlive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.getMessage();
            }
        }
        return result;
    }

    /**
     * 获取客户端的真实主机名
     *
     * @param request
     * @return
     */
    public static String getClientHostName(HttpServletRequest request) {
        return request.getRemoteHost();
    }

    /**
     * 获取客户端的浏览器名
     *
     * @param request
     * @return
     */
    public static String getClientBrowserName(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        return browser;
    }

    /**
     * 获取客户端的浏览器
     *
     * @param request
     * @return
     */
    public static String getUserAgent(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        if (StringUtils.hasText(agent)) {
            StringTokenizer st = new StringTokenizer(agent, ";");
            String browser = st.nextToken();
            if (browser != null && browser.length() > 20) {
                browser = browser.substring(0, 20);
            }
            return browser;
        }
        return null;
    }




}
