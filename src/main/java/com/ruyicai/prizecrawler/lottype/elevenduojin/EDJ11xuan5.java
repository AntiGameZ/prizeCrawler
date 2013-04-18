package com.ruyicai.prizecrawler.lottype.elevenduojin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
//暂时不可用
public class EDJ11xuan5 {

	private static Logger logger = LoggerFactory.getLogger(EDJ11xuan5.class);
	private static final String URL = "http://zoushi.11xuan5.cn/sd/haoma/78.aspx";
	
	public PrizeInfo carwlFrom11xuan5ByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从11xuan5抓取T01012开奖:期号"+batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9).timeout(4000).get();
			Element bnBody = doc.select("#bnBody").first();
			Elements trs = bnBody.select("tr");
			
			for(Element tr:trs) {
				createPrizeInfo(batchcode, prizeInfo, tr);
			}
			
			logger.info("从11xuan5抓取T01012开奖结束:期号"+batchcode+"prizeInfo:"+prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从11xuan5抓取T01012开奖出错:期号"+batchcode, e);
		}
		
		return prizeInfo;
	}

	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Element tr) {
		if(("20"+batchcode).equals(tr.select("td").get(0).text().replace("-", ""))) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01012");
			String wincode = tr.select("td").get(1).text().replace(",", " ");
			prizeInfo.setWinbasecode(wincode);
			prizeInfo.setWinspecialcode("");
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new EDJ11xuan5().carwlFrom11xuan5ByBatchcode("12090539"));
	}

}
