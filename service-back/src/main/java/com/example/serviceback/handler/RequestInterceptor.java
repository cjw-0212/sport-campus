package com.example.serviceback.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.serviceback.util.JwtUtils;
import com.example.serviceback.util.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CJW
 * @since 2024/3/23
 */
@Component
public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求头中的token
        String token = request.getHeader("token");
        if (token == null) {
            Result<Void> result = Result.error("请先登录");
            String json = JSONObject.toJSONString(result);
            //设置响应头，响应数据为json类型，数据编码为utf-8
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            //不放行请求
            return false;
        }
        //token存在，对其进行解析
        try {
            JwtUtils.parseJwt(token);
        } catch (Exception e) {
            Result<Void> result = Result.error("请先登录");
            String json = JSONObject.toJSONString(result);
            //设置响应头，响应数据为json类型，数据编码为utf-8
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            //不放行请求
            return false;
        }
        //token存在且解析成功，放行该请求
        return true;
    }
}
