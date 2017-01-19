package controller;

import annotation.Auth;
import common.JsonResult;
import common.utility.DateUtility;
import domain.model.Job.Job;
import domain.model.Job.JobLog;
import domain.model.Job.query.JobLogQuery;
import domain.model.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import spi.job.JobSPI;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/1/18.
 */
@Controller
public class JobController extends BaseController {
    @Resource
    private JobSPI jobSPIService;

    @Auth(rule = "/job/index")
    @RequestMapping(value = "/job/index")
    public String index(Model model,HttpSession httpSession){
        List<Job> jobs= jobSPIService.queryList();
        model.addAttribute("jobs",jobs);
        model.addAttribute("user",getAuthUser(httpSession));
        return "/job/index";
    }

    @Auth(rule = "/job/add")
    @RequestMapping(value = "/job/add")
    public String add(){
        return "/job/add";
    }

    @Auth(rule ="/job/add")
    @ResponseBody
    @RequestMapping(value = "/job/adding")
    public JsonResult adding(Job job){
        job.setCreateTime(new Date());
        try {
            jobSPIService.insertJob(job);
        } catch (Exception e) {
            return jsonResult(-1,"新增失败");
        }
        return jsonResult(1,"新增成功");
    }

    @Auth(rule ="/job/log")
    @RequestMapping(value = "/job/log")
    public String log(Model model,String queryDate,String startTime,String endTime,HttpSession httpSession){
        JobLogQuery query =new JobLogQuery();
        query.setQueryDate(DateUtility.getDateFromStr(queryDate,"yyyy-MM-dd"));
        if (StringUtils.isNotEmpty(startTime)){
            query.setStartTime(DateUtility.getDateFromStr(queryDate +" "+ startTime,"yyyy-MM-dd hh:mm:ss"));
        }
        if (StringUtils.isNotEmpty(endTime)){
            query.setEndTime(DateUtility.getDateFromStr(queryDate +" "+ endTime,"yyyy-MM-dd hh:mm:ss"));
        }
        PageModel<JobLog> logs=jobSPIService.queryPageJobLog(query);
        model.addAttribute("queryDate",queryDate);
        model.addAttribute("startTime",startTime);
        model.addAttribute("endTime",endTime);
        model.addAttribute("logs",logs);
        model.addAttribute("user",getAuthUser(httpSession));
        return "/job/log";
    }
}