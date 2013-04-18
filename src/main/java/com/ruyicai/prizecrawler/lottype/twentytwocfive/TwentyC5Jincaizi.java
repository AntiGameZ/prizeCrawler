package com.ruyicai.prizecrawler.lottype.twentytwocfive;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class TwentyC5Jincaizi implements IAgency {

	private static Logger logger = LoggerFactory
			.getLogger(TwentyC5Jincaizi.class);
	private static final String URL = "http://kaijiang.kuaiwin.com/22x5/";

	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从金彩子抓取开奖,彩种T01013,期号" + batchcode);
			Document doc = Jsoup.connect(URL).timeout(3500).get();
			Element ele = doc.select("#chartsTable").first();
			Elements trs = ele.select("tr");
			for (Element tr : trs) {
				createPrizeInfo(batchcode, prizeInfo, tr);
			}

			logger.info("从金彩子抓取开奖结束:" + prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从金彩子抓取第" + batchcode + "期22选5开奖出错", e);
		}

		return prizeInfo;
	}

	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Element tr) {
		Elements tds = tr.select("td");

		if (batchcode.equals(tds.get(0).text())) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01013");
			String wincode = tds.get(1).text().trim() + " "
					+ tds.get(2).text().trim() + " " + tds.get(3).text().trim()
					+ " " + tds.get(4).text().trim() + " "
					+ tds.get(5).text().trim();
			prizeInfo.setWinbasecode(wincode);
			prizeInfo.setWinspecialcode("");
		}
	}

	public static void main(String[] args) {
		System.out.println(new TwentyC5Jincaizi().crawlFromAgency("T01013", "2012316"));
	}

}
