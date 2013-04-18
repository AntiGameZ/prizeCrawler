package com.ruyicai.prizecrawler.lottype.elevenduojin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class EDJ360 {

	private static Logger logger = LoggerFactory.getLogger(EDJ360.class);
	private static final String URL = "http://cp.360.cn/kaijiang?LotID=166406";
	
	public PrizeInfo carwlFrom360ByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从360抓取T01012开奖:期号" + batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9)
					.timeout(4000).get();
			Element conKjlist = doc.select("#conKjlist").first();
			Elements trs = conKjlist.select("table>tbody>tr");
			for (int i=1;i<trs.size();i++) {
				createPrizeInfo(batchcode, prizeInfo, trs.get(i));
			}
			logger.info("从360抓取T01012开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从360抓取T01012开奖出错:期号" + batchcode, e);
		}
		return prizeInfo;
	}
	
	
	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Element tr) {
		if (("20"+batchcode).equals(tr.select("td").get(0).text().trim())) {
			prizeInfo.setBatchcode(batchcode);
			prizeInfo.setLotno("T01012");
			Elements spans = tr.select("td").get(1).select("div>span");
			String wincode = spans.get(0).text().trim()+" "+spans.get(1).text().trim()+" "+spans.get(2).text().trim()+" "+spans.get(3).text().trim()+" "+spans.get(4).text().trim();
			prizeInfo.setWinbasecode(wincode);
			prizeInfo.setWinspecialcode("");
		}
	}

	public static void main(String[] args) {
		System.out.println(new EDJ360().carwlFrom360ByBatchcode("12103037"));
	}
}
