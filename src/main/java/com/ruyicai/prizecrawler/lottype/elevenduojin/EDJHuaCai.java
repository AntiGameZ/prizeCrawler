package com.ruyicai.prizecrawler.lottype.elevenduojin;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class EDJHuaCai {

	private static Logger logger = LoggerFactory.getLogger(EDJHuaCai.class);
	private static final String URL = "http://www.huacai.com/kaijiang/history.do?item=gaopin&lotteryid=150";
	
	
	public PrizeInfo carwlFromHuaCaiByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从华彩抓取T01012开奖:期号"+batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9).timeout(4000).get();
			Element prize_info = doc.select(".prize_info").first();
			Element table = prize_info.select("table").get(1);
			Elements trs = table.select("tbody > tr");
			
			for(Element tr:trs) {
				createPrizeInfo(batchcode,prizeInfo,tr);
			}
			
			logger.info("从华彩抓取T01012开奖结束:期号"+batchcode+"prizeInfo:"+prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从华彩抓取T01012开奖出错:期号"+batchcode, e);
		}
		
		return prizeInfo;
	}



	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Element tr) {
		Elements tds = tr.select("td");
		if(tds!=null&&tds.size()>0&&batchcode.equals(tds.first().text())) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setCreatedate(new Date());
			prizeInfo.setLotno("T01012");
			StringBuilder wincode = spliceWincode(tds);
			prizeInfo.setWinbasecode(wincode.toString());
			prizeInfo.setWinspecialcode("");
		}
	}



	private StringBuilder spliceWincode(Elements tds) {
		StringBuilder wincode = new StringBuilder();
		
		Elements lis = tds.get(2).select("ul > li");
		for(Element li:lis) {
			wincode.append(li.text()).append(" ");
		}
		if(wincode.toString().endsWith(" ")) {
			wincode.deleteCharAt(wincode.length()-1);
		}
		return wincode;
	}
	
	
	public static void main(String[] args) {
		System.out.println(new EDJHuaCai().carwlFromHuaCaiByBatchcode("12103037"));
	}


}
