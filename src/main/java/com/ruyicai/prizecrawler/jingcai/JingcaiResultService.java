package com.ruyicai.prizecrawler.jingcai;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.consts.JingcaiState;
import com.ruyicai.prizecrawler.domain.TjingcaiResult;
import com.ruyicai.prizecrawler.domain.Tjingcaimatches;
import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;
import com.ruyicai.prizecrawler.util.JsonUtil;
import com.ruyicai.prizecrawler.util.SendSMS;
import com.ruyicai.prizecrawler.util.StringUtil;

@Service
public class JingcaiResultService {
	
	private Logger logger = LoggerFactory.getLogger(JingcaiResultService.class);

	public static final String FOOTBALL_URL = "http://info.sporttery.com/football/match_result.php";
	
	public static final String BASKETBALL_URL = "http://info.sporttery.com/basketball/match_result.php";
	
	private static final String PREFIX_FOOTBALL_URL = "http://info.sporttery.com/football/";
	
	private static final String PREFIX_BASKETBALL_URL = "http://info.sporttery.com/basketball/";
	
	private static final Map<String, BigDecimal> WEEKID = new HashMap<String, BigDecimal>();
	
	private static final Map<Integer, Integer> WEEK = new HashMap<Integer, Integer>();
	
	@Autowired
	private TjingcaiDao tjingcaiDao;
	
	private int timeout = 30000;
	
	private static final String FOOTBALL_TYPE = "1";
	
	private static final String BASKETBALL_TYPE = "0";
	
	@Autowired
	private SendSMS sendSMS;
	
	@Value("${msg.station}")
	private String msgstation;
	
