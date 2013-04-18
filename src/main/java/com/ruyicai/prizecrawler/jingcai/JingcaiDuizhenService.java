package com.ruyicai.prizecrawler.jingcai;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.consts.JingcaiState;
import com.ruyicai.prizecrawler.domain.Events;
import com.ruyicai.prizecrawler.domain.TJingcaiParam;
import com.ruyicai.prizecrawler.domain.Tjingcaimatches;
import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;
import com.ruyicai.prizecrawler.service.EventsService;
import com.ruyicai.prizecrawler.util.JsonUtil;
import com.ruyicai.prizecrawler.util.SendSMS;
import com.ruyicai.prizecrawler.util.StringUtil;

@Service
public class JingcaiDuizhenService {
	
	private Logger logger = LoggerFactory.getLogger(JingcaiDuizhenService.class);

	private static final String FOOTBALL_URL = "http://info.sporttery.com/football/match_list.php";
	
	private static final String BASKETBALL_URL = "http://info.sporttery.com/basketball/match_list.php";
	
	private static final Map<String, BigDecimal> WEEKID = new HashMap<String, BigDecimal>();
	
	private static final Map<Integer, Integer> WEEK = new HashMap<Integer, Integer>();
	
	private static final Map<String, String> LOTNOS = new HashMap<String, String>();
	
	private String issellStr = "已开售";
	
	@Value("${jingcai.beforemin}")
	public int beforeMin = 14;
	
	@Value("${jingcai.duizhenaudit}")
	public boolean jingcaiaudit;
	
	@Value("${msg.station}")
	private String msgstation;
	
	private int timeout = 30000;
	
	@Autowired
	private TjingcaiDao tjingcaiDao;
	
	@Autowired
	private JingcaiPeiluService jingcaiPeiluService;
	
	@Autowired
	private SendSMS sendSMS;
	
	@Autowired
	private EventsService eventsService;
	
	private boolean issend = false;
	
	@Value("${jingcai.biguser}")
	private boolean biguser;
	
	static {
		WEEKID.put("周一", new BigDecimal(1));
		WEEKID.put("周二", new BigDecimal(2));
		WEEKID.put("周三", new BigDecimal(3));
		WEEKID.put("周四", new BigDecimal(4));
		WEEKID.put("周五", new BigDecimal(5));
		WEEKID.put("周六", new BigDecimal(6));
		WEEKID.put("周日", new BigDecimal(7));
		
		WEEK.put(1, 2);
		WEEK.put(2, 3);
		WEEK.put(3, 4);
		WEEK.put(4, 5);
		WEEK.put(5, 6);
		WEEK.put(6, 7);
		WEEK.put(7, 1);
		
		LOTNOS.put("胜平负", "J00001");
		LOTNOS.put("比分", "J00002");
		LOTNOS.put("总进球数", "J00003");
		LOTNOS.put("半全场胜平负", "J00004");
		LOTNOS.put("胜负", "J00005");
		LOTNOS.put("让分胜负", "J00006");
		LOTNOS.put("胜分差", "J00007");
		LOTNOS.put("大小分", "J00008");
	}
	
	public void init() {
		try {
			issend = false;
			getFootballMatches();
		} catch(Exception e) {
			logger.error("getFootballMatches出错", e);
		}
		try {
			getBasketballMatches();
		} catch(Exception e) {
			logger.error("getBasketballMatches出错", e);
		}
	}
	
	public void getBasketballMatches() throws Exception {
		logger.info("开始获取竞彩蓝球对阵信息, url:{}", new String[] {BASKETBALL_URL});
		Document doc = getJsoupDocument(BASKETBALL_URL);
		Element element = doc.select(".tbl").get(0);
		List<Element> trs = element.getElementsByTag("tr");
		BigDecimal type = BigDecimal.ZERO;
		for(Element tr : trs) {
			List<Element> tds = tr.getElementsByTag("td");
			if(null != tds && !tds.isEmpty()) {
				if(trim(tds.get(0).text()).matches("周.\\d{3}")) {
					saveBasketballMatches(type, tds, tr.outerHtml());
				}
			}
		}
		logger.info("获取竞彩蓝球对阵信息结束, url:{}", new String[] {BASKETBALL_URL});
	}
	
