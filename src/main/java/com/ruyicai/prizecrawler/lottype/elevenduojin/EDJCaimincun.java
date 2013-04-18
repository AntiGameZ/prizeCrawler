package com.ruyicai.prizecrawler.lottype.elevenduojin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.util.HttpTookit;

import flexjson.JSONDeserializer;
//暂时不可用
public class EDJCaimincun {

	private static Logger logger = LoggerFactory.getLogger(EDJCaimincun.class);
	private static final String URL1 = "http://www.caimincun.com/home/php_client/plungins/ajax_draw_notice.php?rand=";
	private static final String URL2 = "&lottery_type=22&num=10";
	
	
	public PrizeInfo carwlFromCaimincunByBatchcode(String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从彩民村抓取T01012开奖:期号"+batchcode);
			long randnum = (long) (Math.random()*1000000000000L+1000000000000L);
			String resp = HttpTookit.doGet(URL1+randnum+URL2, "");
			JSONDeserializer<HashMap<String,Object>> json = new JSONDeserializer<HashMap<String,Object>>();
			Map<String,Object> map = json.deserialize(resp);
			if((Integer)map.get("code")==0) {
				List<Map<String, Object>> inner_datas = getInnerData(map);
				createPrizeInfo(batchcode, prizeInfo, inner_datas);
			}else {
				logger.info("从彩民村请求T01012开奖，彩民村返回错误");
			}
			
			logger.info("从彩民村抓取T01012开奖结束:期号"+batchcode+"prizeInfo:"+prizeInfo.toString());
		} catch (Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从彩民村抓取T01012开奖出错:期号"+batchcode, e);
		}
		
		return prizeInfo;
	}



	@SuppressWarnings("rawtypes")
	private void createPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			List<Map<String, Object>> inner_datas) {
		for(Map inner_data:inner_datas) {
			if(batchcode.equals((String)inner_data.get("issue"))) {
				prizeInfo.setBatchcode(batchcode);
				prizeInfo.setLotno("T01012");
				prizeInfo.setWinbasecode(((String)inner_data.get("bonuscode")).replace(",", " "));
				prizeInfo.setWinspecialcode("");
			}
		}
	}



	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getInnerData(
			Map<String, Object> map) {
		Map<String,Object> outer_datas = (Map<String,Object>)map.get("data");
		List<Map<String,Object>> inner_datas = (List<Map<String,Object>>)outer_datas.get("data");
		return inner_datas;
	}


}
