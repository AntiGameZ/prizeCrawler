package com.ruyicai.prizecrawler.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.beidan.TBeidanScore;
import com.ruyicai.prizecrawler.beidan.dao.TBeidanDao;
import com.ruyicai.prizecrawler.consts.JingcaiState;
import com.ruyicai.prizecrawler.producer.BeidanResultProducer;
import com.ruyicai.prizecrawler.util.HttpTookit;
import com.ruyicai.prizecrawler.util.JsonUtil;
import com.ruyicai.prizecrawler.util.SendSMS;

@Service
public class TbeidanScoreService {

	private static Logger logger = LoggerFactory
			.getLogger(TbeidanScoreService.class);

	@Autowired
	private TBeidanDao beidandao;

	@Autowired
	private SendSMS sendSMS;
	
	@Autowired
	private SystemConfigService sysconfigService;
	
	@Autowired
	private BeidanResultProducer beidanResultProducer;
	
	@Value("${url.dataanalysis}")
	private String dataanalysisUrl;
	
	@Value("${msg.station}")
	private String msgstation;

	public void findAndPersist(String id) {
		TBeidanScore score = beidandao.findTBeidanScore(id);
		if (score != null) {
			logger.info("北单比分id={}已经存在,内容为{}",
					new String[] { id, score.toString() });
			return;
		}
		
		score = getTbeidanScore(id);
		if(score!=null) {
			logger.info("保存北单比分score={}",score.toString());
			if(sysconfigService.getBoolean("beidan.scoreopen")) {
				logger.info("北单比分审核状态为开,保存并发送短信score={}",score.getId());
				beidandao.persist(score);
				sendSMS.sendSMS(msgstation+":"+id+"北单比赛结束,有未审核的比分");
			}else {
				logger.info("北单比分审核状态为关,自动审核并保存score={}",score.getId());
				score.setAudit(JingcaiState.AUDIT.value);
				score.setAuditname("system");
				score.setAudittime(new Date());
				beidandao.persist(score);
			}
			String batchcode = id.split("\\_")[0];
			String no = id.split("\\_")[1];
			beidanResultProducer.send("B00001",batchcode,no);
			beidanResultProducer.send("B00002",batchcode,no);
			beidanResultProducer.send("B00003",batchcode,no);
			beidanResultProducer.send("B00004",batchcode,no);
			beidanResultProducer.send("B00005",batchcode,no);
		}
	}

	private TBeidanScore getTbeidanScore(String id) {

		Map<String,String> params = new HashMap<String,String>();
		params.put("event", id);
		
		String value = HttpTookit.doPost(dataanalysisUrl+"/selectBd/getScheduleByEvent", params);
		
		TBeidanScore score = null;
		
		if(value!=null) {
			Map<String, Object> jsonMap = JsonUtil.transferJson2Map(value);
			Map<String, Object> scoreMap = (Map<String, Object>) jsonMap.get("value");
			String matchState = String.valueOf(scoreMap.get("matchState"));
			if(!"-1".equals(matchState)) {
				logger.info("{} matchestate={} 不为-1,退出",new String[]{id,matchState});
				return null;
			}
			
			
			String turn = String.valueOf(scoreMap.get("bdTurn"));
			score = new TBeidanScore();
			score.setId(String.valueOf(scoreMap.get("bdEvent")));
			score.setCancel(BigDecimal.ZERO);
			

			score.setResult(String.valueOf(scoreMap.get("homeScore"))+":"+String.valueOf(scoreMap.get("guestScore")));
			score.setFirsthalfresult(String.valueOf(scoreMap.get("homeHalfScore"))+":"+String.valueOf(scoreMap.get("guestHalfScore")));
			if("1".equals(turn)) {
				score.setResult(String.valueOf(scoreMap.get("guestScore"))+":"+String.valueOf(scoreMap.get("homeScore")));
				score.setFirsthalfresult(String.valueOf(scoreMap.get("guestHalfScore"))+":"+String.valueOf(scoreMap.get("homeHalfScore")));
			}
			score.setCreatetime(new Date());
			score.setAudittime(null);
			score.setAuditname("");
			score.setAudit(JingcaiState.NOTAUDIT.value);
		}
		return score;
	}

}
