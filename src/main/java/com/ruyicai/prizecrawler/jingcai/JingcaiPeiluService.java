package com.ruyicai.prizecrawler.jingcai;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.domain.Tjingcaimatches;
import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;
import com.ruyicai.prizecrawler.util.StringUtil;

import flexjson.JSONDeserializer;

@Service
public class JingcaiPeiluService {

	private Logger logger = LoggerFactory.getLogger(JingcaiPeiluService.class);

	private static Map<String, org.dom4j.Document> jingcaipeilu = new HashMap<String, org.dom4j.Document>();

	private static Map<String, Object> jingcaijsonpeilu = new HashMap<String, Object>();

	private static final String FOOTBALL_DAN_J00001 = "http://info.sporttery.com/football/hhad_vp.php";

	private static final String FOOTBALL_DAN_J00002 = "http://info.sporttery.com/football/crs_single.php";

	private static final String FOOTBALL_DAN_J00003 = "http://info.sporttery.com/football/ttg_vp.php";

	private static final String FOOTBALL_DAN_J00004 = "http://info.sporttery.com/football/hafu_vp.php";

	private static final String BASKETBALL_DAN_J00005 = "http://info.sporttery.com/basketball/mnl_vp.php";

	private static final String BASKETBALL_DAN_J00006 = "http://info.sporttery.com/basketball/hdc_vp.php";

	private static final String BASKETBALL_DAN_J00007 = "http://info.sporttery.com/basketball/wnm_single.php";

	private static final String BASKETBALL_DAN_J00008 = "http://info.sporttery.com/basketball/hilo_vp.php";

	private static final String BASKETBALL_GUO_J00005 = "http://info.sporttery.com/basketball/mnl_list.php";

	private static final String BASKETBALL_GUO_J00006 = "http://info.sporttery.com/basketball/hdc_list.php";

	private static final String BASKETBALL_GUO_J00007 = "http://info.sporttery.com/basketball/wnm_list.php";

	private static final String BASKETBALL_GUO_J00008 = "http://info.sporttery.com/basketball/hilo_list.php";

	private static final String FOOTBALL_GUO = "http://info.sporttery.com/football/hhad_list.php";

	private static final String FOOTBALL_GUO_1 = "http://info.sporttery.com/interface/interface_wms.php?action=wf_list&pkey=";

	private static final Map<String, BigDecimal> WEEKID = new HashMap<String, BigDecimal>();

	private static final Map<Integer, Integer> WEEK = new HashMap<Integer, Integer>();

	@Autowired
	private TjingcaiDao tjingcaiDao;

	private int timeout = 30000;

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

	public org.dom4j.Document getJingcaipeilu(String type, String valueType) {
		return jingcaipeilu.get(StringUtil.join("_", type, valueType));
	}

	public Object getJingcaijsonpeilu(String type, String valueType) {
		return jingcaijsonpeilu.get(StringUtil.join("_", type, valueType));
	}

	public static void main(String[] args) throws Exception {
		new JingcaiPeiluService().getJingcaiPeilu();
	}

