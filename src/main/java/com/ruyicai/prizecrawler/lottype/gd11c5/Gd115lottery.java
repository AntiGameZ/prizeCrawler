package com.ruyicai.prizecrawler.lottype.gd11c5;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class Gd115lottery {

	private static Logger logger = LoggerFactory.getLogger(Gd115lottery.class);
	private static final String URL = "http://www.gdlottery.cn/zst11xuan5.jspx";

	public PrizeInfo carwlFromLotteryByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从官方抓取T01014开奖:期号" + batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9)
					.timeout(6000).get();
			Element table = doc.select("table").get(1);

			Elements trs = table.select("tbody>tr");

			for (Element tr : trs) {
				createPrizeInfo(batchcode, prizeInfo, tr);
			}

			logger.info("从官方抓取T01014开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从官方抓取T01014开奖出错:期号" + batchcode, e);
		}
		return prizeInfo;

	}

	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Element tr) {
		if (tr.select("td").size() != 0
				&& batchcode.equals("20"
						+ tr.select("td").get(0).text().trim())) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01014");
			String wincode = tr.select("td").get(1).select("span").text()
					.replace("，", " ").replace(",", " ");
			prizeInfo.setWinbasecode(wincode);
			prizeInfo.setWinspecialcode("");
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new Gd115lottery().carwlFromLotteryByBatchcode("2013011605"));
	}

}
