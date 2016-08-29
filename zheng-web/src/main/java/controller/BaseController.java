package controller;

import common.JsonResult;
import exception.AuthException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * Created by XR on 2016/8/25.
 */
public abstract class BaseController {
    protected boolean isAjax(HttpServletRequest request){
        String requestheader= request.getHeader("X-Requested-With");
        if (requestheader!=null){
            return true;
        }
        return false;
    }

    protected void setCookie(HttpServletResponse response, String value)throws  Exception{
        Cookie cookie=new Cookie("curruser", URLEncoder.encode(value, "UTF-8"));
        cookie.setMaxAge(30 * 60);// 设置为30min
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    protected JsonResult jsonResult(Integer _code, String _msg){
        JsonResult jsonResult=new JsonResult(_code,_msg);
        return  jsonResult;
    }

    protected JsonResult jsonResult(Integer _code,String _msg,Object _data){
        JsonResult jsonResult=new JsonResult(_code,_msg,_data);
        return jsonResult;
    }

//    @ExceptionHandler
//    public String exp(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception ex){
//        if (ex instanceof AuthException){
//            return null;
//        }
//        System.out.println(ex.getMessage());
//        return "404";
//    }
}
