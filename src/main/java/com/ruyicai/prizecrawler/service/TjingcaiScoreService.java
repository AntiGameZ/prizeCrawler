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

import com.ruyicai.prizecrawler.consts.JingcaiState;
import com.ruyicai.prizecrawler.domain.TjingcaiScore;
import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;
import com.ruyicai.prizecrawler.producer.JingcaiResultProducer;
import com.ruyicai.prizecrawler.util.HttpTookit;
import com.ruyicai.prizecrawler.util.JsonUtil;
import com.ruyicai.prizecrawler.util.SendSMS;

@Service
public class TjingcaiScoreService {

	private static Logger logger = LoggerFactory
			.getLogger(TjingcaiScoreService.class);

	@Autowired
	private TjingcaiDao jingcaidao;

	@Autowired
	private SendSMS sendSMS;
	
	@Autowired
	private SystemConfigService sysconfigService;
	
	@Autowired
	private JingcaiResultProducer jingcaiResultProducer;
	
	@Value("${url.dataanalysis}")
	private String dataanalysisUrl;
	
	@Value("${msg.station}")
	private String msgstation;

	public void findAndPersist(String id) {
		TjingcaiScore score = jingcaidao.findTjingcaiScore(id);
		if (score != null) {
			logger.info("竞彩比分id={}已经存在,内容为{}",
					new String[] { id, score.toString() });
			return;
		}
		
		score = getTjingcaiScore(id);
		if(score!=null) {
			logger.info("保存竞彩比分score={}",score.toString());
			if(sysconfigService.getBoolean("jingcai.scoreopen")) {
				logger.info("竞彩比分审核状态为开,保存并发送短信score={}",score.getId());
				jingcaidao.persist(score);
				sendSMS.sendSMS(msgstation+":"+id+"比赛结束,有未审核的比分");
			}else {
				logger.info("竞彩比分审核状态为关,自动审核并保存score={}",score.getId());
				score.setAudit(JingcaiState.AUDIT.value);
				score.setAuditname("system");
				score.setAudittime(new Date());
				jingcaidao.persist(score);
			}
			
			jingcaiResultProducer.send(id);
		}
	}

	private TjingcaiScore getTjingcaiScore(String id) {
		String type = id.split("\\_")[0];

		Map<String,String> params = new HashMap<String,String>();
		params.put("event", id);
		
		String value = null;
		
		if ("0".equals(type)) {
			value = HttpTookit.doPost(dataanalysisUrl+"/selectJcl/getScheduleByEvent", params);
		} else {
			value = HttpTookit.doPost(dataanalysisUrl+"/select/getScheduleByEvent", params);
		}
		
		TjingcaiScore score = null;
		
		if(value!=null) {
			Map<String, Object> jsonMap = JsonUtil.transferJson2Map(value);
			Map<String, Object> scoreMap = (Map<String, Object>) jsonMap.get("value");
			String matchState = String.valueOf(scoreMap.get("matchState"));
			if(!"-1".equals(matchState)) {
				logger.info("{} matchestate={} 不为-1,退出",new String[]{id,matchState});
				return null;
			}
			
			
			String turn = String.valueOf(scoreMap.get("turn"));
			score = new TjingcaiScore();
			score.setId(String.valueOf(scoreMap.get("event")));
			score.setCancel(BigDecimal.ZERO);
			

			score.setResult(String.valueOf(scoreMap.get("homeScore"))+":"+String.valueOf(scoreMap.get("guestScore")));
			if("1".equals(type)) {
				score.setFirsthalfresult(String.valueOf(scoreMap.get("homeHalfScore"))+":"+String.valueOf(scoreMap.get("guestHalfScore")));
			}
			if("1".equals(turn)) {
				score.setResult(String.valueOf(scoreMap.get("guestScore"))+":"+String.valueOf(scoreMap.get("homeScore")));
				if("1".equals(type)) {
					score.setFirsthalfresult(String.valueOf(scoreMap.get("guestHalfScore"))+":"+String.valueOf(scoreMap.get("homeHalfScore")));
				}
			}
			score.setCreatetime(new Date());
			score.setAudittime(null);
			score.setAuditname("");
			score.setAudit(JingcaiState.NOTAUDIT.value);
		}
		return score;
	}

}