	public void getJingcaiPeilu() {
		logger.info("开始更新竞彩赔率信息");
		List<String> matchlist = tjingcaiDao.findMatchlist(BigDecimal.ONE);
		try {
			FootballGlobal footballGlobal = getFootballGlobal_Dan();
			jingcaipeilu.put("1_0",
					getPeilu_Football_Dan(footballGlobal, matchlist));
			jingcaijsonpeilu.put("1_0", footballGlobal);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		try {
			FootballGlobal footballGlobal = getPeilu_Football_Guo();
			jingcaipeilu.put("1_1",
					getPeilu_Football_Guo(footballGlobal, matchlist));
			jingcaijsonpeilu.put("1_1", footballGlobal);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		matchlist = tjingcaiDao.findMatchlist(BigDecimal.ZERO);
		try {
			BasketballGlobal basketballGlobal = getBasketballGlobal_DAN();
			jingcaipeilu.put("0_0",
					getPeilu_Basketball_Dan(basketballGlobal, matchlist));
			jingcaijsonpeilu.put("0_0", basketballGlobal);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		try {
			BasketballGlobal basketballGlobal = getBasketballGlobal_Guo();
			jingcaipeilu.put("0_1",
					getPeilu_Basketball_Guo(basketballGlobal, matchlist));
			jingcaijsonpeilu.put("0_1", basketballGlobal);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("更新竞彩赔率信息结束");
	}

	public boolean getPeilu_Football(BigDecimal weekid, String teamid,
			String league, String team) throws Exception {
		return getPeilu_Football_Guo(weekid, teamid, league, team);
	}

	public String getHtml(String url) throws Exception {
		return getJsoupDocument(url).html();
	}

	private boolean getPeilu_Football_Guo(BigDecimal weekidp, String teamidp,
			String leaguep, String teamp) throws Exception {
		logger.info("开始获取足球过关赔率信息, url:{}", new String[] { FOOTBALL_GUO });
		org.jsoup.nodes.Document doc = getJsoupDocument(FOOTBALL_GUO);
		org.jsoup.nodes.Element element = doc.getElementById("data2");
		doc = getJsoupDocument(FOOTBALL_GUO_1 + trim(element.text()));
		String data = doc.select("body").first().text();
		data = data.substring(data.indexOf("[[["));
		data = data.substring(0, data.indexOf("]]]") + 3);
		JSONDeserializer<List<List<List<Object>>>> json = new JSONDeserializer<List<List<List<Object>>>>();
		List<List<List<Object>>> list = json.deserialize(data);
		for (List<List<Object>> list1 : list) {
			String event = trim(list1.get(0).get(0).toString());
			BigDecimal weekid = WEEKID.get(event.substring(0, 2));
			String teamid = event.substring(2);

			String team = list1.get(0).toString();
			String letpoint = getLetpoint(team);
			String replace = letpoint;
			if (replace.startsWith("+") || replace.startsWith("-")) {
				replace = "\\" + replace;
			}
			String league = trim(list1.get(0).get(1).toString());
			team = trim((list1.get(0).get(2).toString()).replaceAll("\\$"
					+ replace + "\\$", ":"));
			team = team.replaceAll("\\$\\$", ":");
			if (weekidp.intValue() == weekid.intValue()
					&& teamid.equals(teamidp) && league.equals(leaguep)
					&& team.equals(teamp)) {
				return true;
			}
		}
		logger.info("获取足球过关赔率信息结束, url:{}", new String[] { FOOTBALL_GUO });
		return false;
	}

	private org.dom4j.Document getPeilu_Football_Guo(
			FootballGlobal footballGlobal, List<String> list) throws Exception {
		org.dom4j.Element matchList = getMatchList();
		for (Entry<String, Football_Vs> entry : footballGlobal.getVsmap()
				.entrySet()) {
			String id = entry.getKey();
			if (list.contains(id)) {
				Football_Vs vs = entry.getValue();
				Score score = footballGlobal.getScore(id);
				Goal goal = footballGlobal.getGoal(id);
				Half half = footballGlobal.getHalf(id);
				org.dom4j.Element item = matchList.addElement("item");
				item.addElement("id").setText(id);
				addElement(Football_Vs.class, vs, item);
				addElement(Score.class, score, item);
				addElement(Goal.class, goal, item);
				addElement(Half.class, half, item);
			}
		}
		logger.info("获取足球过关赔率信息结束");
		return matchList.getDocument();
	}

	private org.dom4j.Element getMatchList() {
		org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
		org.dom4j.Element root = document.addElement("message");
		org.dom4j.Element head = root.addElement("head");
		head.addElement("messageId").setText(
				UUID.randomUUID().toString().replaceAll("-", ""));
		head.addElement("result").setText("0");
		head.addElement("md").setText(
				UUID.randomUUID().toString().replaceAll("-", ""));
		org.dom4j.Element matchList = root.addElement("body").addElement(
				"matchList");
		return matchList;
	}

	private FootballGlobal getPeilu_Football_Guo() throws Exception {
		logger.info("开始获取足球过关赔率信息, url:{}", new String[] { FOOTBALL_GUO });
		org.jsoup.nodes.Document doc = getJsoupDocument(FOOTBALL_GUO);
		org.jsoup.nodes.Element element = doc.getElementById("data2");
		doc = getJsoupDocument(FOOTBALL_GUO_1 + trim(element.text()));
		FootballGlobal footballGlobal = new FootballGlobal();
		String data = doc.select("body").first().text();
		data = data.substring(data.indexOf("[[["));
		data = data.substring(0, data.indexOf("]]]") + 3);
		JSONDeserializer<List<List<List<Object>>>> json = new JSONDeserializer<List<List<List<Object>>>>();
		List<List<List<Object>>> list = json.deserialize(data);
		for (List<List<Object>> list1 : list) {
			String event = trim(list1.get(0).get(0).toString());
			BigDecimal weekid = WEEKID.get(event.substring(0, 2));
			String teamid = event.substring(2);

			String team = list1.get(0).toString();
			String letpoint = getLetpoint(team);
			String replace = letpoint;
			if (replace.startsWith("+") || replace.startsWith("-")) {
				replace = "\\" + replace;
			}
			String league = trim(list1.get(0).get(1).toString());
			team = trim((list1.get(0).get(2).toString()).replaceAll("\\$"
					+ replace + "\\$", ":"));
			team = team.replaceAll("\\$\\$", ":");

			Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
					BigDecimal.ONE, weekid, teamid, league, team);
			if (null == tjingcaimatches) {
				continue;
			}
			String id = StringUtil.join("_", tjingcaimatches.getDay(),
					String.valueOf(weekid.intValue()), teamid);
			addVs(id, team.contains(letpoint) ? "0" : letpoint, footballGlobal, list1.get(1));
			addScore(id, footballGlobal, list1.get(2));
			addGoal(id, footballGlobal, list1.get(3));
			addHalf(id, footballGlobal, list1.get(4));
		}
		logger.info("获取足球过关赔率信息结束, url:{}", new String[] { FOOTBALL_GUO });
		return footballGlobal;
	}

	private void addHalf(String id, FootballGlobal footballGlobal,
			List<Object> list) {
		try {
			footballGlobal.putHalf(id, new Half(list.get(0).toString(), list.get(1)
					.toString(), list.get(2).toString(), list.get(3).toString(),
					list.get(4).toString(), list.get(5).toString(), list.get(6)
							.toString(), list.get(7).toString(), list.get(8)
							.toString()));
		}catch (Exception e) {
			logger.error("存在未出赔率", e);
		}
		
	}

	private void addGoal(String id, FootballGlobal footballGlobal,
			List<Object> list) {
		footballGlobal.putGoal(id, new Goal(list.get(0).toString(), list.get(1)
				.toString(), list.get(2).toString(), list.get(3).toString(),
				list.get(4).toString(), list.get(5).toString(), list.get(6)
						.toString(), list.get(7).toString()));
	}

	private void addScore(String id, FootballGlobal footballGlobal,
			List<Object> list) {
		Score score = new Score();
		score.setV09(list.get(0).toString());
		score.setV99(list.get(1).toString());
		score.setV90(list.get(2).toString());
		score.setV00(list.get(3).toString());
		score.setV01(list.get(4).toString());
		score.setV02(list.get(5).toString());
		score.setV03(list.get(6).toString());
		score.setV04(list.get(7).toString());
		score.setV05(list.get(8).toString());
		score.setV10(list.get(9).toString());
		score.setV11(list.get(10).toString());
		score.setV12(list.get(11).toString());
		score.setV13(list.get(12).toString());
		score.setV14(list.get(13).toString());
		score.setV15(list.get(14).toString());
		score.setV20(list.get(15).toString());
		score.setV21(list.get(16).toString());
		score.setV22(list.get(17).toString());
		score.setV23(list.get(18).toString());
		score.setV24(list.get(19).toString());
		score.setV25(list.get(20).toString());
		score.setV30(list.get(21).toString());
		score.setV31(list.get(22).toString());
		score.setV32(list.get(23).toString());
		score.setV33(list.get(24).toString());
		score.setV40(list.get(25).toString());
		score.setV41(list.get(26).toString());
		score.setV42(list.get(27).toString());
		score.setV50(list.get(28).toString());
		score.setV51(list.get(29).toString());
		score.setV52(list.get(30).toString());
		footballGlobal.putScore(id, score);
	}

	private void addVs(String id, String letpoint,
			FootballGlobal footballGlobal, List<Object> list) {
		footballGlobal.putVs(id, new Football_Vs(list.get(0).toString(), list
				.get(1).toString(), list.get(2).toString(), letpoint));
	}

	private String getLetpoint(String team) {
		String letpoint = "";
		Pattern pattern = Pattern.compile(".*\\$(.*)\\$.*");
		Matcher matcher = pattern.matcher(team);
		if (matcher.find()) {
			letpoint = matcher.group(1);
		}
		if (StringUtil.isEmpty(letpoint)) {
			return "0";
		}
		return letpoint;
	}

	private org.dom4j.Document getPeilu_Basketball_Guo(
			BasketballGlobal basketballGlobal, List<String> list)
			throws Exception {
		logger.info("开始获取蓝球过关赔率信息");
		org.dom4j.Element matchList = getMatchList();
		for (Entry<String, LetVs> entry : basketballGlobal.getLetvsmap()
				.entrySet()) {
			String id = entry.getKey();
			if (list.contains(id)) {
				LetVs letvs = entry.getValue();
				Basketball_Vs vs = basketballGlobal.getVs(id);
				Diff diff = basketballGlobal.getDiff(id);
				Bs bs = basketballGlobal.getBs(id);
				org.dom4j.Element item = matchList.addElement("item");
				item.addElement("id").setText(id);
				addElement(Basketball_Vs.class, vs, item);
				addElement(LetVs.class, letvs, item);
				addElement(Bs.class, bs, item);
				addElement(Diff.class, diff, item);
			}
		}
		logger.info("获取蓝球过关赔率信息结束");
		return matchList.getDocument();
	}

	private BasketballGlobal getBasketballGlobal_Guo() throws Exception {
		logger.info("开始获取蓝球过关赔率");
		BasketballGlobal basketballGlobal = new BasketballGlobal();
		buildBasketball_Guo_J00005(basketballGlobal);
		buildBasketball_Guo_J00006(basketballGlobal);
		buildBasketball_Guo_J00007(basketballGlobal);
		buildBasketball_Guo_J00008(basketballGlobal);
		return basketballGlobal;
	}

	private void buildBasketball_Guo_J00005(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_GUO_J00005, url:{}",
				new String[] { BASKETBALL_GUO_J00005 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_GUO_J00005);
		org.jsoup.nodes.Element table = doc.select(".tbl").get(1);
		List<org.jsoup.nodes.Element> trs = table.select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 8) {
				buildBasketball_Guo_J00005(tds, basketballGlobal);
			}
		}
		logger.info("获取BASKETBALL_GUO_J00005结束, url:{}",
				new String[] { BASKETBALL_GUO_J00005 });
	}

	private void buildBasketball_Guo_J00005(List<org.jsoup.nodes.Element> tds,
			BasketballGlobal basketballGlobal) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);
		String league = trim(tds.get(1).text());
		String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}
		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		String v0 = trim(tds.get(4).text());
		String v3 = trim(tds.get(5).text());
		basketballGlobal.putVs(id, new Basketball_Vs(v0, v3));
	}

	private void buildBasketball_Guo_J00006(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_GUO_J00006, url:{}",
				new String[] { BASKETBALL_GUO_J00006 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_GUO_J00006);
		List<org.jsoup.nodes.Element> trs = doc.select(".tbl").select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 8) {
				buildBasketball_Guo_J00006(basketballGlobal, tds);
			}
		}
		logger.info("获取BASKETBALL_GUO_J00006结束, url:{}",
				new String[] { BASKETBALL_GUO_J00006 });
	}

	private void buildBasketball_Guo_J00006(BasketballGlobal basketballGlobal,
			List<org.jsoup.nodes.Element> tds) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String team = trim(tds.get(2).text());
		String letpoint = getShuzi(team);
		String v0 = trim(tds.get(4).text());
		String v3 = trim(tds.get(5).text());
		String replace = letpoint;
		if (replace.startsWith("+") || replace.startsWith("-")) {
			replace = "\\" + replace;
		}
		String league = trim(tds.get(1).text());
		team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		team = team.replaceAll("\\(" + replace + "\\)", "");
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}

		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);

		basketballGlobal.putLetvs(id, new LetVs(team.contains(letpoint) ? "0" : letpoint, v0, v3));
	}

	private void buildBasketball_Guo_J00007(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_GUO_J00007, url:{}",
				new String[] { BASKETBALL_GUO_J00007 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_GUO_J00007);
		List<org.jsoup.nodes.Element> trs = doc.select(".tbl").select("tr");
		Date date = getDate();
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 11) {
				buildBasketball_Guo_J00007(basketballGlobal, tds, date);
			}
		}
		logger.info("获取BASKETBALL_GUO_J00007结束, url:{}",
				new String[] { BASKETBALL_GUO_J00007 });
	}

	private void buildBasketball_Guo_J00007(BasketballGlobal basketballGlobal,
			List<org.jsoup.nodes.Element> tds, Date date) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);
		String league = trim(tds.get(1).text());
		String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		team = team.substring(0, team.length() - 1);
		team = team.substring(0,
				team.indexOf("(") <= 0 ? team.length() : team.indexOf("("));
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}
		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		String[] values = tds.get(5).ownText().split(" ");
		String v11 = trim(values[0]);
		String v01 = trim(values[1]);
		values = tds.get(6).ownText().split(" ");
		String v12 = trim(values[0]);
		String v02 = trim(values[1]);
		values = tds.get(7).ownText().split(" ");
		String v13 = trim(values[0]);
		String v03 = trim(values[1]);
		values = tds.get(8).ownText().split(" ");
		String v14 = trim(values[0]);
		String v04 = trim(values[1]);
		values = tds.get(9).ownText().split(" ");
		String v15 = trim(values[0]);
		String v05 = trim(values[1]);
		values = tds.get(10).ownText().split(" ");
		String v16 = trim(values[0]);
		String v06 = trim(values[1]);
		basketballGlobal.putDiff(id, new Diff(v01, v02, v03, v04, v05, v06,
				v11, v12, v13, v14, v15, v16));
	}

	private void buildBasketball_Guo_J00008(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_GUO_J00008, url:{}",
				new String[] { BASKETBALL_GUO_J00008 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_GUO_J00008);
		List<org.jsoup.nodes.Element> trs = doc.select(".tbl").select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 7) {
				buildBasketball_Guo_J00008(basketballGlobal, tds);
			}
		}
		logger.info("获取BASKETBALL_GUO_J00008结束, url:{}",
				new String[] { BASKETBALL_GUO_J00008 });
	}

	private void buildBasketball_Guo_J00008(BasketballGlobal basketballGlobal,
			List<org.jsoup.nodes.Element> tds) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String team = trim(tds.get(2).text());
		String basepoint = trim(getShuzi(team));
		String g = trim(tds.get(4).text());
		String l = trim(tds.get(5).text());

		String league = trim(tds.get(1).text());
		team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		team = team.replaceAll(basepoint, "");
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}
		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		basketballGlobal.putBs(id, new Bs(basepoint, g, l));
	}

	private org.dom4j.Document getPeilu_Basketball_Dan(
			BasketballGlobal basketballGlobal, List<String> list)
			throws Exception {
		logger.info("开始获取蓝球单关赔率信息");
		org.dom4j.Element matchList = getMatchList();
		for (Entry<String, LetVs> entry : basketballGlobal.getLetvsmap()
				.entrySet()) {
			String id = entry.getKey();
			if (list.contains(id)) {
				LetVs letvs = entry.getValue();
				Basketball_Vs vs = basketballGlobal.getVs(id);
				Diff diff = basketballGlobal.getDiff(id);
				Bs bs = basketballGlobal.getBs(id);
				org.dom4j.Element item = matchList.addElement("item");
				item.addElement("id").setText(id);
				addElement(Basketball_Vs.class, vs, item);
				addElement(LetVs.class, letvs, item);
				addElement(Bs.class, bs, item);
				addElement(Diff.class, diff, item);
			}
		}
		logger.info("获取蓝球单关赔率信息结束");
		return matchList.getDocument();
	}

	private BasketballGlobal getBasketballGlobal_DAN() throws Exception {
		logger.info("开始获取蓝球单关赔率");
		BasketballGlobal basketballGlobal = new BasketballGlobal();
		try {
			buildBasketball_Dan_J00005(basketballGlobal);
		} catch (Exception e) {
		}
		try {
			buildBasketball_Dan_J00006(basketballGlobal);
		} catch (Exception e) {
		}
		try {
			buildBasketball_Dan_J00007(basketballGlobal);
		} catch (Exception e) {
		}
		try {
			buildBasketball_Dan_J00008(basketballGlobal);
		} catch (Exception e) {
		}
		return basketballGlobal;
	}

	private void buildBasketball_Dan_J00008(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_DAN_J00008, url:{}",
				new String[] { BASKETBALL_DAN_J00008 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_DAN_J00008);
		List<org.jsoup.nodes.Element> trs = doc.select(".tbl").select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 8) {
				buildBasketball_Dan_J00008(basketballGlobal, tds);
			}
		}
		logger.info("获取BASKETBALL_DAN_J00008结束, url:{}",
				new String[] { BASKETBALL_DAN_J00008 });
	}

	private void buildBasketball_Dan_J00008(BasketballGlobal basketballGlobal,
			List<org.jsoup.nodes.Element> tds) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String league = trim(tds.get(1).text());
		String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}

		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		String basepoint = trim(tds.get(4).text());
		String g = trim(tds.get(5).text());
		String l = trim(tds.get(6).text());
		basketballGlobal.putBs(id, new Bs(basepoint, g, l));
	}

	private void buildBasketball_Dan_J00007(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_DAN_J00007, url:{}",
				new String[] { BASKETBALL_DAN_J00007 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_DAN_J00007);
		List<org.jsoup.nodes.Element> trs = doc.select(".tbl").select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 11) {
				buildBasketball_Dan_J00007(basketballGlobal, tds);
			}
		}
		logger.info("获取BASKETBALL_DAN_J00007结束, url:{}",
				new String[] { BASKETBALL_DAN_J00007 });
	}

	private void buildBasketball_Dan_J00007(BasketballGlobal basketballGlobal,
			List<org.jsoup.nodes.Element> tds) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String league = trim(tds.get(1).text());
		String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}

		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		String[] values = tds.get(5).ownText().split(" ");
		String v11 = trim(values[0]);
		String v01 = trim(values[1]);
		values = tds.get(6).ownText().split(" ");
		String v12 = trim(values[0]);
		String v02 = trim(values[1]);
		values = tds.get(7).ownText().split(" ");
		String v13 = trim(values[0]);
		String v03 = trim(values[1]);
		values = tds.get(8).ownText().split(" ");
		String v14 = trim(values[0]);
		String v04 = trim(values[1]);
		values = tds.get(9).ownText().split(" ");
		String v15 = trim(values[0]);
		String v05 = trim(values[1]);
		values = tds.get(10).ownText().split(" ");
		String v16 = trim(values[0]);
		String v06 = trim(values[1]);
		basketballGlobal.putDiff(id, new Diff(v01, v02, v03, v04, v05, v06,
				v11, v12, v13, v14, v15, v16));
	}

	private void buildBasketball_Dan_J00006(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_DAN_J00006, url:{}",
				new String[] { BASKETBALL_DAN_J00006 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_DAN_J00006);
		List<org.jsoup.nodes.Element> trs = doc.select(".tbl").select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 9) {
				buildBasketball_Dan_J00006(basketballGlobal, tds);
			}
		}
		logger.info("获取BASKETBALL_DAN_J00006结束, url:{}",
				new String[] { BASKETBALL_DAN_J00006 });
	}

	private void buildBasketball_Dan_J00006(BasketballGlobal basketballGlobal,
			List<org.jsoup.nodes.Element> tds) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String team = trim(tds.get(2).text());
		String letpoint = getShuzi(team);
		String v0 = trim(tds.get(4).text());
		String v3 = trim(tds.get(5).text());

		String league = trim(tds.get(1).text());
		team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		String replace = letpoint;
		if (replace.startsWith("+") || replace.startsWith("-")) {
			replace = "\\" + replace;
		}
		team = team.replaceAll("\\(" + replace + "\\)", "");
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}

		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);

		basketballGlobal.putLetvs(id, new LetVs(team.contains(letpoint) ? "0" : letpoint, v0, v3));
	}

	private void buildBasketball_Dan_J00005(BasketballGlobal basketballGlobal)
			throws Exception {
		logger.info("开始获取BASKETBALL_DAN_J00005, url:{}",
				new String[] { BASKETBALL_DAN_J00005 });
		org.jsoup.nodes.Document doc = getJsoupDocument(BASKETBALL_DAN_J00005);
		org.jsoup.nodes.Element table = doc.select(".tbl").first();
		List<org.jsoup.nodes.Element> trs = table.select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 6) {
				buildBasketball_Dan_J00005(tds, basketballGlobal);
			}
		}
		logger.info("获取FOOTBALL_DAN_J00001结束, url:{}",
				new String[] { FOOTBALL_DAN_J00001 });
	}

	private void buildBasketball_Dan_J00005(List<org.jsoup.nodes.Element> tds,
			BasketballGlobal basketballGlobal) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String league = trim(tds.get(1).text());
		String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		String[] teams = team.split("\\:");
		team = teams[1] + ":" + teams[0];
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ZERO, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}

		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		String v0 = trim(tds.get(4).text());
		String v3 = trim(tds.get(5).text());
		basketballGlobal.putVs(id, new Basketball_Vs(v0, v3));
	}

	private org.dom4j.Document getPeilu_Football_Dan(
			FootballGlobal footballGlobal, List<String> list) throws Exception {
		org.dom4j.Element matchList = getMatchList();
		for (Entry<String, Football_Vs> entry : footballGlobal.getVsmap()
				.entrySet()) {
			String id = entry.getKey();
			if (list.contains(id)) {
				Football_Vs vs = entry.getValue();
				Score score = footballGlobal.getScore(id);
				Goal goal = footballGlobal.getGoal(id);
				Half half = footballGlobal.getHalf(id);
				org.dom4j.Element item = matchList.addElement("item");
				item.addElement("id").setText(id);
				addElement(Football_Vs.class, vs, item);
				addElement(Score.class, score, item);
				addElement(Goal.class, goal, item);
				addElement(Half.class, half, item);
			}
		}
		logger.info("获取足球单关赔率信息结束");
		return matchList.getDocument();
	}

	public void addElement(Class<?> clazz, Object object, org.dom4j.Element item)
			throws Exception {
		String[] names = clazz.getSimpleName().split("\\_");
		int index = 0;
		if (names.length > 1) {
			index = 1;
		}
		String name = names[index].substring(0, 1).toLowerCase()
				+ names[index].substring(1);
		if (null == object) {
			item.addElement(name);
			return;
		}
		BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
		org.dom4j.Element element = item.addElement(name);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			if ("class".equals(pd.getName())) {
				continue;
			}
			element.addElement(pd.getName()).setText(
					pd.getReadMethod().invoke(object, new Object[] {})
							.toString());
		}
	}

	private FootballGlobal getFootballGlobal_Dan() throws Exception {
		logger.info("开始获取竞彩足球单关赔率");
		FootballGlobal footballGlobal = new FootballGlobal();
		try {
			buildFootball_Dan_J00001(footballGlobal);
		} catch (Exception e) {
		}
		try {
			buildFootball_Dan_J00002(footballGlobal);
		} catch (Exception e) {
		}
		try {
			buildFootball_Dan_J00003(footballGlobal);
		} catch (Exception e) {
		}
		try {
			buildFootball_Dan_J00004(footballGlobal);
		} catch (Exception e) {
		}
		return footballGlobal;
	}

	private void buildFootball_Dan_J00004(FootballGlobal footballGlobal)
			throws Exception {
		logger.info("开始获取FOOTBALL_DAN_J00004, url:{}",
				new String[] { FOOTBALL_DAN_J00004 });
		org.jsoup.nodes.Document doc = getJsoupDocument(FOOTBALL_DAN_J00004);
		org.jsoup.nodes.Element element = doc.getElementById("jumpTable");
		List<org.jsoup.nodes.Element> trs = element.select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 13) {
				buildFootball_Dan_J00004(tds, footballGlobal);
			}
		}
		logger.info("获取FOOTBALL_DAN_J00004结束, url:{}",
				new String[] { FOOTBALL_DAN_J00004 });
	}

	private void buildFootball_Dan_J00004(List<org.jsoup.nodes.Element> tds,
			FootballGlobal footballGlobal) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String league = trim(tds.get(1).text());
		String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ONE, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}

		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		String v33 = trim(tds.get(4).text());
		String v31 = trim(tds.get(5).text());
		String v30 = trim(tds.get(6).text());
		String v13 = trim(tds.get(7).text());
		String v11 = trim(tds.get(8).text());
		String v10 = trim(tds.get(9).text());
		String v03 = trim(tds.get(10).text());
		String v01 = trim(tds.get(11).text());
		String v00 = trim(tds.get(12).text());
		footballGlobal.putHalf(id, new Half(v00, v01, v03, v10, v11, v13, v30,
				v31, v33));
	}

	private void buildFootball_Dan_J00003(FootballGlobal footballGlobal)
			throws Exception {
		logger.info("开始获取FOOTBALL_DAN_J00003, url:{}",
				new String[] { FOOTBALL_DAN_J00003 });
		org.jsoup.nodes.Document doc = getJsoupDocument(FOOTBALL_DAN_J00003);
		org.jsoup.nodes.Element element = doc.getElementById("jumpTable");
		List<org.jsoup.nodes.Element> trs = element.select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 12) {
				buildFootball_Dan_J00003(tds, footballGlobal);
			}
		}
		logger.info("获取FOOTBALL_DAN_J00003结束, url:{}",
				new String[] { FOOTBALL_DAN_J00003 });
	}

	private void buildFootball_Dan_J00003(List<org.jsoup.nodes.Element> tds,
			FootballGlobal footballGlobal) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String league = trim(tds.get(1).text());
		String team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ONE, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}

		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		String v0 = trim(tds.get(4).text());
		String v1 = trim(tds.get(5).text());
		String v2 = trim(tds.get(6).text());
		String v3 = trim(tds.get(7).text());
		String v4 = trim(tds.get(8).text());
		String v5 = trim(tds.get(9).text());
		String v6 = trim(tds.get(10).text());
		String v7 = trim(tds.get(11).text());
		footballGlobal.putGoal(id, new Goal(v0, v1, v2, v3, v4, v5, v6, v7));
	}

	private void buildFootball_Dan_J00002(FootballGlobal footballGlobal)
			throws Exception {
		logger.info("开始获取FOOTBALL_DAN_J00002, url:{}",
				new String[] { FOOTBALL_DAN_J00002 });
		org.jsoup.nodes.Document doc = getJsoupDocument(FOOTBALL_DAN_J00002);
		List<org.jsoup.nodes.Element> divs = doc.select(".titleScore");
		for (org.jsoup.nodes.Element div : divs) {
			org.jsoup.nodes.Element h3 = div.select("h3").first();
			String value = h3.text();
			String[] values = value.split("\\|");
			String event = trim(values[0]).substring(0, 5);
			BigDecimal weekid = WEEKID.get(event.substring(0, 2));
			String teamid = event.substring(2);

			Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
					BigDecimal.ONE, weekid, teamid, trim(values[1]),
					trim(values[2]).replaceAll("\\s*VS\\s*", ":"));
			if (null == tjingcaimatches) {
				continue;
			}
			String id = StringUtil.join("_", tjingcaimatches.getDay(),
					String.valueOf(weekid.intValue()), teamid);
			buildFootball_Dan_J00002(id, footballGlobal, div.select("table")
					.first());
		}
		logger.info("获取FOOTBALL_DAN_J00002结束, url:{}",
				new String[] { FOOTBALL_DAN_J00002 });
	}

	private void buildFootball_Dan_J00002(String id,
			FootballGlobal footballGlobal, org.jsoup.nodes.Element table) {
		Score score = new Score();
		List<org.jsoup.nodes.Element> trs = table.select("tr");
		org.jsoup.nodes.Element tr = trs.get(0);
		List<org.jsoup.nodes.Element> tds = tr.select("td");
		score.setV10(trim(tds.get(1).text().substring(3)));
		score.setV20(trim(tds.get(2).text().substring(3)));
		score.setV21(trim(tds.get(3).text().substring(3)));
		score.setV30(trim(tds.get(4).text().substring(3)));
		score.setV31(trim(tds.get(5).text().substring(3)));
		score.setV32(trim(tds.get(6).text().substring(3)));
		score.setV40(trim(tds.get(7).text().substring(3)));
		score.setV41(trim(tds.get(8).text().substring(3)));
		score.setV42(trim(tds.get(9).text().substring(3)));
		score.setV50(trim(tds.get(10).text().substring(3)));
		score.setV51(trim(tds.get(11).text().substring(3)));
		score.setV52(trim(tds.get(12).text().substring(3)));
		score.setV90(trim(tds.get(13).text().substring(3)));
		tr = trs.get(1);
		tds = tr.select("td");
		score.setV00(trim(tds.get(1).text().substring(3)));
		score.setV11(trim(tds.get(2).text().substring(3)));
		score.setV22(trim(tds.get(3).text().substring(3)));
		score.setV33(trim(tds.get(4).text().substring(3)));
		score.setV99(trim(tds.get(5).text().substring(3)));
		tr = trs.get(2);
		tds = tr.select("td");
		score.setV01(trim(tds.get(1).text().substring(3)));
		score.setV02(trim(tds.get(2).text().substring(3)));
		score.setV12(trim(tds.get(3).text().substring(3)));
		score.setV03(trim(tds.get(4).text().substring(3)));
		score.setV13(trim(tds.get(5).text().substring(3)));
		score.setV23(trim(tds.get(6).text().substring(3)));
		score.setV04(trim(tds.get(7).text().substring(3)));
		score.setV14(trim(tds.get(8).text().substring(3)));
		score.setV24(trim(tds.get(9).text().substring(3)));
		score.setV05(trim(tds.get(10).text().substring(3)));
		score.setV15(trim(tds.get(11).text().substring(3)));
		score.setV25(trim(tds.get(12).text().substring(3)));
		score.setV09(trim(tds.get(13).text().substring(3)));
		footballGlobal.putScore(id, score);
	}

	private void buildFootball_Dan_J00001(FootballGlobal footballGlobal)
			throws Exception {
		logger.info("开始获取FOOTBALL_DAN_J00001, url:{}",
				new String[] { FOOTBALL_DAN_J00001 });
		org.jsoup.nodes.Document doc = getJsoupDocument(FOOTBALL_DAN_J00001);
		org.jsoup.nodes.Element table = doc.getElementById("jumpTable");
		List<org.jsoup.nodes.Element> trs = table.select("tr");
		for (org.jsoup.nodes.Element tr : trs) {
			List<org.jsoup.nodes.Element> tds = tr.select("td");
			if (null != tds && tds.size() == 8) {
				buildFootball_Dan_J00001(tds, footballGlobal);
			}
		}
		logger.info("获取FOOTBALL_DAN_J00001结束, url:{}",
				new String[] { FOOTBALL_DAN_J00001 });
	}

	private void buildFootball_Dan_J00001(List<org.jsoup.nodes.Element> tds,
			FootballGlobal footballGlobal) throws Exception {
		String event = trim(tds.get(0).text());
		BigDecimal weekid = WEEKID.get(event.substring(0, 2));
		String teamid = event.substring(2);

		String v3 = trim(tds.get(4).text());
		String v1 = trim(tds.get(5).text());
		String v0 = trim(tds.get(6).text());
		String team = trim(tds.get(2).text());
		String letpoint = getShuzi(team);
		String replace = letpoint;
		if (replace.startsWith("+") || replace.startsWith("-")) {
			replace = "\\" + replace;
		}
		String league = trim(tds.get(1).text());
		team = trim(tds.get(2).text()).replaceAll("\\s*VS\\s*", ":");
		team = team.replaceAll("\\(" + replace + "\\)", "");
		Tjingcaimatches tjingcaimatches = tjingcaiDao.findTjingcaimatches(
				BigDecimal.ONE, weekid, teamid, league, team);
		if (null == tjingcaimatches) {
			return;
		}
		String id = StringUtil.join("_", tjingcaimatches.getDay(),
				String.valueOf(weekid.intValue()), teamid);
		footballGlobal.putVs(id, new Football_Vs(v0, v1, v3, team.contains(letpoint) ? "0" : letpoint));
	}

	private org.jsoup.nodes.Document getJsoupDocument(String url)
			throws Exception {
		return org.jsoup.Jsoup.connect(url).timeout(timeout).get();
	}

	private String getShuzi(String team) {
		Pattern pattern = Pattern.compile("(\\+?\\-?\\d+\\.?\\d*)");
		Matcher matcher = pattern.matcher(team);
		String result = null;
		while (matcher.find()) {
			result = matcher.group(1);
		}
		return null == result ? "0" : result;
	}

	private String trim(String content) {
		content = content.replaceAll("\\s*", "");
		content = content.replaceAll(" *", "");
		return content;
	}

	public Date getDate() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTime();
	}

	class Football_Vs {
		private String v0;
		private String v1;
		private String v3;
		private String letPoint;

		public Football_Vs(String v0, String v1, String v3, String letPoint) {
			this.v0 = v0;
			this.v1 = v1;
			this.v3 = v3;
			this.letPoint = letPoint;
		}

		public String getV0() {
			return v0;
		}

		public void setV0(String v0) {
			this.v0 = v0;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV3() {
			return v3;
		}

		public void setV3(String v3) {
			this.v3 = v3;
		}

		public String getLetPoint() {
			return letPoint;
		}

		public void setLetPoint(String letPoint) {
			this.letPoint = letPoint;
		}
	}

	class Score {
		private String v00, v01, v02, v03, v04, v05, v09, v10, v11, v12, v13,
				v14, v15, v20, v21, v22, v23, v24, v25, v30, v31, v32, v33,
				v40, v41, v42, v50, v51, v52, v90, v99;

		public String getV00() {
			return v00;
		}

		public void setV00(String v00) {
			this.v00 = v00;
		}

		public String getV01() {
			return v01;
		}

		public void setV01(String v01) {
			this.v01 = v01;
		}

		public String getV02() {
			return v02;
		}

		public void setV02(String v02) {
			this.v02 = v02;
		}

		public String getV03() {
			return v03;
		}

		public void setV03(String v03) {
			this.v03 = v03;
		}

		public String getV04() {
			return v04;
		}

		public void setV04(String v04) {
			this.v04 = v04;
		}

		public String getV05() {
			return v05;
		}

		public void setV05(String v05) {
			this.v05 = v05;
		}

		public String getV09() {
			return v09;
		}

		public void setV09(String v09) {
			this.v09 = v09;
		}

		public String getV10() {
			return v10;
		}

		public void setV10(String v10) {
			this.v10 = v10;
		}

		public String getV11() {
			return v11;
		}

		public void setV11(String v11) {
			this.v11 = v11;
		}

		public String getV12() {
			return v12;
		}

		public void setV12(String v12) {
			this.v12 = v12;
		}

		public String getV13() {
			return v13;
		}

		public void setV13(String v13) {
			this.v13 = v13;
		}

		public String getV14() {
			return v14;
		}

		public void setV14(String v14) {
			this.v14 = v14;
		}

		public String getV15() {
			return v15;
		}

		public void setV15(String v15) {
			this.v15 = v15;
		}

		public String getV20() {
			return v20;
		}

		public void setV20(String v20) {
			this.v20 = v20;
		}

		public String getV21() {
			return v21;
		}

		public void setV21(String v21) {
			this.v21 = v21;
		}

		public String getV22() {
			return v22;
		}

		public void setV22(String v22) {
			this.v22 = v22;
		}

		public String getV23() {
			return v23;
		}

		public void setV23(String v23) {
			this.v23 = v23;
		}

		public String getV24() {
			return v24;
		}

		public void setV24(String v24) {
			this.v24 = v24;
		}

		public String getV25() {
			return v25;
		}

		public void setV25(String v25) {
			this.v25 = v25;
		}

		public String getV30() {
			return v30;
		}

		public void setV30(String v30) {
			this.v30 = v30;
		}

		public String getV31() {
			return v31;
		}

		public void setV31(String v31) {
			this.v31 = v31;
		}

		public String getV32() {
			return v32;
		}

		public void setV32(String v32) {
			this.v32 = v32;
		}

		public String getV33() {
			return v33;
		}

		public void setV33(String v33) {
			this.v33 = v33;
		}

		public String getV40() {
			return v40;
		}

		public void setV40(String v40) {
			this.v40 = v40;
		}

		public String getV41() {
			return v41;
		}

		public void setV41(String v41) {
			this.v41 = v41;
		}

		public String getV42() {
			return v42;
		}

		public void setV42(String v42) {
			this.v42 = v42;
		}

		public String getV50() {
			return v50;
		}

		public void setV50(String v50) {
			this.v50 = v50;
		}

		public String getV51() {
			return v51;
		}

		public void setV51(String v51) {
			this.v51 = v51;
		}

		public String getV52() {
			return v52;
		}

		public void setV52(String v52) {
			this.v52 = v52;
		}

		public String getV90() {
			return v90;
		}

		public void setV90(String v90) {
			this.v90 = v90;
		}

		public String getV99() {
			return v99;
		}

		public void setV99(String v99) {
			this.v99 = v99;
		}
	}

	class Goal {
		private String v0, v1, v2, v3, v4, v5, v6, v7;

		public Goal(String v0, String v1, String v2, String v3, String v4,
				String v5, String v6, String v7) {
			this.v0 = v0;
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
			this.v4 = v4;
			this.v5 = v5;
			this.v6 = v6;
			this.v7 = v7;
		}

		public String getV0() {
			return v0;
		}

		public void setV0(String v0) {
			this.v0 = v0;
		}

		public String getV1() {
			return v1;
		}

		public void setV1(String v1) {
			this.v1 = v1;
		}

		public String getV2() {
			return v2;
		}

		public void setV2(String v2) {
			this.v2 = v2;
		}

		public String getV3() {
			return v3;
		}

		public void setV3(String v3) {
			this.v3 = v3;
		}

		public String getV4() {
			return v4;
		}

		public void setV4(String v4) {
			this.v4 = v4;
		}

		public String getV5() {
			return v5;
		}

		public void setV5(String v5) {
			this.v5 = v5;
		}

		public String getV6() {
			return v6;
		}

		public void setV6(String v6) {
			this.v6 = v6;
		}

		public String getV7() {
			return v7;
		}

		public void setV7(String v7) {
			this.v7 = v7;
		}
	}

	class Half {
		private String v00, v01, v03, v10, v11, v13, v30, v31, v33;

		public Half(String v00, String v01, String v03, String v10, String v11,
				String v13, String v30, String v31, String v33) {
			this.v00 = v00;
			this.v01 = v01;
			this.v03 = v03;
			this.v10 = v10;
			this.v11 = v11;
			this.v13 = v13;
			this.v30 = v30;
			this.v31 = v31;
			this.v33 = v33;
		}

		public String getV00() {
			return v00;
		}

		public void setV00(String v00) {
			this.v00 = v00;
		}

		public String getV01() {
			return v01;
		}

		public void setV01(String v01) {
			this.v01 = v01;
		}

		public String getV03() {
			return v03;
		}

		public void setV03(String v03) {
			this.v03 = v03;
		}

		public String getV10() {
			return v10;
		}

		public void setV10(String v10) {
			this.v10 = v10;
		}

		public String getV11() {
			return v11;
		}

		public void setV11(String v11) {
			this.v11 = v11;
		}

		public String getV13() {
			return v13;
		}

		public void setV13(String v13) {
			this.v13 = v13;
		}

		public String getV30() {
			return v30;
		}

		public void setV30(String v30) {
			this.v30 = v30;
		}

		public String getV31() {
			return v31;
		}

		public void setV31(String v31) {
			this.v31 = v31;
		}

		public String getV33() {
			return v33;
		}

		public void setV33(String v33) {
			this.v33 = v33;
		}
	}

	class FootballGlobal {
		private Map<String, Football_Vs> vsmap = new LinkedHashMap<String, Football_Vs>();
		private Map<String, Score> scoremap = new HashMap<String, Score>();
		private Map<String, Goal> goalmap = new HashMap<String, Goal>();
		private Map<String, Half> halfmap = new HashMap<String, Half>();

		public void putVs(String id, Football_Vs vs) {
			vsmap.put(id, vs);
		}

		public Football_Vs getVs(String id) {
			return vsmap.get(id);
		}

		public void putScore(String id, Score score) {
			scoremap.put(id, score);
		}

		public Score getScore(String id) {
			return scoremap.get(id);
		}

		public void putGoal(String id, Goal goal) {
			goalmap.put(id, goal);
		}

		public Goal getGoal(String id) {
			return goalmap.get(id);
		}

		public void putHalf(String id, Half half) {
			halfmap.put(id, half);
		}

		public Half getHalf(String id) {
			return halfmap.get(id);
		}

		public Map<String, Football_Vs> getVsmap() {
			return vsmap;
		}

		public void setVsmap(Map<String, Football_Vs> vsmap) {
			this.vsmap = vsmap;
		}

		public Map<String, Score> getScoremap() {
			return scoremap;
		}

		public void setScoremap(Map<String, Score> scoremap) {
			this.scoremap = scoremap;
		}

		public Map<String, Goal> getGoalmap() {
			return goalmap;
		}

		public void setGoalmap(Map<String, Goal> goalmap) {
			this.goalmap = goalmap;
		}

		public Map<String, Half> getHalfmap() {
			return halfmap;
		}

		public void setHalfmap(Map<String, Half> halfmap) {
			this.halfmap = halfmap;
		}
	}

	class Basketball_Vs {
		private String v0;
		private String v3;

		public Basketball_Vs(String v0, String v3) {
			this.v0 = v0;
			this.v3 = v3;
		}

		public String getV0() {
			return v0;
		}

		public void setV0(String v0) {
			this.v0 = v0;
		}

		public String getV3() {
			return v3;
		}

		public void setV3(String v3) {
			this.v3 = v3;
		}
	}

	class LetVs {
		private String letPoint;
		private String v0;
		private String v3;

		public LetVs(String letPoint, String v0, String v3) {
			this.letPoint = letPoint;
			this.v0 = v0;
			this.v3 = v3;
		}

		public String getLetPoint() {
			return letPoint;
		}

		public void setLetPoint(String letPoint) {
			this.letPoint = letPoint;
		}

		public String getV0() {
			return v0;
		}

		public void setV0(String v0) {
			this.v0 = v0;
		}

		public String getV3() {
			return v3;
		}

		public void setV3(String v3) {
			this.v3 = v3;
		}
	}

	class Bs {
		private String basePoint;
		private String g;
		private String l;

		public Bs(String basePoint, String g, String l) {
			this.basePoint = basePoint;
			this.g = g;
			this.l = l;
		}

		public String getBasePoint() {
			return basePoint;
		}

		public void setBasePoint(String basePoint) {
			this.basePoint = basePoint;
		}

		public String getG() {
			return g;
		}

		public void setG(String g) {
			this.g = g;
		}

		public String getL() {
			return l;
		}

		public void setL(String l) {
			this.l = l;
		}
	}

	class Diff {
		private String v01, v02, v03, v04, v05, v06, v11, v12, v13, v14, v15,
				v16;

		public Diff(String v01, String v02, String v03, String v04, String v05,
				String v06, String v11, String v12, String v13, String v14,
				String v15, String v16) {
			this.v01 = v01;
			this.v02 = v02;
			this.v03 = v03;
			this.v04 = v04;
			this.v05 = v05;
			this.v06 = v06;
			this.v11 = v11;
			this.v12 = v12;
			this.v13 = v13;
			this.v14 = v14;
			this.v15 = v15;
			this.v16 = v16;
		}

		public String getV01() {
			return v01;
		}

		public void setV01(String v01) {
			this.v01 = v01;
		}

		public String getV02() {
			return v02;
		}

		public void setV02(String v02) {
			this.v02 = v02;
		}

		public String getV03() {
			return v03;
		}

		public void setV03(String v03) {
			this.v03 = v03;
		}

		public String getV04() {
			return v04;
		}

		public void setV04(String v04) {
			this.v04 = v04;
		}

		public String getV05() {
			return v05;
		}

		public void setV05(String v05) {
			this.v05 = v05;
		}

		public String getV06() {
			return v06;
		}

		public void setV06(String v06) {
			this.v06 = v06;
		}

		public String getV11() {
			return v11;
		}

		public void setV11(String v11) {
			this.v11 = v11;
		}

		public String getV12() {
			return v12;
		}

		public void setV12(String v12) {
			this.v12 = v12;
		}

		public String getV13() {
			return v13;
		}

		public void setV13(String v13) {
			this.v13 = v13;
		}

		public String getV14() {
			return v14;
		}

		public void setV14(String v14) {
			this.v14 = v14;
		}

		public String getV15() {
			return v15;
		}

		public void setV15(String v15) {
			this.v15 = v15;
		}

		public String getV16() {
			return v16;
		}

		public void setV16(String v16) {
			this.v16 = v16;
		}
	}

	class BasketballGlobal {
		private Map<String, Basketball_Vs> vsmap = new HashMap<String, Basketball_Vs>();
		private Map<String, LetVs> letvsmap = new LinkedHashMap<String, LetVs>();
		private Map<String, Bs> bsmap = new HashMap<String, Bs>();
		private Map<String, Diff> diffmap = new HashMap<String, Diff>();

		public void putVs(String id, Basketball_Vs vs) {
			vsmap.put(id, vs);
		}

		public Basketball_Vs getVs(String id) {
			return vsmap.get(id);
		}

		public void putLetvs(String id, LetVs letvs) {
			letvsmap.put(id, letvs);
		}

		public LetVs getLetvs(String id) {
			return letvsmap.get(id);
		}

		public void putBs(String id, Bs bs) {
			bsmap.put(id, bs);
		}

		public Bs getBs(String id) {
			return bsmap.get(id);
		}

		public void putDiff(String id, Diff diff) {
			diffmap.put(id, diff);
		}

		public Diff getDiff(String id) {
			return diffmap.get(id);
		}

		public Map<String, Basketball_Vs> getVsmap() {
			return vsmap;
		}

		public void setVsmap(Map<String, Basketball_Vs> vsmap) {
			this.vsmap = vsmap;
		}

		public Map<String, LetVs> getLetvsmap() {
			return letvsmap;
		}

		public void setLetvsmap(Map<String, LetVs> letvsmap) {
			this.letvsmap = letvsmap;
		}

		public Map<String, Bs> getBsmap() {
			return bsmap;
		}

		public void setBsmap(Map<String, Bs> bsmap) {
			this.bsmap = bsmap;
		}

		public Map<String, Diff> getDiffmap() {
			return diffmap;
		}

		public void setDiffmap(Map<String, Diff> diffmap) {
			this.diffmap = diffmap;
		}
	}
}