	@Value("${jingcai.audit}")
	public boolean jingcaiaudit;
	
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
	}
	
	public void footProcess(String url) {
		try {
			List<Tjingcaimatches> tjingcaimatches = tjingcaiDao.findByStateAndType(JingcaiState.END.value, BigDecimal.ONE);
			if(!tjingcaimatches.isEmpty()) {
				getFootballResult(url);
			}
		} catch(Exception e) {
			logger.error("getFootballResult出错", e);
		}
	}
	
	public void bassProcess(String url) {
		try {
			List<Tjingcaimatches> tjingcaimatches = tjingcaiDao.findByStateAndType(JingcaiState.END.value, BigDecimal.ZERO);
			if(!tjingcaimatches.isEmpty()) {
				getBasketballResult(url);
			}
		} catch(Exception e) {
			logger.error("getFootballResult出错", e);
		}
	}
	
	public String getPreDate(int p) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0 - p);
		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}
	
	public void init(int daycount, int page) {
		try {
			List<Tjingcaimatches> tjingcaimatches = tjingcaiDao.findByStateAndType(JingcaiState.END.value, BigDecimal.ONE);
			if(!tjingcaimatches.isEmpty()) {
				String start_date = getPreDate(daycount);
				String end_date = getPreDate(0);
				for(int i = 1; i <= page; i ++) {
					String url = FOOTBALL_URL + "?start_date=" + start_date + "&end_date=" + end_date + "&page=" + i;
					try {
						getFootballResult(url);
					} catch(Exception e) {
						logger.error("getFootballResult出错, url:" + url, e);
					}
				}
			}
		} catch(Exception e) {
			logger.error("getFootballResult出错", e);
		}
		try {
			List<Tjingcaimatches> tjingcaimatches = tjingcaiDao.findByStateAndType(JingcaiState.END.value, BigDecimal.ZERO);
			if(!tjingcaimatches.isEmpty()) {
				String start_date = getPreDate(daycount);
				String end_date = getPreDate(0);
				for(int i = 1; i <= page; i ++) {
					String url = BASKETBALL_URL + "?start_date=" + start_date + "&end_date=" + end_date + "&page=" + i;
					try {
						getBasketballResult(url);
					} catch(Exception e) {
						logger.error("getBasketballResult出错, url:" + url, e);
					}
				}
			}
		} catch(Exception e) {
			logger.error("getBasketballResult出错", e);
		}
	}
	
	public void init() {
		init(10, 10);
	}
	
	public void persist(Map<String, TjingcaiResult> map) {
		for(TjingcaiResult tjingcaiResult : map.values()) {
			logger.info("保存TjingcaiResult, result:{}", new String[] {JsonUtil.toJson(tjingcaiResult)});
			tjingcaiDao.persist(tjingcaiResult);
		}
		if(!map.isEmpty()) {
			sendSMS.sendSMS(msgstation+sendSMS.resultMsg);
		}
	}
	
	public TjingcaiResult getJingcaiResult(String id) throws Exception {
		String type = id.split("\\_")[0];
		Map<String, TjingcaiResult> map = new HashMap<String, TjingcaiResult>();
		if(FOOTBALL_TYPE.equals(type)) {
			getFootballResult(map, FOOTBALL_URL);
		} else if(BASKETBALL_TYPE.equals(type)) {
			getBasketballResult(map, BASKETBALL_URL);
		} else {
			logger.info("id输入错误, id:{}", new String[] {id});
			throw new RuntimeException("id输入错误, id:" + id);
		}
		TjingcaiResult tjingcaiResult = map.get(id);
		if(null != tjingcaiResult) {
			logger.info("保存TjingcaiResult, result:{}", new String[] {JsonUtil.toJson(tjingcaiResult)});
			tjingcaiDao.persist(tjingcaiResult);
		}
		return tjingcaiResult;
	}
	
	public void getBasketballResult(String url) throws Exception {
		Map<String, TjingcaiResult> map = new HashMap<String, TjingcaiResult>();
		getBasketballResult(map, url);
		persist(map);
	}
	
	public void getBasketballResult(Map<String, TjingcaiResult> map, String url) throws Exception {
		logger.info("开始获取竞彩蓝球赛果信息, url:{}", new String[] {url});
		Document doc = getJsoupDocument(url);
		Element element = doc.select(".tbl").get(0);
		List<Element> trs = element.getElementsByTag("tr");
		BigDecimal type = BigDecimal.ZERO;
		for(Element tr : trs) {
			List<Element> tds = tr.getElementsByTag("td");
			if(null != tds && tds.size() > 1 && trim(tds.get(1).text()).matches("周.\\d{3}")) {
				saveBasketballResult(type, tds, tr.outerHtml(), map);
			}
		}
		logger.info("获取竞彩蓝球赛果信息结束, url:{}", new String[] {BASKETBALL_URL});
	}
	
	private void saveBasketballResult(BigDecimal type, List<Element> tds,
			String tr, Map<String, TjingcaiResult> map) {
		try {
			String openday = trim(tds.get(0).text());
			long opendayL = Long.parseLong(openday.replaceAll("\\-", ""));
			String event = trim(tds.get(1).text());
			String league = trim(tds.get(2).text());
			String team = trim(tds.get(3).text()).replaceAll("\\s*VS\\s*", ":");
			String[] teams = team.split("\\:");
			team = teams[1] + ":" + teams[0];
			BigDecimal weekid = WEEKID.get(event.substring(0, 2));
			String teamid = event.substring(2);
			Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(type, weekid, teamid, league, team);
			if(null == tjingcaimatches || opendayL < Long.parseLong(tjingcaimatches.getDay())) {
				return;
			}
			String id = StringUtil.join("_", String.valueOf(type.intValue()), tjingcaimatches.getDay(), String.valueOf(weekid.intValue()), teamid);
			if(null == tjingcaiDao.findResultByid(id)) {
				int rindex = tds.size() == 9 ? 7 : 9;
				if("取消".equals(trim(tds.get(rindex).text()))) {
					map.put(id, buildResult(id, "", "", "", "", "", "", "", "", "", "", "", BigDecimal.ONE));
					return;
				}
				if(!"已完成".equals(trim(tds.get(rindex).text()))) {
					return;
				}
				rindex = tds.size() == 9 ? 6 : 8;
				String result = trim(tds.get(rindex).text().trim());
				String[] results = result.split("\\:");
				result = results[1] + ":" + results[0];
				rindex = tds.size() == 9 ? 8 : 10;
				String url = tds.get(rindex).getElementsByTag("a").get(0).attr("href");
				saveBasketballResult(id, result, url, map);
			}
		} catch(Exception e) {
			logger.error("saveBasketballResult出错, tr:" + tr, e);
		}
	}


	private void saveBasketballResult(String id, String result, String url, Map<String, TjingcaiResult> map) throws Exception {
		Document doc = getJsoupDocument(PREFIX_BASKETBALL_URL + url);
		Element element = doc.select(".table01").first();
		List<Element> trs = element.select("tr");
		TdsAndIndex tai = getTDS(trs, "胜负");
		List<Element> tds = tai.getTds();
		String b0 = trim(tds.get(2).text());
		b0 = StringUtil.isEmpty(b0) ? "1" : b0;
		tai = getTDS(trs, "让分胜负");
		tds = tai.getTds();
		String letpoint = getShuzi(tds.get(1).text());
		letpoint = letpoint.replaceAll("\\(*\\)*", "");
		String b1 = trim(tds.get(2).text());
		b1 = StringUtil.isEmpty(b1) ? "1" : b1;
		String b2 = "1";
		tai = getTDS(trs, "大小分");
		tds = tai.getTds();
		String basepoint = getShuzi(tds.get(1).text());
		basepoint = basepoint.replaceAll("\\(*\\)*", "");
		String b3 = trim(tds.get(2).text());
		b3 = StringUtil.isEmpty(b3) ? "1" : b3;
		String b4 = "", b5 = "", b6 = "";
		if(!StringUtil.isEmpty(transferNotwin(b0)) && !StringUtil.isEmpty(transferNotwin(b1)) && !StringUtil.isEmpty(transferNotwin(b2)) && !StringUtil.isEmpty(transferNotwin(b3))) {
			map.put(id, buildResult(id, letpoint, basepoint, result, "", transferNotwin(b0), transferNotwin(b1), transferNotwin(b2), transferNotwin(b3), transferNotwin(b4), transferNotwin(b5), transferNotwin(b6), BigDecimal.ZERO));
		}
	}
	
	public String getShuzi(String team) {
		Pattern pattern = Pattern.compile(".*?(\\+?\\-?\\d+\\.?\\d*).*");
		Matcher matcher = pattern.matcher(team);
		if(matcher.find()) {
			return matcher.group(1);
		}
		return "0";
	}

	private TdsAndIndex getTDS(List<Element> trs, String name) throws Exception {
		List<Element> tds = null;
		for(int i = 0; i < trs.size(); i ++) {
			tds = trs.get(i).select("td");
			if(tds.size() > 0) {
				if(name.equals(trim(tds.get(0).text()))) {
					TdsAndIndex tai = new TdsAndIndex(tds, 0);
					return tai;
				}
			}
		}
		return null;
	}
	
	public void getFootballResult(String url) throws Exception {
		Map<String, TjingcaiResult> map = new HashMap<String, TjingcaiResult>();
		getFootballResult(map, url);
		persist(map);
	}

	public void getFootballResult(Map<String, TjingcaiResult> map, String url) throws Exception {
		logger.info("开始获取竞彩足彩赛果信息, url:{}", new String[] {url});
		Document doc = getJsoupDocument(url);
		Element element = doc.getElementById("jumpTable");
		List<Element> trs = element.getElementsByTag("tr");
		BigDecimal type = BigDecimal.ONE;
		for(Element tr : trs) {
			List<Element> tds = tr.getElementsByTag("td");
			if(null != tds && tds.size() > 1 &&  trim(tds.get(1).text()).matches("周.\\d{3}")) {
				saveFootballResult(type, tds, tr.outerHtml(), map);
			}
		}
		logger.info("获取竞彩足彩赛果信息结束, url:{}", new String[] {FOOTBALL_URL});
	}

	private void saveFootballResult(BigDecimal type, List<Element> tds, String tr, Map<String, TjingcaiResult> map) {
		try {
			String openday = trim(tds.get(0).text());
			long opendayL = Long.parseLong(openday.replaceAll("\\-", ""));
			String event = trim(tds.get(1).text());
			String league = trim(tds.get(2).text());
			String team = trim(tds.get(3).text()).replaceAll("\\s*VS\\s*", ":");
			team = team.replaceAll("\\(.*\\)", "");
			BigDecimal weekid = WEEKID.get(event.substring(0, 2));
			String teamid = event.substring(2);
			Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(type, weekid, teamid, league, team);
			if(null == tjingcaimatches || opendayL < Long.parseLong(tjingcaimatches.getDay())) {
				return;
			}
			String id = StringUtil.join("_", String.valueOf(type.intValue()), tjingcaimatches.getDay(), String.valueOf(weekid.intValue()), teamid);
			if(null == tjingcaiDao.findResultByid(id)) {
				if("取消".equals(trim(tds.get(6).text()))) {
					map.put(id, buildResult(id, "", "", "", "", "", "", "", "", "", "", "", BigDecimal.ONE));
					return;
				}
				if(!"已完成".equals(trim(tds.get(6).text()))) {
					return;
				}
				String firsthalfresult = trim(tds.get(4).text());
				String result = trim(tds.get(5).text());
				String url = tds.get(7).getElementsByTag("a").get(0).attr("href");
				saveFootballResult(id, firsthalfresult, result, url, map);
			}
		} catch(Exception e) {
			logger.error("saveFootballResult出错, tr:" + tr, e);
		}
	}

	private void saveFootballResult(String id, String firsthalfresult,
			String result, String url, Map<String, TjingcaiResult> map) throws Exception {
		Document doc = Jsoup.connect(PREFIX_FOOTBALL_URL + url).timeout(30000).get();
		Element element = doc.select("table").first();
		List<Element> trs = element.select("tr");
		List<Element> tds = trs.get(3).select("td");
		String letpoint = trim(tds.get(1).text());
		letpoint = letpoint.replaceAll("\\(*\\)*", "");
		if("无让球".equals(letpoint)) {
			letpoint = "0";
		}
		String b1 = trim(tds.get(3).text());
		String b2 = "", b3 = "";
		tds = trs.get(4).select("td");
		String b0 = trim(tds.get(3).text());
		tds = trs.get(5).select("td");
		String b4 = StringUtil.isEmpty(trim(tds.get(3).text())) ? "0" : trim(tds.get(3).text());
		tds = trs.get(6).select("td");
		String b5 = trim(tds.get(3).text());
		tds = trs.get(7).select("td");
		String b6 = trim(tds.get(3).text());
		if(!StringUtil.isEmpty(transferNotwin(b0)) && !StringUtil.isEmpty(transferNotwin(b4)) && !StringUtil.isEmpty(transferNotwin(b5)) && !StringUtil.isEmpty(transferNotwin(b6))) {
			map.put(id, buildResult(id, letpoint, "", result, firsthalfresult, transferNotwin(b0), transferNotwin(b1), transferNotwin(b2), transferNotwin(b3), transferNotwin(b4), transferNotwin(b5), transferNotwin(b6), BigDecimal.ZERO));
		}
	}
	
	private static String transferNotwin(String b) {
		if(StringUtil.isEmpty(b)) {
			return "1";
		}
		if("无胜出投注".equals(b)) {
			return "1";
		} else {
			return b;
		}
	}
	
	private TjingcaiResult buildResult(String id, String letpoint, String basepoint, String result, String firsthalfresult, String b0, String b1, String b2, String b3, String b4, String b5, String b6, BigDecimal cancel) {
		TjingcaiResult tjingcaiResult = new TjingcaiResult();
		tjingcaiResult.setId(id);
		tjingcaiResult.setLetpoint(letpoint);
		tjingcaiResult.setBasepoint(basepoint);
		tjingcaiResult.setResult(result);
		tjingcaiResult.setFirsthalfresult(firsthalfresult);
		tjingcaiResult.setB0(b0);
		tjingcaiResult.setB1(b1);
		tjingcaiResult.setB2(b2);
		tjingcaiResult.setB3(b3);
		tjingcaiResult.setB4(b4);
		tjingcaiResult.setB5(b5);
		tjingcaiResult.setB6(b6);
		tjingcaiResult.setCancel(cancel);
		tjingcaiResult.setAudit(jingcaiaudit ? JingcaiState.AUDIT.value : JingcaiState.NOTAUDIT.value);
		return tjingcaiResult;
	}
	
	private Document getJsoupDocument(String url) throws Exception {
		return Jsoup.connect(url).timeout(timeout).get();
	}

	public String getDay(int weekid, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, weekid);
		if(calendar.getTime().after(date)) {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.WEEK_OF_MONTH, -1);
			calendar.set(Calendar.DAY_OF_WEEK, weekid);
		}
		return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
	}
	
	private String trim(String content) {
		content = content.replaceAll("\\s*", "");
		content = content.replaceAll(" *", "");
		return content;
	}
	
	class TdsAndIndex {
		
		private List<Element> tds;
		
		private int index;
		
		public TdsAndIndex(List<Element> tds, int index) {
			this.tds = tds;
			this.index = index;
		}

		public List<Element> getTds() {
			return tds;
		}

		public void setTds(List<Element> tds) {
			this.tds = tds;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}
}
