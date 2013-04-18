package com.ruyicai.prizecrawler.lottype.daletou;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class DLTOkooo implements IAgency {

	private static Logger logger = LoggerFactory.getLogger(DLTOkooo.class);
	private static final String URL = "http://www.okooo.com/daletou/dltkj/";


	private static void buildPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Document doc) {
		Elements redball = doc.select(".red-ball").first().select("strong");
		Elements blueball = doc.select(".blue-ball").first().select("strong");
		StringBuilder red = new StringBuilder();
		StringBuilder blue = new StringBuilder();
		
		for(Element strong:redball) {
			red.append(strong.text()).append(" ");
		}
		
		for(Element strong:blueball) {
			blue.append(strong.text()).append(" ");
		}
		
		if(red.toString().endsWith(" ")) {
			red = red.deleteCharAt(red.length()-1);
		}
		if(blue.toString().endsWith(" ")) {
			blue = blue.deleteCharAt(blue.length()-1);
		}
		
		prizeInfo.setBatchcode(batchcode);
		prizeInfo.setLotno("T01001");
		prizeInfo.setWinbasecode(red.toString()+"+"+blue.toString());
		prizeInfo.setWinspecialcode("");
	}

	private static boolean isIssueExist(String batchcode, Document doc) {
		boolean flag = false;
		Element select = doc.select("#LotteryNo").first();
		Elements options = select.select("option");
		for(Element option:options) {
			if(batchcode.equals("20"+option.text())&&option.hasAttr("selected")) {
				flag = true;
			}
		}
		return flag;
	}
	


	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从澳客抓取开奖,彩种T01001,期号"+batchcode);
			Document doc = Jsoup.connect(URL+batchcode.substring(2)).timeout(3500).get();
			
			if(isIssueExist(batchcode, doc)) {
				buildPrizeInfo(batchcode, prizeInfo, doc);
			}
			
			logger.info("从澳客抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从澳客抓取第" + batchcode + "期大乐透开奖出错", e);
		}
		
		return prizeInfo;
	}
	
	
	public static void main(String[] args) {
		System.out.println(new DLTOkooo().crawlFromAgency("T01010","2012150"));
	}
}
