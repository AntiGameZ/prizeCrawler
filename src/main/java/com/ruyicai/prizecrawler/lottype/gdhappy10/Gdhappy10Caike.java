package com.ruyicai.prizecrawler.lottype.gdhappy10;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.util.HttpTookit;

import flexjson.JSONDeserializer;

public class Gdhappy10Caike {

	private static Logger logger = LoggerFactory.getLogger(Gdhappy10Caike.class);
	private static final String URL = "http://www.310win.com/Info/Result/High.aspx";
	
	public PrizeInfo carwlFromHappy10Caike(String batchcode){
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从caike抓取快乐十分开奖，期号"+batchcode);
			String queryString = "load=ajax&typeID=116&date="+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"&randomT-_-"+"0."+System.nanoTime();
			String res = HttpTookit.doGet(URL, queryString);
			JSONDeserializer<Map<String,List<Map<String,String>>>> des = new JSONDeserializer<Map<String,List<Map<String,String>>>>();
			
			Map<String, List<Map<String,String>>> deserialize = des.deserialize(res);
			
			List<Map<String,String>> list = deserialize.get("Table");
			
			for(Map<String,String> map:list) {
				if(batchcode.equals(map.get("IssueNum"))) {
					prizeInfo.setBatchcode(batchcode);
					prizeInfo.setLotno("T01015");
					prizeInfo.setWinbasecode(map.get("Result"));
					prizeInfo.setWinspecialcode("");
				}
			}
			logger.info("从caike抓取T01015开奖结束:期号" + batchcode + "prizeInfo:"
					+ prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("开始从caike抓取快乐十分开奖出错，期号"+batchcode,e);
		}
		return prizeInfo;
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		System.out.println(new Gdhappy10Caike().carwlFromHappy10Caike("2013060618"));
	}
}
