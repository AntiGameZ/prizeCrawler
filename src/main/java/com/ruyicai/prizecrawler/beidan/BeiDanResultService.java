package com.ruyicai.prizecrawler.beidan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.beidan.dao.TBeidanDao;
import com.ruyicai.prizecrawler.util.HttpTookit;

import flexjson.JSONDeserializer;

@Service
public class BeiDanResultService {

	private Logger logger = LoggerFactory.getLogger(BeiDanResultService.class);

	private static final String WEBSITEURL = "http://www.bjlot.com/data";
	private static final String TYPE_SFC = "200";
	private static final String TYPE_ZJQ = "230";
	private static final String TYPE_BQC = "240";
	private static final String TYPE_DS = "210";
	private static final String TYPE_BF = "250";
	private static final String CTRL = "control";
	private static final String DRAW = "draw";

	@Autowired
	private TBeidanDao beidandao;

	public void resultTimer() {
		logger.info("抓取赛果定时开始");
		crawlShengFP();
		crawlZongJQ();
		crawlBanQC();
		crawlDanShuang();
		crawlBiFen();
		logger.info("抓取赛果定时结束");
	}

	public void crawlShengFP(){
		try {
			logger.info("开始抓取北单胜负平赛果");
			findAndPersist(getResultList(TYPE_SFC, "B00001"));
			logger.info("抓取北单胜负平赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单胜负平赛果出错",e);
		}

	}

	public void crawlZongJQ(){
		try {
			logger.info("开始抓取北单总进球赛果");
			findAndPersist(getResultList(TYPE_ZJQ, "B00002"));
			logger.info("抓取北单总进球赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单总进球赛果出错",e);
		}
	}

	public void crawlBanQC(){
		try {
			logger.info("开始抓取北单半全场赛果");
			findAndPersist(getResultList(TYPE_BQC, "B00003"));
			logger.info("抓取北单半全场赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单半全场赛果出错",e);
		}
	}

	public void crawlDanShuang(){
		try {
			logger.info("开始抓取北单单双赛果");
			findAndPersist(getResultList(TYPE_DS, "B00004"));
			logger.info("抓取北单单双赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单单双赛果出错",e);
		}
	}

	public void crawlBiFen(){
		try {
			logger.info("开始抓取北单比分赛果");
			findAndPersist(getResultList(TYPE_BF, "B00005"));
			logger.info("抓取北单比分赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单比分赛果出错",e);
		}
	}

	private void findAndPersist(List<TBeiDanResult> list) {
		for (TBeiDanResult result : list) {
			if (beidandao.findBeidanResult(result.getLotno(),
					result.getBatchcode(), result.getNo()) == null) {
				logger.info("保存北单赛果" + result.toString());
				beidandao.persist(result);
			} else {
				logger.info("北单赛果{} {} {}已经存在",
						new String[] { result.getLotno(),
								result.getBatchcode(), result.getNo() });
			}
		}
	}

	private List<TBeiDanResult> getResultList(String type, String lotno) {
		List<TBeiDanResult> list = new ArrayList<TBeiDanResult>();
		String currentIssue = getMaxIssue(type);
		String[] params = currentIssue.split("\\_");
		String resultJson = HttpTookit.doGet(WEBSITEURL + "/" + type + "/"
				+ DRAW + "/" + params[0] + "/" + params[2] + ".js", "_="
				+ System.currentTimeMillis());

		int point = resultJson.indexOf("{");
		resultJson = resultJson.substring(point);

		JSONDeserializer<Map<String, Object>> des = new JSONDeserializer<Map<String, Object>>();
		Map<String, Object> resultmap = des.deserialize(resultJson);

		List<Map<String, String>> results = (List<Map<String, String>>) resultmap
				.get("drawresult");
		for (Map<String, String> result : results) {
			TBeiDanResult beidanresult = new TBeiDanResult();
			beidanresult.setLotno(lotno);
			beidanresult.setBatchcode(params[2]);
			beidanresult.setNo(result.get("matchno"));
			beidanresult.setIscancel(BigDecimal.ZERO);
			beidanresult.setScorehalf("");
			beidanresult.setScoreall(result.get("score"));
			String saiguo = result.get("result").replace("-", "")
					.replace(":", "").replace("胜其他", "90").replace("负其他", "09")
					.replace("平其他", "99").replace("上单", "1").replace("上双", "2")
					.replace("下单", "3").replace("下双", "4");
			beidanresult.setResult(saiguo);
			beidanresult.setPeilu(result.get("spvalue"));
			if(saiguo.equals("*")) {
				beidanresult.setIscancel(BigDecimal.ONE);
				beidanresult.setPeilu("1");
			}
			
			beidanresult.setHandicap(new BigDecimal(result.get("handicap")));
			beidanresult.setCreatetime(new Date());
			beidanresult.setAudit(BigDecimal.ONE);
			list.add(beidanresult);
		}

		return list;
	}

	/**
	 * 获得最大的年
	 * 
	 * @param type
	 * @return
	 */
	private String getMaxYear(String type) {
		String drawyears = HttpTookit.doGet(WEBSITEURL + "/" + type + "/"
				+ CTRL + "/" + "drawyearlist.js", "");

		List<Integer> years = new ArrayList<Integer>();
		Pattern p = Pattern.compile("2[0-9]{3}");
		Matcher m = p.matcher(drawyears);

		while (m.find()) {
			years.add(Integer.parseInt(m.group()));
			drawyears = drawyears.replace(m.group(), "");
			m = p.matcher(drawyears);
		}

		Integer[] years_int = new Integer[years.size()];
		years_int = years.toArray(years_int);
		Arrays.sort(years_int);

		return String.valueOf(years_int[years_int.length - 1]);
	}

	/**
	 * 获得最大的年_月 格式2013_04
	 * 
	 * @param type
	 * @return
	 */
	private String getMaxMonth(String type) {

		String maxYear = getMaxYear(type);
		String drawmonths = HttpTookit.doGet(WEBSITEURL + "/" + type + "/"
				+ CTRL + "/" + maxYear + ".js", "");

		List<Integer> months = new ArrayList<Integer>();
		Pattern p = Pattern.compile("0[1-9]|1[0-2]");
		Matcher m = p.matcher(drawmonths);

		while (m.find()) {
			months.add(Integer.parseInt(m.group()));
			drawmonths = drawmonths.replace(m.group(), "");
			m = p.matcher(drawmonths);
		}

		Integer[] months_int = new Integer[months.size()];
		months_int = months.toArray(months_int);
		Arrays.sort(months_int);
		int maxMonthInt = months_int[months_int.length - 1];

		return maxYear + "_"
				+ (maxMonthInt < 10 ? "0" + maxMonthInt : maxMonthInt);
	}

	private String getMaxIssue(String type) {
		String year_month = getMaxMonth(type);

		String drawissue = HttpTookit.doGet(WEBSITEURL + "/" + type + "/"
				+ CTRL + "/drawnolist_" + year_month.replace("_", "") + ".js",
				"");

		List<Integer> issues = new ArrayList<Integer>();
		Pattern p = Pattern.compile("[0-9]{5}");
		Matcher m = p.matcher(drawissue);

		while (m.find()) {
			issues.add(Integer.parseInt(m.group()));
			drawissue = drawissue.replace(m.group(), "");
			m = p.matcher(drawissue);
		}

		Integer[] issues_int = new Integer[issues.size()];
		issues_int = issues.toArray(issues_int);
		Arrays.sort(issues_int);

		return year_month + "_"
				+ String.valueOf(issues_int[issues_int.length - 1]);
	}
}
