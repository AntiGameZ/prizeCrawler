package com.ruyicai.prizecrawler.lottype.duolecai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class DLC500wan {

	private static Logger logger = LoggerFactory.getLogger(DLC500wan.class);
	private static final String URL = "http://kaijiang.500.com/static/public/dlc/xml/historyopen.xml";
	
	
	public PrizeInfo carwlFrom500wanByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从500万抓取T01010开奖:期号"+batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9).timeout(4000).get();
			Elements rows = doc.select("body > xml > row");
			for(Element row:rows) {
				createPrize(batchcode, prizeInfo, row);
			}
			logger.info("从500万抓取T01010开奖结束:期号"+batchcode+"prizeInfo:"+prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从500万抓取T01010开奖出错:期号"+batchcode, e);
		}
		
		return prizeInfo;
	}

	private void createPrize(String batchcode, PrizeInfo prizeInfo,
			Element row) {
		if(batchcode.equals("20"+row.attr("expect"))) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01010");
			prizeInfo.setWinbasecode(row.attr("opencode").replace(",", " "));
			prizeInfo.setWinspecialcode("");
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new DLC500wan().carwlFrom500wanByBatchcode("2013011627"));
	}

}
