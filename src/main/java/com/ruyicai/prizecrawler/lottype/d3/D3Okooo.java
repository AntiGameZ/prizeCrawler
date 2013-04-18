package com.ruyicai.prizecrawler.lottype.d3;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class D3Okooo implements IAgency {

	private static Logger logger = LoggerFactory.getLogger(D3Okooo.class);
	private static final String URL = "http://www.okooo.com/3d/3dkj/";


	private void buildPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Document doc) {
		Elements redball = doc.select(".red-ball").first().select("strong");
		StringBuilder red = new StringBuilder();
		
		for(Element strong:redball) {
			red.append("0"+strong.text());
		}
		
		prizeInfo.setBatchcode(batchcode);
		prizeInfo.setLotno("F47103");
		prizeInfo.setWinbasecode(red.toString());
		prizeInfo.setWinspecialcode("");
	}

	private boolean isIssueExist(String batchcode, Document doc) {
		boolean flag = false;
		Element select = doc.select("#LotteryNo").first();
		Elements options = select.select("option");
		for(Element option:options) {
			if(batchcode.equals(option.text())&&option.hasAttr("selected")) {
				flag = true;
			}
		}
		return flag;
	}
	


	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从澳客抓取开奖,彩种F47103,期号"+batchcode);
			Document doc = Jsoup.connect(URL+batchcode).timeout(3500).get();
			
			if(isIssueExist(batchcode, doc)) {
				buildPrizeInfo(batchcode, prizeInfo, doc);
			}
			
			logger.info("从澳客抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从澳客抓取第" + batchcode + "期3D开奖出错", e);
		}
		
		return prizeInfo;
	}
	
	public static void main(String[] args) {
		System.out.println(new D3Okooo().crawlFromAgency("F47103", "2012351").toString());
	}

}
