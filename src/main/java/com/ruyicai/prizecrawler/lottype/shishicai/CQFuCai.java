package com.ruyicai.prizecrawler.lottype.shishicai;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.util.HttpTookit;

public class CQFuCai {

	private static Logger logger = LoggerFactory.getLogger(CQFuCai.class);
	private static final String URL = "http://www.cqcp.net/ajaxhttp/game/getopennumber.aspx";

	public PrizeInfo carwlFromCQByBatchcode(String batchcode) {

		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从重庆福彩抓取开奖,彩种T01007,期号"+batchcode);
			Map<String,String> params = new HashMap<String, String>();
			params.put("iCount", "1");
			params.put("idMode", "3002");
			String res = HttpTookit.POST(URL, params, null,"GBK");
			int point = res.indexOf("<ul");
			res = res.substring(point);
			Document doc = Jsoup.parse(res);
			Elements uls = doc.select("ul");
			
			for(Element ul:uls) {
				Elements lis = ul.select("li");
				if(lis.get(0).text().trim().equals(batchcode.substring(2))) {
					prizeInfo.setBatchcode(batchcode);
					prizeInfo.setLotno("T01007");
					prizeInfo.setWinbasecode(lis.get(1).text()
							.replace("-", ""));
					prizeInfo.setWinspecialcode("");
					break;
				}
			}
			logger.info("从重庆福彩抓取开奖结束:"+prizeInfo.toString());
		} catch (Exception e) {
			logger.info("从重庆官网抓取第" + batchcode + "期时时彩开奖出错", e);
		}

		return prizeInfo;

	}
	
	public static void main(String[] args) {
		System.out.println(new CQFuCai().carwlFromCQByBatchcode("20130406058"));
	}
	
}
