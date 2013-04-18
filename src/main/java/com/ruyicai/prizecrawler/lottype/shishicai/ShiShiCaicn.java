package com.ruyicai.prizecrawler.lottype.shishicai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.util.HttpTookit;

import flexjson.JSONDeserializer;

public class ShiShiCaicn {
	

	private static Logger logger = LoggerFactory.getLogger(ShiShiCaicn.class);
	private static final String URLCookie = "http://data.shishicai.cn/cqssc/haoma/";
	private static final String URL = "http://data.shishicai.cn/handler/kuaikai/data.ashx";
	

	
	
	public PrizeInfo carwlFromFromShiShiCaicnByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从shishicai.cn抓取开奖,彩种T01007,期号"+batchcode);
			String cookie = HttpTookit.getWebSiteCookie(URLCookie);
			Map<String,String> params = new HashMap<String, String>();
			params.put("date", "0001-01-01");
			params.put("lottery", "4");
			
			Map<String,String> requestProperties = new HashMap<String, String>();
			requestProperties.put("Referer", "http://data.shishicai.cn/cqssc/haoma/");
			requestProperties.put("Cookie", cookie);
			
			String res = HttpTookit.POST(URL, params, requestProperties,"UTF-8");
			
			JSONDeserializer<List<String>> des = new JSONDeserializer<List<String>>();
			
			List<String> opencode = des.deserialize(res);
			
			Map<String, String> opencodeMap = convertOpencodes(opencode);
			if(opencodeMap.containsKey(batchcode)) {
				prizeInfo.setBatchcode(batchcode);
				prizeInfo.setLotno("T01007");
				prizeInfo.setWinbasecode(opencodeMap.get(batchcode));
				prizeInfo.setWinspecialcode("");
			}

			logger.info("从shishicai.cn抓取开奖结束:"+prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从shishicai.cn抓取第"+batchcode+"期时时彩最新开奖出错", e);
		}

		return prizeInfo;
	}
	
	
	private Map<String, String> convertOpencodes(List<String> opencodes) {
		Map<String,String> opencodeMap = new HashMap<String,String>();
		for(String code:opencodes) {
			String[] codes = code.split(";");
			opencodeMap.put(codes[0].replace("-", ""),codes[1]);
		}
		return opencodeMap;
	}
	
	
	public static void main(String[] args) {
		System.out.println(new ShiShiCaicn().carwlFromFromShiShiCaicnByBatchcode("20130404093"));
	}

}
