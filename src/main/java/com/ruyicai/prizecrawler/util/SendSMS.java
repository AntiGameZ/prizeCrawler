package com.ruyicai.prizecrawler.util;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendSMS {
	
	private Logger logger = LoggerFactory.getLogger(SendSMS.class);

	@Value("${sendsmsurl}")
	private String url;
	
	@Value("${sendsmsmobiles}")
	private String mobiles;
	
	@Autowired
	private HttpUtil httpUtil;
	
	public String matchesMsg = "有未审核场次"; 
	
	public String resultMsg = "有未审核赛果";
	
	public String beidanMatchMsg = "北京单场有未审核场次";
	
	public String beidanResultMsg = "北京单场有未审核赛果";
	
	public void sendSMS(String msg) {
		logger.info("发送短信, mobiles:{},msg:{}", new String[] {mobiles, msg});
		if(!StringUtil.isEmpty(mobiles)) {
			try {
				String result = httpUtil.getResponse(url, HttpUtil.POST, HttpUtil.UTF8, buildMobiles() + "text=" + URLEncoder.encode(msg, HttpUtil.UTF8));
				logger.info("发送短信返回, result:{},mobiles:{},msg:{}", new String[] {result, mobiles, msg});
			} catch (Throwable e) {
				logger.info("发送短信出错, mobiles:{},msg:{}", new String[] {mobiles, msg});
			}
		}
	}
	
	public String buildMobiles() {
		StringBuilder builder = new StringBuilder();
		for(String mobile : mobiles.split("\\,")) {
			builder.append("mobileIds=").append(mobile).append("&");
		}
		return builder.toString();
	}
}
