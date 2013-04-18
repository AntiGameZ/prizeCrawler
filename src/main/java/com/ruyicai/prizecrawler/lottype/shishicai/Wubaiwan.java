package com.ruyicai.prizecrawler.lottype.shishicai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class Wubaiwan {

	private static Logger logger = LoggerFactory.getLogger(Wubaiwan.class);
	private static final String URL = "http://kaijiang.500.com/static/public/ssc/xml/qihaoxml/";

	public PrizeInfo carwlFromWubaiwanByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从500万抓取开奖,彩种T01007,期号"+batchcode);
			Document doc = Jsoup.connect(URL+batchcode.substring(0, 8)+".xml").userAgent(SystemCode.UA_IE9).get();
			Elements eles = doc.select("body > xml > row");
			for(Element e:eles) {
				if(batchcode.equals("20"+e.attr("expect"))) {
					prizeInfo.setBatchcode("20"+e.attr("expect"));
					prizeInfo.setLotno("T01007");
					prizeInfo.setWinbasecode(e.attr("opencode").replace(",", ""));
					prizeInfo.setWinspecialcode("");
				}
			}
			logger.info("从500万抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从五百万抓取第" + batchcode + "期时时彩开奖出错", e);
		}
		
		return prizeInfo;
	}
	
	
	public static void main(String[] args) {
		System.out.println(new Wubaiwan().carwlFromWubaiwanByBatchcode("20130116048"));
	}

}
