package com.ruyicai.prizecrawler.jingcaiguanyajun;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.domain.TjingcaiGYJMatch;
import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;

import flexjson.JSONDeserializer;

@Service
public class GuanYaJun01Service {

	private Logger logger = LoggerFactory.getLogger(GuanYaJun01Service.class);

	@Autowired
	private TjingcaiDao jingcaidao;

	private static final String EURO = "http://info.sporttery.cn/interface/interface.php?action=euro_list2012&round=0.7082738105505352";

	public void calGYJ01() {

		List<TjingcaiGYJMatch> matches = buildMatches();
		
		logger.info("开始比较竞彩冠亚军对阵");
		for (TjingcaiGYJMatch match : matches) {
			TjingcaiGYJMatch oldMatch = jingcaidao.getGYJMatche(
					match.getSaishi(), match.getType(), match.getNumber());
			if (!oldMatch.equals(match)) {
				logger.info("新抓取对阵与原来对阵不相等。新对阵:"+match.toString()+"老对阵:"+oldMatch.toString());
				if (!oldMatch.getTeam().equals(match.getTeam())) {
					logger.info("竞彩冠亚军对阵发生更改");
					throw new RuntimeException("竞彩冠亚军对阵发生更改");
				}
				logger.info("开始更新冠亚军对阵");
				jingcaidao.updateTjingcaiGYCMatches(match.getSaishi(),
						match.getType(), match.getNumber(), match.getState(),
						match.getAward(), match.getPopularityRating(),
						match.getProbability(),oldMatch.getId());
				logger.info("冠亚军对阵更新成功");
			}else {
				logger.info("本次抓取的对阵和数据库中对阵没有改变，不更新。新对阵:"+match.toString()+"老对阵:"+oldMatch.toString());
			}
			

		}
	}

	
	private List<TjingcaiGYJMatch> buildMatches() {
		logger.info("开始抓取竞彩冠亚军对阵");
		try {
			Document d = Jsoup.connect(EURO).timeout(5000).get();
			String jsonStr = d.getElementsByTag("body").text();
			JSONDeserializer<Map<String, String>> jsondes = new JSONDeserializer<Map<String, String>>();
			Map<String, String> map = jsondes.deserialize(jsonStr);

			List<TjingcaiGYJMatch> matches = new ArrayList<TjingcaiGYJMatch>();
			String[] champions = map.get("champion").split("\\|");
			for (String champion : champions) {
				String[] fields = champion.split("\\-");
				TjingcaiGYJMatch match = new TjingcaiGYJMatch();
				match.setSaishi("01");
				match.setType(BigDecimal.ZERO);
				match.setNumber(Integer.valueOf(fields[0]) >= 10 ? fields[0]
						: "0" + fields[0]);
				match.setTeam(fields[1]);
//				match.setState(fields[2].contains("开售") ? JingcaiState.Normal.value
//						: JingcaiState.END.value);
				match.setAward(fields[3]);
				match.setPopularityRating(fields[4]);
				match.setProbability(fields[5]);
				matches.add(match);
			}

			String[] seconds = map.get("second").split("\\|");
			for (String second : seconds) {
				String[] fields = second.split("\\-");
				TjingcaiGYJMatch match = new TjingcaiGYJMatch();
				match.setSaishi("01");
				match.setType(BigDecimal.ONE);
				match.setNumber(Integer.valueOf(fields[0]) >= 10 ? fields[0]
						: "0" + fields[0]);
				match.setTeam(fields[1]);
//				match.setState(fields[6].contains("开售") ? JingcaiState.Normal.value
//						: JingcaiState.END.value);
				match.setAward(fields[2]);
				match.setPopularityRating(fields[3]);
				match.setProbability(fields[4]);
				matches.add(match);
			}
			logger.info("开始抓取竞彩冠亚军对阵成功");
			return matches;
		} catch (Exception e) {
			logger.info("开始抓取竞彩冠亚军对阵出错");
			return null;
		}

	}

	
	public void saveMatches() {
		List<TjingcaiGYJMatch> matches = buildMatches();
		for (TjingcaiGYJMatch match : matches) {
			jingcaidao.persist(match);
		}
	}

}
