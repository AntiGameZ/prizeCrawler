package com.ruyicai.prizecrawler.lottype.gd11c5;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class Gd115Jincaizi {

	private static Logger logger = LoggerFactory.getLogger(Gd115Jincaizi.class);
	private static final String URL = "http://kaijiang.kuaiwin.com/gd11x5/";

	public PrizeInfo carwlFromJincaiziByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从金彩子抓取T01014开奖:期号" + batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9)
					.timeout(4000).get();
			Element bnBody = doc.select("#chartsTable").first();
			Elements trs = bnBody.select("tr");
			for (Element tr : trs) {
				createPrizeInfo(batchcode, prizeInfo, tr);
			}
			logger.info("从金彩子抓取T01014开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从金彩子抓取T01014开奖出错:期号" + batchcode, e);
		}
		return prizeInfo;

	}

	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Element tr) {
		if (batchcode.equals(tr.select("td").get(0).text())) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01014");
			String wincode = tr.select("td").get(1).text().trim()
					.replace(",", " ");
			prizeInfo.setWinbasecode(wincode);
			prizeInfo.setWinspecialcode("");
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println(new Gd115Jincaizi().carwlFromJincaiziByBatchcode("2013011605"));
	}

}
