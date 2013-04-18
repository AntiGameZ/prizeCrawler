package com.ruyicai.prizecrawler.jingcaiguanyajun;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.consts.JingcaiState;
import com.ruyicai.prizecrawler.domain.TjingcaiGYJMatch;
import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;

import flexjson.JSONDeserializer;

@Service
public class GuanYaJunOlympicService {

	private Logger logger = LoggerFactory.getLogger(GuanYaJunOlympicService.class);
	
	Map<String, String> map1=new HashMap<String, String>();
	Map<String, String> map2=new HashMap<String, String>();
	{
		map1.put("02", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=fb&p=130978&round=0.2082186416248053");
		map1.put("03", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=fb&p=130980&round=0.29105999680001293");
		map1.put("04", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=bk&p=130990&round=0.8817780922907246");
		map1.put("05", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=bk&p=130992&round=0.4620749633756974");
		
		map2.put("02", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=fb&p=130979&round=0.11872076997818659");
		map2.put("03", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=fb&p=130981&round=0.012912264502030757");
		map2.put("04", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=bk&p=130991&round=0.5921551698899715");
		map2.put("05", "http://info.sporttery.cn/interface/interface.php?action=champion_list&s=bk&p=130993&round=0.3616715047198722");
	}

	@Autowired
	private TjingcaiDao jingcaidao;
	
	
	public void olympicCal() {
		logger.info("开始初始化奥运赛事");
		
		logger.info("开始初始化奥运冠军赛事");
		for (Iterator<String> iterator = map1.keySet().iterator(); iterator.hasNext();) {
			String saishi = (String) iterator.next();
			calGYJ(map1.get(saishi), saishi, BigDecimal.ZERO);
		}
		logger.info("初始化奥运冠军赛事结束");
		
		logger.info("开始初始化奥运冠亚军赛事");
		for (Iterator<String> iterator = map2.keySet().iterator(); iterator.hasNext();) {
			String saishi = (String) iterator.next();
			calGYJ(map2.get(saishi), saishi, BigDecimal.ONE);
		}
		logger.info("开始初始化奥运冠亚军赛事结束");
	}


	/**
	 * 
	 * @param url 获取地址
	 * @param saishi 赛事编号
	 * @param type 0冠军 1冠亚军
	 */
	private void calGYJ(String url,String saishi,BigDecimal type) {

		List<TjingcaiGYJMatch> matches = buildMatches(url,saishi,type);
		
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

	
	private List<TjingcaiGYJMatch> buildMatches(String url,String saishi,BigDecimal type) {
		logger.info("开始抓取竞彩冠亚军对阵");
		try {
			Document d = Jsoup.connect(url).timeout(5000).get();
			String jsonStr = d.getElementsByTag("body").text();
			JSONDeserializer<Map<String, String>> jsondes = new JSONDeserializer<Map<String, String>>();
			Map<String, String> map = jsondes.deserialize(jsonStr);

			List<TjingcaiGYJMatch> matches = new ArrayList<TjingcaiGYJMatch>();
			String[] datas = map.get("data").split("\\|");
			for (String data : datas) {
				String[] fields = data.split("\\-");
				TjingcaiGYJMatch match = new TjingcaiGYJMatch();
				match.setSaishi(saishi);
				match.setType(type);
				match.setNumber(Integer.valueOf(fields[0]) >= 10 ? fields[0]
						: "0" + fields[0]);
				match.setTeam(fields[1]);
				match.setState(fields[2].contains("开售") ? JingcaiState.Normal.value
						: JingcaiState.END.value);
				match.setAward(fields[3]);
				match.setPopularityRating(fields[4]);
				match.setProbability(fields[5]);
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
		
		logger.info("开始初始化奥运赛事");
		
		logger.info("开始初始化奥运冠军赛事");
		for (Iterator<String> iterator = map1.keySet().iterator(); iterator.hasNext();) {
			String saishi = (String) iterator.next();
			List<TjingcaiGYJMatch> matches = buildMatches(map1.get(saishi), saishi, BigDecimal.ZERO);
			for(TjingcaiGYJMatch match:matches) {
				jingcaidao.persist(match);
			}
		}
		logger.info("初始化奥运冠军赛事结束");
		
		logger.info("开始初始化奥运冠亚军赛事");
		for (Iterator<String> iterator = map2.keySet().iterator(); iterator.hasNext();) {
			String saishi = (String) iterator.next();
			List<TjingcaiGYJMatch> matches = buildMatches(map2.get(saishi), saishi, BigDecimal.ONE);
			for(TjingcaiGYJMatch match:matches) {
				jingcaidao.persist(match);
			}
		}
		logger.info("开始初始化奥运冠亚军赛事结束");
	}

	
	public static void main(String[] args) {
		new GuanYaJunOlympicService().saveMatches();
	}
}
