package com.ruyicai.prizecrawler.lottype.gdhappy10;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class Gdhappy10Lehe {
	
	private static Logger logger = LoggerFactory.getLogger(Gdhappy10Lehe.class);
	private static final String URL = "http://www.lecai.com/lottery/draw/list/544";
	
	
	public PrizeInfo carwlFromHappy10Lehe(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从乐和彩抓取T01015开奖:期号" + batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9)
					.timeout(4000).get();
			Element ele = doc.select("#draw_list").first();
			Elements trs = ele.select("tbody>tr");
			for (Element tr : trs) {
				createPrizeInfo(batchcode, prizeInfo, tr);
			}
			logger.info("从乐和彩抓取T01015开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从乐和彩抓取T01015开奖出错:期号" + batchcode, e);
		}
		return prizeInfo;
	}
	
	
	
	
	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Element tr) {
		if (batchcode.equals(tr.select("td").get(1).text().trim())) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01015");
			
			Elements spans = tr.select("td").get(2).select("span>span");
			String wincode = spans.get(0).text()+" "+spans.get(1).text()+" "+spans.get(2).text()+" "
					+spans.get(3).text()+" "+spans.get(4).text()+" "+spans.get(5).text()+" "+
					spans.get(6).text()+" "+spans.get(7).text();
			
			prizeInfo.setWinbasecode(wincode);
			prizeInfo.setWinspecialcode("");
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println(new Gdhappy10Lehe().carwlFromHappy10Lehe("2012112105"));
	}
	

}
