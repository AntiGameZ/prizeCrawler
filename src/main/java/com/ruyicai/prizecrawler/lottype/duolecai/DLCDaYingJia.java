package com.ruyicai.prizecrawler.lottype.duolecai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class DLCDaYingJia {

	private static Logger logger = LoggerFactory.getLogger(DLCDaYingJia.class);
	private static final String URL = "http://11x5.cpdyj.com/staticdata/lotteryinfo/dlc/xml/opencode.xml";

	public PrizeInfo carwlFromDYJByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从大赢家抓取T01010开奖:期号" + batchcode);
			Document doc = Jsoup
					.connect(URL + "?_=" + System.currentTimeMillis()).userAgent(SystemCode.UA_IE9)
					.timeout(4000).get();
			Elements rows = doc.select("body > xml > row");
			for (Element row : rows) {
				createPrize(batchcode, prizeInfo, row);
			}
			logger.info("从大赢家抓取T01010开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从大赢家抓取T01010开奖出错:期号" + batchcode, e);
		}

		return prizeInfo;
	}

	private void createPrize(String batchcode, PrizeInfo prizeInfo, Element row) {
		if (batchcode.equals(row.attr("expect").replace("-", ""))) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01010");
			prizeInfo.setWinbasecode(row.attr("opencode"));
			prizeInfo.setWinspecialcode("");
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new DLCDaYingJia().carwlFromDYJByBatchcode("2012103041"));
	}

}
