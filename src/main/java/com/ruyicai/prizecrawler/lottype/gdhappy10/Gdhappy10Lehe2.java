package com.ruyicai.prizecrawler.lottype.gdhappy10;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

import flexjson.JSONDeserializer;

public class Gdhappy10Lehe2 {

	private static Logger logger = LoggerFactory
			.getLogger(Gdhappy10Lehe2.class);
	private static final String URL = "http://www.lecai.com/lottery/draw/view/544";

	public PrizeInfo carwlFromHappy10Lehe(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从乐和彩2抓取T01015开奖:期号" + batchcode);
			Document doc = Jsoup.connect(URL).userAgent(SystemCode.UA_IE9)
					.timeout(4000).get();
			Elements scripts = doc.select("script");
			Element script = scripts.get(16);
			Map<String,String> opencodeMap = convertOpencodes(script.toString());
			
			if(opencodeMap.containsKey(batchcode)) {
				prizeInfo.setBatchcode(batchcode);
				prizeInfo.setLotno("T01015");
				prizeInfo.setWinbasecode(opencodeMap.get(batchcode));
				prizeInfo.setWinspecialcode("");
			}

			logger.info("从乐和彩2抓取T01015开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从乐和彩2抓取T01015开奖出错:期号" + batchcode, e);
		}
		return prizeInfo;
	}

	private Map<String, String> convertOpencodes(String script) {
		int start = script.indexOf("phaseData");
		int end = script.indexOf("result_config_arr");
		script = script.substring(start, end);

		start = script.indexOf("{");
		end = script.indexOf("}}}");
		script = script.substring(start, end + 3);

		JSONDeserializer<Map<String, Map<String, Map<String, Object>>>> des = new JSONDeserializer<Map<String, Map<String, Map<String, Object>>>>();
		Map<String, Map<String, Map<String, Object>>> codes = des
				.deserialize(script);

		Map<String, Map<String, Object>> current = codes
				.get(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

		Set<String> keyset = current.keySet();
		Map<String, String> opencodes = new HashMap<String, String>();
		for (Iterator<String> iterator = keyset.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) current.get(key)
					.get("result");
			@SuppressWarnings("unchecked")
			ArrayList<String> array = (ArrayList<String>) map.get("red");
			if (array.size() == 8) {
				opencodes.put(key,
						array.get(0) + " " + array.get(1) + " " + array.get(2)
								+ " " + array.get(3) + " " + array.get(4) + " "
								+ array.get(5) + " " + array.get(6) + " "
								+ array.get(7));
			}

		}

		

		return opencodes;
	}

	public static void main(String[] args) {
		System.out.println(new Gdhappy10Lehe2()
				.carwlFromHappy10Lehe("2013060619"));
	}

}