	private void saveBasketballMatches(BigDecimal type, List<Element> tds, String tr) {
		try {
			String issell = trim(tds.get(4).text());
			if(issellStr.equals(issell)) {
				String event = trim(tds.get(0).text());
				Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("20" + tds.get(3).text().trim());
				BigDecimal weekid = WEEKID.get(event.substring(0, 2));
				String day = getDay(weekid.intValue(), time);
				String teamid = event.substring(2);
				String league = trim(tds.get(1).text());
				String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
				String[] teams = team.split("\\:");
				team = teams[1] + ":" + teams[0];
				if(null == tjingcaiDao.findTjingcaimatches(type, weekid, teamid, league, team, day)) {
					Date datetime = getTime(time, 1);
					String unsupportStr = trim(tds.get(5).text());
					String unsupport = "";
					if(!StringUtil.isEmpty(unsupportStr)) {
						String[] s = unsupportStr.split("\\:");
						if(s.length > 1 ) {
							try {
								String[] values = unsupportStr.split("\\:")[1].split(",");
								unsupport = buildUnsupport(values);
							} catch(Exception e){}
						}
					}
					saveMatches(type, weekid, day, teamid, league, team,
							datetime, unsupport);
				}
			}
		} catch(Exception e) {
			logger.error("saveBasketballMatches出错, tr:" + tr, e);
		}
	}

	private String buildUnsupport(String[] unsupports) {
		if(null != unsupports) {
			StringBuilder builder = new StringBuilder();
			for(String unsupport : unsupports) {
				unsupport = trim(unsupport);
				for(Entry<String, String> entry : LOTNOS.entrySet()) {
					String key = entry.getKey();
					if(unsupport.contains(key)) {
						String type = unsupport.substring(key.length());
						builder.append(entry.getValue()).append("_");
						if(type.contains("单场")) {
							builder.append("0");
						} else {
							builder.append("1");
						}
						builder.append(",");
						break;
					}
				}
			}
			if(!StringUtil.isEmpty(builder.toString())) {
				builder.deleteCharAt(builder.length() - 1);
			}
			return builder.toString();
		}
		return null;
	}

	public void getFootballMatches() throws Exception {
		logger.info("开始获取竞彩足彩对阵信息, url:{}", new String[] {FOOTBALL_URL});
		Document doc = getJsoupDocument(FOOTBALL_URL);
		Element element = doc.getElementById("jumpTable");
		List<Element> trs = element.getElementsByTag("tr");
		BigDecimal type = BigDecimal.ONE;
		for(Element tr : trs) {
			List<Element> tds = tr.getElementsByTag("td");
			if(null != tds && !tds.isEmpty()) {
				if(trim(tds.get(0).text()).matches("周.\\d{3}")) {
					saveFootballMatches(type, tds, tr.outerHtml());
				}
			}
		}
		logger.info("获取竞彩足彩对阵信息结束, url:{}", new String[] {FOOTBALL_URL});
	}

	private void saveFootballMatches(BigDecimal type, List<Element> tds, String tr) {
		try {
			String event = trim(tds.get(0).text());
			Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(tds.get(3).text().trim());
			BigDecimal weekid = WEEKID.get(event.substring(0, 2));
			String day = getDay(weekid.intValue(), time);
			String teamid = event.substring(2);
			String league = trim(tds.get(1).text());
			String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
			if(null == tjingcaiDao.findTjingcaimatches(type, weekid, teamid, league, team, day)) {
				logger.info("开始处理event:{},weekid:{},teamid:{},league:{},team:{}", new String[]{event, String.valueOf(weekid.intValue()), teamid, league, team});
				Date datetime = getTime(time, 1);
				String unsupportStr = trim(tds.get(5).text());
				String unsupport = "";
				if(!StringUtil.isEmpty(unsupportStr)) {
					String[] values = unsupportStr.split("\\:")[1].split(",");
					unsupport = buildUnsupport(values);
				}
				if(jingcaiPeiluService.getPeilu_Football(weekid, teamid, league, team)) {
					logger.info("存在赔率，event:" + event);
					saveMatches(type, weekid, day, teamid, league, team,
							datetime, unsupport);
				} else {
					logger.info("不存在赔率，event:" + event);
				}
			}
		} catch(Exception e) {
			logger.error("saveFootballMatches出错, tr:" + tr, e);
		}
	}

