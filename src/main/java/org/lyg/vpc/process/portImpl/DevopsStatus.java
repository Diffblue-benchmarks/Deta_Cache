package org.lyg.vpc.process.portImpl;
import com.google.gson.JsonObject;
import org.lyg.common.RequestLimit;
import org.lyg.common.utils.DevopsUtil;
import org.lyg.vpc.controller.port.RestCachePort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static org.lyg.common.constants.DetaDBConstant.DIGIT_60000;
import static org.lyg.common.constants.DetaDBConstant.DIGIT_ONE;
import static org.lyg.common.constants.DetaDBConstant.REST_JSON_CONFIG;

/*
 * 
 *     ����³����
 *     ����������˹��ܵ�ͨ���޸�
 * */
@RestController
public class DevopsStatus{

    @RequestLimit(count = DIGIT_ONE, time = DIGIT_60000)
    @POST
    @RequestMapping("/devopsCache")
    @Produces(REST_JSON_CONFIG)
    public String getServerInf(@QueryParam("token") String key
            , @QueryParam("email") String email
            , @QueryParam("password") String password) {
		//���� �����������״̬
		//��ȡ �ִ��߳����� �����ڴ棬����ڴ棬�����ڴ棬������cpu��С��Ӳ������
		System.out.println(email+" :  "+password);
//		String countThread= new DevopsUtil().getThreadCount()+"";
		String usedMemoryUsage=new DevopsUtil().getMemoryRatio();
//		String avaMemoryUsage="";//TODO:
//		String maxMemoryUsage="";//
//		String cpuCapacity=new DevopsUtil().getCPURatio();
//		List<String> hardDiskCap=new DevopsUtil().getDisk();
		if(email.equalsIgnoreCase("313699483@qq.com") && password.equalsIgnoreCase("Fengyue1985!")){
//
            return "request success! /r/n ���������������Ϣ";
//				    return " request success! /r/n ���������������Ϣ, �ִ��߳���:" + countThread
//							+"/r/n �����ڴ�:" + usedMemoryUsage
//							+"/r/n �����ڴ�:" + avaMemoryUsage
//							+"/r/n ����ڴ�:" + maxMemoryUsage
//							+"/r/n ������cpu��С:" + cpuCapacity
//							+"/r/n Ӳ������:" + hardDiskCap;
//
				}
		
		return "unsuccess";
    }
}