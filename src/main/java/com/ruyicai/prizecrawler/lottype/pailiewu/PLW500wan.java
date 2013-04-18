package com.ruyicai.prizecrawler.lottype.pailiewu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class PLW500wan implements IAgency{

	private static Logger logger = LoggerFactory.getLogger(PLW500wan.class);
	private static final String URL = "http://kaijiang.500.com/static/info/kaijiang/xml/plw/list.xml";
	

	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从500万抓取开奖,彩种T01011,期号"+batchcode);
			Document doc = Jsoup.connect(URL).timeout(3500).get();
			Elements eles = doc.select("body > xml > row");
			for(Element e:eles) {
				if(batchcode.equals("20"+e.attr("expect"))) {
					prizeInfo.setBatchcode(batchcode);
					prizeInfo.setLotno("T01011");
					prizeInfo.setWinbasecode(e.attr("opencode").replace(",", ""));
					prizeInfo.setWinspecialcode("");
				}
			}
			logger.info("从500万抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从五百万抓取第" + batchcode + "期排列五开奖出错", e);
		}
		
		return prizeInfo;
	}
	
	public static void main(String[] args) {
		System.out.println(new PLW500wan().crawlFromAgency("T01011", "2012351"));
	}

}
