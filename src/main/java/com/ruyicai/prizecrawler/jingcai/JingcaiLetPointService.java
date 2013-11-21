package com.ruyicai.prizecrawler.jingcai;

import java.math.BigDecimal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.domain.Tjingcaimatches;
import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;

@Service
public class JingcaiLetPointService {
	
	private Logger logger = LoggerFactory.getLogger(JingcaiLetPointService.class);

	@Autowired
	private JingcaiPeiluService peiluservice;
	
	@Autowired
	private TjingcaiDao dao;
	
	public void updateFootBallLetPointService() {
		logger.info("开始更新足球让球");
		org.dom4j.Document football = peiluservice.getJingcaipeilu("1", "0");
		
		if(football==null) {
			logger.info("足球赛事赔率为空,退出...");
			return;
		}
		
		logger.info("获取足球赛事赔率成功");
		
		Document soapDocument = Jsoup.parse(football.asXML());
		
		logger.info("开始获取足球赛事每场次赔率");
		Elements items = soapDocument.select("matchList>item");
		logger.info("获取足球赛事每场次赔率成功");
		for(Element item:items) {
			String id = item.select("id").text();
			String letpoint = item.select("letVs").get(0).select("letPoint").text();
			if(id.trim().equals("")||letpoint.trim().equals("")) {
				continue;
			}
			
			BigDecimal type = BigDecimal.ONE;
			String day = id.split("\\_")[0];
			BigDecimal weekid = new BigDecimal(id.split("\\_")[1]);
			String teamid = id.split("\\_")[2];
			
			Tjingcaimatches match = dao.findTjingcaimatches(type, day, weekid, teamid);
			logger.info("足球场次{}原始让球为{},本次让球为{}",new String[]{id,match==null?"null":match.getLetpoint(),letpoint});
			
			if(match==null) {
				logger.info("足球对阵{}为空,不进行操作",new String[]{id});
			}else if(match.getLetpoint()==null||(!match.getLetpoint().equals(letpoint))) {
				logger.info("足球场次{}两次让球不等,更新足彩场次让球",new String[]{id});
				dao.updateLetPoint(type, day, weekid, teamid, letpoint);
			}else {
				logger.info("足球场次{}两次让球相同,未发生变化，不进行操作");
			}
		}
	}
	
	
	
	public void updateBasketBallLetPointService() {
		
		logger.info("开始更新篮球让分");
		org.dom4j.Document basketball = peiluservice.getJingcaipeilu("0", "1");
		
		if(basketball==null) {
			logger.info("篮球赛事赔率为空,退出...");
			return;
		}
		
		logger.info("获取篮球赛事赔率成功");
		Document soapDocument = Jsoup.parse(basketball.asXML());
		
		Elements items = soapDocument.select("matchList>item");
		
		for(Element item:items) {
			String id = item.select("id").text();
			String letpoint = item.select("letVs").get(0).select("letPoint").text();
			if(id.trim().equals("")||letpoint.trim().equals("")) {
				continue;
			}
			
			BigDecimal type = BigDecimal.ZERO;
			String day = id.split("\\_")[0];
			BigDecimal weekid = new BigDecimal(id.split("\\_")[1]);
			String teamid = id.split("\\_")[2];
			
			Tjingcaimatches match = dao.findTjingcaimatches(type, day, weekid, teamid);
			logger.info("篮球场次{}原始让分为{},本次让分为{}",new String[]{id,match==null?"null":match.getLetpoint(),letpoint});
			if(match==null) {
				logger.info("篮球对阵{}原始为空,不进行操作",new String[]{id});
			}else if(match.getLetpoint()==null||(!match.getLetpoint().equals(letpoint))) {
				logger.info("篮球场次{}两次让分不等,更新篮球场次让分",new String[]{id});
				dao.updateLetPoint(type, day, weekid, teamid, letpoint);
			}else {
				logger.info("篮球场次{}两次让分相同,未发生变化，不进行操作");
			}
		}
	}
}
