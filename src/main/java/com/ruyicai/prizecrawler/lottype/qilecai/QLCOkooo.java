package com.ruyicai.prizecrawler.lottype.qilecai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class QLCOkooo implements IAgency{

	private static Logger logger = LoggerFactory.getLogger(QLCOkooo.class);
	private static final String URL = "http://www.okooo.com/qilecai/qlckj/";
	
	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从澳客抓取开奖,彩种F47102,期号"+batchcode);
			Document doc = Jsoup.connect(URL+batchcode).timeout(3500).get();
			
			if(isIssueExist(batchcode, doc)) {
				buildPrizeInfo(batchcode, prizeInfo, doc);
			}
			
			logger.info("从澳客抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从澳客抓取第" + batchcode + "期七乐彩开奖出错", e);
		}
		
		return prizeInfo;
	}

	private void buildPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Document doc) {
		Elements redball = doc.select(".red-ball").first().select("strong");
		Elements blueball = doc.select(".blue-ball").first().select("strong");
		StringBuilder red = new StringBuilder();
		StringBuilder blue = new StringBuilder();
		
		for(Element strong:redball) {
			red.append(strong.text());
		}
		
		for(Element strong:blueball) {
			blue.append(strong.text());
		}
		
		prizeInfo.setBatchcode(batchcode);
		prizeInfo.setLotno("F47102");
		prizeInfo.setWinbasecode(red.toString());
		prizeInfo.setWinspecialcode(blue.toString());
	}

	private boolean isIssueExist(String batchcode, Document doc) {
		boolean flag = false;
		Element select = doc.select("#LotteryNo").first();
		Elements options = select.select("option");
		for(Element option:options) {
			if(batchcode.equals(option.text())&&option.hasAttr("selected")==true) {
				flag = true;
			}
		}
		return flag;
	}
	
	
	public static void main(String[] args) {
		System.out.println(new QLCOkooo().crawlFromAgency("F47102", "2012150"));
	}

}
