package com.ruyicai.prizecrawler.lottype.duolecai;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.util.HttpTookit;

import flexjson.JSONDeserializer;

public class DLCShiShiCaicn {

	private static Logger logger = LoggerFactory.getLogger(DLCShiShiCaicn.class);
	private static final String URLCookie = "http://data.shishicai.cn/jx11x5/haoma/";
	private static final String URL = "http://data.shishicai.cn/handler/kuaikai/data.ashx";
	
	
	public PrizeInfo carwlFromFromShiShiCaicnByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从shishicai.cn抓取T01010开奖:期号"+batchcode);
			String cookie = HttpTookit.getWebSiteCookie(URLCookie);
			Map<String,String> params = new HashMap<String, String>();
			params.put("date", "0001-01-01");
			params.put("lottery", "23");
			
			Map<String,String> requestProperties = new HashMap<String, String>();
			requestProperties.put("Referer", "http://data.shishicai.cn/jx11x5/haoma/");
			requestProperties.put("Cookie", cookie);
			
			String res = HttpTookit.POST(URL, params, requestProperties,"UTF-8");
			
			
			
			JSONDeserializer<List<String>> des = new JSONDeserializer<List<String>>();
			
			List<String> opencode = des.deserialize(res);
			Map<String,String> opencodeMap = convertOpencodes(opencode);
			if(opencodeMap.containsKey(batchcode)) {
				prizeInfo.setBatchcode(batchcode);
				prizeInfo.setLotno("T01010");
				prizeInfo.setWinbasecode(opencodeMap.get(batchcode));
				prizeInfo.setWinspecialcode("");
			}

			logger.info("从shishicai.cn抓取T01010开奖结束:期号"+batchcode+"prizeInfo:"+prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从shishicai.cn抓取T01010开奖出错:期号"+batchcode, e);
		}

		return prizeInfo;
	}

	
	
	private Map<String, String> convertOpencodes(List<String> opencodes) {
		Map<String,String> opencodeMap = new HashMap<String,String>();
		for(String code:opencodes) {
			String[] codes = code.split(";");
			opencodeMap.put(codes[0].split("\\-")[0]+codes[0].split("\\-")[1].substring(1),codes[1].replace(",", " "));
		}
		return opencodeMap;
	}


	
	public static void main(String[] args) throws IOException {
		System.out.println(new DLCShiShiCaicn().carwlFromFromShiShiCaicnByBatchcode("2013011727"));
	}


}
