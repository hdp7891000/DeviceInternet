package com.zlgmcu;

/**
 * Created by yfm on 17-3-29.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class APPAPIController{

    static HashMap<String, String> deviceStatus = new HashMap<>();

    @RequestMapping(value = "/api/{productName}/{functionName}")
    public void api(@PathVariable String productName,
                    @PathVariable String functionName,
                    HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("http connect in "+new Date().toString());

        HashMap<String, Object> resultMap = new HashMap<>();

        try {
            // 获取参数信息
            Map<String, String> parameters = convertHttpParamMap(request.getParameterMap());
            String active = parameters.get("active");
            if (active.compareTo("put") == 0) {
                // http://127.0.0.1:9090/api/a7/control?active=put&key=led&value=no
                String key = parameters.get("key");
                String value = parameters.get("value");
                deviceStatus.put(key, value);
                resultMap.put("key", key);
                resultMap.put("value", value);
                resultMap.put("errorCode", "0");
            }else if(active.compareTo("get") == 0) {
                // http://127.0.0.1:9090/api/a7/control?active=get&key=led
                String key = parameters.get("key");
                String value = deviceStatus.get(key);
                resultMap.put("key", key);
                resultMap.put("value", value);
                resultMap.put("errorCode", "0");
            }else {
                resultMap.put("errorCode", "1001");
                resultMap.put("errorMsg", "active不正确");
            }
        }catch (Exception e) {
            resultMap.put("errorCode", "1002");
            resultMap.put("errorMsg", "参数不正确");
        }

        response.setCharacterEncoding("UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, resultMap);

        outputStream.flush();
    }

    protected Map<String, String> convertHttpParamMap(Map map) throws UnsupportedEncodingException {
        Map<String, String> hashMap = new HashMap<String, String>();
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            Map.Entry element = (Map.Entry) iter.next();
            String key = (String) element.getKey();
            // 如果有多个值，就取最后一个
            Object t = element.getValue();
            String v = null;
            if (t instanceof String) {
                v = (String) t;
            } else {
                String[] value = (String[]) element.getValue();
                v = value[value.length - 1];
            }
            hashMap.put(key, v);
        }
        return hashMap;
    }
}