	private void saveMatches(BigDecimal type, BigDecimal weekid, String day, String teamid,
			String league, String team, Date datetime, String unsupport) {
		Tjingcaimatches tjingcaimatches = new Tjingcaimatches();
		tjingcaimatches.setType(type);
		tjingcaimatches.setDay(day);
		tjingcaimatches.setWeekid(weekid);
		tjingcaimatches.setTeamid(teamid);
		tjingcaimatches.setSaleflag(BigDecimal.ZERO);
		tjingcaimatches.setState(JingcaiState.Normal.value);
		tjingcaimatches.setLeague(league);
		tjingcaimatches.setTeam(team);
		tjingcaimatches.setTime(datetime);
		tjingcaimatches.setUnsupport(unsupport);
		tjingcaimatches.setCtstate(BigDecimal.ZERO);
		tjingcaimatches.setAudit(jingcaiaudit ? JingcaiState.AUDIT.value : JingcaiState.NOTAUDIT.value);
		try {
			tjingcaimatches.setEndtime(getEndtime(datetime, String.valueOf(type.intValue())));
			tjingcaiDao.persist(tjingcaimatches);
		} catch(Exception e) {
			logger.error("保存竞彩场次出错, matched:" + JsonUtil.toJson(tjingcaimatches), e);
		}
		
		try {
			logger.info("开始更新赛事简称:type:"+type+" day:"+day+" weekid:"+weekid+" teamid:"+teamid+" name:"+league);
			Events events = eventsService.findEvent(type, league);
			if(events!=null) {
				tjingcaiDao.updateShortname(type, day, weekid, teamid, events.getShortname());
			}
			logger.info("更新赛事简称成功:type:"+type+" day:"+day+" weekid:"+weekid+" teamid:"+teamid+" name:"+league+" shortname:"+(null == events ? null : events.getShortname()));
		}catch(Exception e) {
			logger.error("更新赛事简称出错" + JsonUtil.toJson(tjingcaimatches), e);
		}
		if(!issend && !jingcaiaudit) {
			sendSMS.sendSMS(msgstation+sendSMS.matchesMsg);
			issend = true;
		}
	}
	
	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = sdf.parse("2013-04-17 02:45:00");
		System.out.println(sdf.format(new JingcaiDuizhenService().getEndtime(now, "0")));
	}
	
	public Date getEndtime(Date datetime,String type) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(datetime);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int weekid = calendar.get(Calendar.DAY_OF_WEEK);
		TJingcaiParam jingcaiParam = tjingcaiDao.findParamByid("beforemin");
		int beforemin = biguser ? 30 : 45;
		if("0".equals(type)) {
			if(4 == weekid || 5 == weekid) {
				if(hour < 7 || ( hour == 7 && min <= beforemin)) {
					calendar.add(Calendar.DATE, -1);
					calendar.set(Calendar.HOUR_OF_DAY, 23);
					calendar.set(Calendar.MINUTE, 59 - Integer.parseInt(jingcaiParam.getValue()));
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					return calendar.getTime();
				} else {
					return getTime(datetime, 0 - Integer.parseInt(jingcaiParam.getValue()));
				}
			}
		}
		beforemin = biguser ? 1 : 15;
		if(0 <= hour && hour < 9) {
			if((1 == weekid || 2 == weekid)) {
				if(hour == 0 && min <= 59) {
					return getTime(datetime, 0 - Integer.parseInt(jingcaiParam.getValue()));
				}
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 59 - Integer.parseInt(jingcaiParam.getValue()));
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return calendar.getTime();
			} else {
				calendar.add(Calendar.DATE, -1);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59 - Integer.parseInt(jingcaiParam.getValue()));
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return calendar.getTime();
			}
		} else if(9 == hour  && min <= beforemin) {
			if(1 == weekid || 2 == weekid) {
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 59 - Integer.parseInt(jingcaiParam.getValue()));
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return calendar.getTime();
			} else {
				calendar.add(Calendar.DATE, -1);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59 - Integer.parseInt(jingcaiParam.getValue()));
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return calendar.getTime();
			}
		} else {
			return getTime(datetime, 0 - Integer.parseInt(jingcaiParam.getValue()));
		}
	}

	private Document getJsoupDocument(String url) throws Exception {
		return Jsoup.connect(url).timeout(timeout).get();
	}
	
	private Date getTime(Date time, int min) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.add(Calendar.MINUTE, min);
		return calendar.getTime();
	}

	public String getDay(int weekid, Date time) {
		while(WEEK.get(weekid) != getWeekid(time)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			calendar.add(Calendar.DAY_OF_WEEK, -1);
			time = calendar.getTime();
		}
		return new SimpleDateFormat("yyyyMMdd").format(time);
	}
	
	public int getWeekid(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	private String trim(String content) {
		content = content.replaceAll("\\s*", "");
		content = content.replaceAll(" *", "");
		return content;
	}
}
