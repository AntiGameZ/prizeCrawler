package com.ruyicai.prizecrawler.lottype.gdhappy10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

import flexjson.JSONDeserializer;

public class Gdhappy10Lehe3 {

	private static Logger logger = LoggerFactory
			.getLogger(Gdhappy10Lehe3.class);
	private static final String URL = "http://www.lecai.com/lottery/draw/view/544";

	public PrizeInfo carwlFromHappy10Lehe(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从乐和彩3抓取T01015开奖:期号" + batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9)
					.timeout(4000).get();
			Elements scripts = doc.select("script");
			Element script = scripts.get(15);
			Map<String, String> opencodeMap = convertOpencodes(script
					.toString());

			if (opencodeMap.containsKey(batchcode)) {
				prizeInfo.setBatchcode(batchcode);
				prizeInfo.setLotno("T01015");
				prizeInfo.setWinbasecode(opencodeMap.get(batchcode));
				prizeInfo.setWinspecialcode("");
			}

			logger.info("从乐和彩3抓取T01015开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从乐和彩3抓取T01015开奖出错:期号" + batchcode, e);
		}
		return prizeInfo;
	}

	private Map<String, String> convertOpencodes(String script) {

		String opencode = "";
		int start = script.indexOf("latest_draw_result");
		int end = script.indexOf("latest_draw_phase");
		opencode = script.substring(start, end);

		start = opencode.indexOf("{");
		end = opencode.indexOf("}");
		opencode = opencode.substring(start, end + 1);

		String batchcode = "";
		start = script.indexOf("latest_draw_phase");
		end = script.indexOf("latest_draw_time");
		batchcode = script.substring(start, end);

		start = batchcode.indexOf("\'");
		end = batchcode.lastIndexOf("\'");
		batchcode = batchcode.substring(start + 1, end).trim();
		JSONDeserializer<Map<String, ArrayList<String>>> des = new JSONDeserializer<Map<String, ArrayList<String>>>();

		Map<String, ArrayList<String>> map = des.deserialize(opencode);
		List<String> red = map.get("red");

		Map<String, String> opencodes = new HashMap<String, String>();
		if (red.size() == 8 && batchcode.length() == 10) {
			opencodes.put(batchcode, red.get(0) + " " + red.get(1) + " " + red.get(2)
					+ " " + red.get(3) + " " + red.get(4) + " " + red.get(5)
					+ " " + red.get(6) + " " + red.get(7));
		}

		return opencodes;
	}

	public static void main(String[] args) {
		System.out.println(new Gdhappy10Lehe3()
				.carwlFromHappy10Lehe("2012112105"));
	}

}
