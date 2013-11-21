package com.ruyicai.prizecrawler.beidan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.beidan.dao.TBeidanDao;
import com.ruyicai.prizecrawler.util.HttpTookit;
import com.ruyicai.prizecrawler.util.SendSMS;

import flexjson.JSONDeserializer;

@Service
public class BeiDanResultService {

	private Logger logger = LoggerFactory.getLogger(BeiDanResultService.class);

	private static final String RESULT_WEBSITEURL = "http://www.bjlot.com/data";
	private static final String MATCH_RESULT_URL = "http://www.bjlot.com/ssm/ssm";
	private static final String TYPE_SFC = "200";
	private static final String TYPE_ZJQ = "230";
	private static final String TYPE_BQC = "240";
	private static final String TYPE_DS = "210";
	private static final String TYPE_BF = "250";
	private static final String CTRL = "control";
	private static final String DRAW = "draw";

	
	@Value("${msg.station}")
	private String msgstation;
	
	@Autowired
	private SendSMS sendSMS;
	
	@Autowired
	private TBeidanDao beidandao;

	public void resultTimer() {
		logger.info("抓取赛果定时开始");
//		getShengPFResult();
//		getZongJQResult();
//		getBanQCResult();
//		getDanShuangResult();
//		getBiFenResult();
		
		crawlShengFPResultPage();
		crawlZongJQResultPage();
		crawlBanQCResultPage();
		crawlDanShuangResultPage();
		crawlBiFenResultPage();
		logger.info("抓取赛果定时结束");
	}
	
	public void crawResultPage(String lotno,String year,String batchcode) {
		if("B00001".equals(lotno)) {
			findAndPersist(getResultList(TYPE_SFC, "B00001",year,batchcode));
		}else if("B00002".equals(lotno)) {
			findAndPersist(getResultList(TYPE_ZJQ, "B00002",year,batchcode));
		}else if("B00003".equals(lotno)) {
			findAndPersist(getResultList(TYPE_BQC, "B00003",year,batchcode));
		}else if("B00004".equals(lotno)) {
			findAndPersist(getResultList(TYPE_DS, "B00004",year,batchcode));
		}else if("B00005".equals(lotno)) {
			findAndPersist(getResultList(TYPE_BF, "B00005",year,batchcode));
		}
	}
	
	public void crawResultMatchPage(String lotno) {
		if("B00001".equals(lotno)) {
			getShengPFResult();
		}else if("B00002".equals(lotno)) {
			getZongJQResult();
		}else if("B00003".equals(lotno)) {
			getBanQCResult();
		}else if("B00004".equals(lotno)) {
			getDanShuangResult();
		}else if("B00005".equals(lotno)) {
			getBiFenResult();
		}
	}
	
	
	

	private void crawlShengFPResultPage(){
		try {
			logger.info("开始抓取北单胜负平赛果");
			findAndPersist(getResultList(TYPE_SFC, "B00001",null,null));
			logger.info("抓取北单胜负平赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单胜负平赛果出错",e);
		}

	}

	private void crawlZongJQResultPage(){
		try {
			logger.info("开始抓取北单总进球赛果");
			findAndPersist(getResultList(TYPE_ZJQ, "B00002",null,null));
			logger.info("抓取北单总进球赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单总进球赛果出错",e);
		}
	}

	private void crawlBanQCResultPage(){
		try {
			logger.info("开始抓取北单半全场赛果");
			findAndPersist(getResultList(TYPE_BQC, "B00003",null,null));
			logger.info("抓取北单半全场赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单半全场赛果出错",e);
		}
	}

	private void crawlDanShuangResultPage(){
		try {
			logger.info("开始抓取北单单双赛果");
			findAndPersist(getResultList(TYPE_DS, "B00004",null,null));
			logger.info("抓取北单单双赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单单双赛果出错",e);
		}
	}

	private void crawlBiFenResultPage(){
		try {
			logger.info("开始抓取北单比分赛果");
			findAndPersist(getResultList(TYPE_BF, "B00005",null,null));
			logger.info("抓取北单比分赛果结束");
		} catch (Exception e) {
			logger.info("抓取北单比分赛果出错",e);
		}
	}

	private void findAndPersist(List<TBeiDanResult> list) {
		int newresult = 0;
		for (TBeiDanResult result : list) {
			if (beidandao.findBeidanResult(result.getLotno(),
					result.getBatchcode(), result.getNo()) == null) {
				logger.info("保存北单赛果" + result.toString());
				beidandao.persist(result);
				newresult = newresult + 1;
			} else {
				logger.info("北单赛果{} {} {}已经存在",
						new String[] { result.getLotno(),
								result.getBatchcode(), result.getNo() });
			}
		}
		
		if(newresult>0) {
			sendSMS.sendSMS(msgstation+sendSMS.beidanResultMsg);
		}
	}

	private List<TBeiDanResult> getResultList(String type, String lotno,String year,String batchcode) {
		List<TBeiDanResult> list = new ArrayList<TBeiDanResult>();
		
		String[] params = null;
		if(year!=null&&!year.equals("")&&batchcode!=null&&!batchcode.equals("")) {
			params = new String[]{year,"",batchcode};
		}else {
			String currentIssue = getMaxIssue(type);
			params = currentIssue.split("\\_");
		}
		String resultJson = HttpTookit.doGet(RESULT_WEBSITEURL + "/" + type + "/"
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
			beidanresult.setBatchcode("201"+params[2]);
			beidanresult.setNo(result.get("matchno"));
			beidanresult.setIscancel(BigDecimal.ZERO);
			beidanresult.setScorehalf("");
			beidanresult.setScoreall(result.get("score"));
			String saiguo = result.get("result").replace("-", "")
					.replace(":", "").replace("胜其它", "90").replace("负其它", "09")
					.replace("平其它", "99").replace("上单", "1").replace("上双", "2")
					.replace("下单", "3").replace("下双", "4").replace("7+", "7");
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
		String drawyears = HttpTookit.doGet(RESULT_WEBSITEURL + "/" + type + "/"
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
		String drawmonths = HttpTookit.doGet(RESULT_WEBSITEURL + "/" + type + "/"
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

		String drawissue = HttpTookit.doGet(RESULT_WEBSITEURL + "/" + type + "/"
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
	
	
	
	
	
	
//==============================================================
	
	
	private void getShengPFResult(){
		try {
			List<TBeiDanResult> list = new ArrayList<TBeiDanResult>();
			Document doc = Jsoup.connect(MATCH_RESULT_URL+TYPE_SFC+".shtml").timeout(5000).get();
			String batchcode = doc.select("#drawNo").text();
			Elements tbody = doc.select("#ssm200Table_stop").get(0).select("tbody");
			if (tbody.size()<=0||tbody.size() % 2 != 0) {
				return;
			}
			
			for(int i=0;i<tbody.size();i=i+2) {
				Element openbody = tbody.get(i + 1);
				Elements openmatches = openbody.select("tr");
				for (Element openmatch : openmatches) {
					Elements tds = openmatch.select("td");
					String no = tds.get(0).text().trim();
					String letPoint = tds.get(5).text().trim();
					String v3 = tds.get(7).text().trim();
					String v1 = tds.get(8).text().trim();
					String v0 = tds.get(9).text().trim();
					TBeiDanResult result = new TBeiDanResult();
					result.setLotno("B00001");
					result.setBatchcode("201"+batchcode);
					result.setNo(no);
					result.setHandicap(new BigDecimal(letPoint));
					result.setCreatetime(new Date());
					result.setAudit(BigDecimal.ONE);
					result.setIscancel(BigDecimal.ZERO);
					if(Double.parseDouble(v3)>1) {
						result.setResult("3");
						result.setPeilu(v3);
					}else if(Double.parseDouble(v1)>1) {
						result.setResult("1");
						result.setPeilu(v1);
					}else if(Double.parseDouble(v0)>1) {
						result.setResult("0");
						result.setPeilu(v0);
					}else if(isCancel(v1,v3,v0)) {
						result.setResult("*");
						result.setPeilu("1");
						result.setIscancel(BigDecimal.ONE);
					}else {
						continue;
					}
					list.add(result);
				}
			}
			findAndPersist(list);
		}catch (Exception e) {
			logger.info("抓取出错",e);
		}
		
		
	}
	
	
	private void getZongJQResult(){
		try {
			List<TBeiDanResult> list = new ArrayList<TBeiDanResult>();
			Document doc = Jsoup.connect(MATCH_RESULT_URL+TYPE_ZJQ+".shtml").timeout(5000).get();
			String batchcode = doc.select("#drawNo").text();
			Elements tbody = doc.select("#ssm200Table_stop").get(0).select("tbody");
			if (tbody.size()<=0||tbody.size() % 2 != 0) {
				return;
			}
			
			for(int i=0;i<tbody.size();i=i+2) {
				Element openbody = tbody.get(i + 1);
				Elements openmatches = openbody.select("tr");
				for (Element openmatch : openmatches) {
					Elements tds = openmatch.select("td");
					String no = tds.get(0).text().trim();
					String v0 = tds.get(7).text().trim();
					String v1 = tds.get(8).text().trim();
					String v2 = tds.get(9).text().trim();
					String v3 = tds.get(10).text().trim();
					String v4 = tds.get(11).text().trim();
					String v5 = tds.get(12).text().trim();
					String v6 = tds.get(13).text().trim();
					String v7 = tds.get(14).text().trim();
					TBeiDanResult result = new TBeiDanResult();
					result.setLotno("B00002");
					result.setBatchcode("201"+batchcode);
					result.setNo(no);
					result.setCreatetime(new Date());
					result.setAudit(BigDecimal.ONE);
					result.setIscancel(BigDecimal.ZERO);
					if(Double.parseDouble(v0)>1d) {
						result.setResult("0");
						result.setPeilu(v0);
					}else if(Double.parseDouble(v1)>1d) {
						result.setResult("1");
						result.setPeilu(v1);
					}else if(Double.parseDouble(v2)>1d) {
						result.setResult("2");
						result.setPeilu(v2);
					}else if(Double.parseDouble(v3)>1d) {
						result.setResult("3");
						result.setPeilu(v3);
					}else if(Double.parseDouble(v4)>1d) {
						result.setResult("4");
						result.setPeilu(v4);
					}else if(Double.parseDouble(v5)>1d) {
						result.setResult("5");
						result.setPeilu(v5);
					}else if(Double.parseDouble(v6)>1d) {
						result.setResult("6");
						result.setPeilu(v6);
					}else if(Double.parseDouble(v7)>1d) {
						result.setResult("7");
						result.setPeilu(v7);
					}else if(isCancel(v0,v1,v2,v3,v4,v5,v6,v7)) {
						result.setResult("*");
						result.setPeilu("1");
						result.setIscancel(BigDecimal.ONE);
					}else {
						continue;
					}
					list.add(result);
					
				}
			}
			
			findAndPersist(list);
		}catch(Exception e) {
			logger.info("抓取出错",e);
		}
		
	}
	
	
	private void getBanQCResult(){
		try {
			List<TBeiDanResult> list = new ArrayList<TBeiDanResult>();
			Document doc = Jsoup.connect(MATCH_RESULT_URL+TYPE_BQC+".shtml").timeout(5000).get();
			String batchcode = doc.select("#drawNo").text();
			Elements tbody = doc.select("#ssm200Table_stop").get(0).select("tbody");
			if (tbody.size()<=0||tbody.size() % 2 != 0) {
				return;
			}
			
			for(int i=0;i<tbody.size();i=i+2) {
				Element openbody = tbody.get(i + 1);
				Elements openmatches = openbody.select("tr");
				for (Element openmatch : openmatches) {
					Elements tds = openmatch.select("td");
					String no = tds.get(0).text().trim();
					String v33 = tds.get(7).text().trim();
					String v31 = tds.get(8).text().trim();
					String v30 = tds.get(9).text().trim();
					String v13 = tds.get(10).text().trim();
					String v11 = tds.get(11).text().trim();
					String v10 = tds.get(12).text().trim();
					String v03 = tds.get(13).text().trim();
					String v01 = tds.get(14).text().trim();
					String v00 = tds.get(15).text().trim();
					
					TBeiDanResult result = new TBeiDanResult();
					result.setLotno("B00003");
					result.setBatchcode("201"+batchcode);
					result.setNo(no);
					result.setCreatetime(new Date());
					result.setAudit(BigDecimal.ONE);
					result.setIscancel(BigDecimal.ZERO);
					if(Double.parseDouble(v33)>1d) {
						result.setResult("33");
						result.setPeilu(v33);
					}else if(Double.parseDouble(v31)>1d) {
						result.setResult("31");
						result.setPeilu(v31);
					}else if(Double.parseDouble(v30)>1d) {
						result.setResult("30");
						result.setPeilu(v30);
					}else if(Double.parseDouble(v13)>1d) {
						result.setResult("13");
						result.setPeilu(v13);
					}else if(Double.parseDouble(v11)>1d) {
						result.setResult("11");
						result.setPeilu(v11);
					}else if(Double.parseDouble(v10)>1d) {
						result.setResult("10");
						result.setPeilu(v10);
					}else if(Double.parseDouble(v03)>1d) {
						result.setResult("03");
						result.setPeilu(v03);
					}else if(Double.parseDouble(v01)>1d) {
						result.setResult("01");
						result.setPeilu(v01);
					}else if(Double.parseDouble(v00)>1d) {
						result.setResult("00");
						result.setPeilu(v00);
					}else if(isCancel(v33,v31,v30,v13,v11,v10,v03,v01,v00)) {
						result.setResult("*");
						result.setPeilu("1");
						result.setIscancel(BigDecimal.ONE);
					}else {
						continue;
					}
					list.add(result);
				}
			}
			
			findAndPersist(list);
		}catch(Exception e) {
			logger.info("抓取出错",e);
		}
	}
	
	
	private void getDanShuangResult(){
		try {
			List<TBeiDanResult> list = new ArrayList<TBeiDanResult>();
			Document doc = Jsoup.connect(MATCH_RESULT_URL+TYPE_DS+".shtml").timeout(5000).get();
			String batchcode = doc.select("#drawNo").text();
			Elements tbody = doc.select("#ssm200Table_stop").get(0).select("tbody");
			if (tbody.size()<=0||tbody.size() % 2 != 0) {
				return;
			}
			
			for(int i=0;i<tbody.size();i=i+2) {
				Element openbody = tbody.get(i + 1);
				Elements openmatches = openbody.select("tr");
				for (Element openmatch : openmatches) {
					Elements tds = openmatch.select("td");
					String no = tds.get(0).text().trim();
					String v1 = tds.get(7).text().trim();
					String v2 = tds.get(8).text().trim();
					String v3 = tds.get(9).text().trim();
					String v4 = tds.get(10).text().trim();
					TBeiDanResult result = new TBeiDanResult();
					result.setLotno("B00004");
					result.setBatchcode("201"+batchcode);
					result.setNo(no);
					result.setCreatetime(new Date());
					result.setAudit(BigDecimal.ONE);
					result.setIscancel(BigDecimal.ZERO);
					if(Double.parseDouble(v1)>1d) {
						result.setResult("1");
						result.setPeilu(v1);
					}else if(Double.parseDouble(v2)>1d) {
						result.setResult("2");
						result.setPeilu(v2);
					}else if(Double.parseDouble(v3)>1d) {
						result.setResult("3");
						result.setPeilu(v3);
					}else if(Double.parseDouble(v4)>1d) {
						result.setResult("4");
						result.setPeilu(v4);
					}else if(isCancel(v1,v2,v3,v4)) {
						result.setResult("*");
						result.setPeilu("1");
						result.setIscancel(BigDecimal.ONE);
					}else {
						continue;
					}
					list.add(result);
				}
			}
			
			findAndPersist(list);
		}catch(Exception e) {
			logger.info("抓取出错",e);
		}
	}
	
	
	private void getBiFenResult(){
		try {
			List<TBeiDanResult> list = new ArrayList<TBeiDanResult>();
			Document doc = Jsoup.connect(MATCH_RESULT_URL+TYPE_BF+".shtml").timeout(5000).get();
			String batchcode = doc.select("#drawNo").text();
			Elements tbody = doc.select("#ssm200Table_stop").get(0).select("tbody");
			if (tbody.size()<=0||tbody.size() % 2 != 0) {
				return;
			}
			
			for(int i=0;i<tbody.size();i=i+2) {
				Element openbody = tbody.get(i + 1);
				Elements openmatches = openbody.select("tr");
				
				for(int j=0;j<openmatches.size();j=j+4) {
					Elements tds0 = openmatches.get(j).select("td");
					Elements tds1 = openmatches.get(j+1).select("td");
					Elements tds2 = openmatches.get(j+2).select("td");
					Elements tds3 = openmatches.get(j+3).select("td");
					
					String no = tds0.get(0).text().trim();
					String v90 = tds1.get(0).text().trim();
					String v10 = tds1.get(1).text().trim();
					String v20 = tds1.get(2).text().trim();
					String v21 = tds1.get(3).text().trim();
					String v30 = tds1.get(4).text().trim();
					String v31 = tds1.get(5).text().trim();
					String v32 = tds1.get(6).text().trim();
					String v40 = tds1.get(7).text().trim();
					String v41 = tds1.get(8).text().trim();
					String v42 = tds1.get(9).text().trim();
					
					String v99 = tds2.get(0).text().trim();
					String v00 = tds2.get(1).text().trim();
					String v11 = tds2.get(2).text().trim();
					String v22 = tds2.get(3).text().trim();
					String v33 = tds2.get(4).text().trim();
					
					String v09 = tds3.get(0).text().trim();
					String v01 = tds3.get(1).text().trim();
					String v02 = tds3.get(2).text().trim();
					String v12 = tds3.get(3).text().trim();
					String v03 = tds3.get(4).text().trim();
					String v13 = tds3.get(5).text().trim();
					String v23 = tds3.get(6).text().trim();
					String v04 = tds3.get(7).text().trim();
					String v14 = tds3.get(8).text().trim();
					String v24 = tds3.get(9).text().trim();
					
					TBeiDanResult result = new TBeiDanResult();
					result.setLotno("B00005");
					result.setBatchcode("201"+batchcode);
					result.setNo(no);
					result.setCreatetime(new Date());
					result.setAudit(BigDecimal.ONE);
					result.setIscancel(BigDecimal.ZERO);
					if(Double.parseDouble(v90)>1d) {
						result.setResult("90");
						result.setPeilu(v90);
					}else if(Double.parseDouble(v10)>1d) {
						result.setResult("10");
						result.setPeilu(v10);
					}else if(Double.parseDouble(v20)>1d) {
						result.setResult("20");
						result.setPeilu(v20);
					}else if(Double.parseDouble(v21)>1d) {
						result.setResult("21");
						result.setPeilu(v21);
					}else if(Double.parseDouble(v30)>1d) {
						result.setResult("30");
						result.setPeilu(v30);
					}else if(Double.parseDouble(v31)>1d) {
						result.setResult("31");
						result.setPeilu(v31);
					}else if(Double.parseDouble(v32)>1d) {
						result.setResult("32");
						result.setPeilu(v32);
					}else if(Double.parseDouble(v40)>1d) {
						result.setResult("40");
						result.setPeilu(v40);
					}else if(Double.parseDouble(v41)>1d) {
						result.setResult("41");
						result.setPeilu(v41);
					}else if(Double.parseDouble(v42)>1d) {
						result.setResult("42");
						result.setPeilu(v42);
					}else if(Double.parseDouble(v99)>1d) {
						result.setResult("99");
						result.setPeilu(v99);
					}else if(Double.parseDouble(v00)>1d) {
						result.setResult("00");
						result.setPeilu(v00);
					}else if(Double.parseDouble(v11)>1d) {
						result.setResult("11");
						result.setPeilu(v11);
					}else if(Double.parseDouble(v22)>1d) {
						result.setResult("22");
						result.setPeilu(v22);
					}else if(Double.parseDouble(v33)>1d) {
						result.setResult("33");
						result.setPeilu(v33);
					}else if(Double.parseDouble(v09)>1d) {
						result.setResult("09");
						result.setPeilu(v09);
					}else if(Double.parseDouble(v01)>1d) {
						result.setResult("01");
						result.setPeilu(v01);
					}else if(Double.parseDouble(v02)>1d) {
						result.setResult("02");
						result.setPeilu(v02);
					}else if(Double.parseDouble(v12)>1d) {
						result.setResult("12");
						result.setPeilu(v12);
					}else if(Double.parseDouble(v03)>1d) {
						result.setResult("03");
						result.setPeilu(v13);
					}else if(Double.parseDouble(v33)>1d) {
						result.setResult("33");
						result.setPeilu(v33);
					}else if(Double.parseDouble(v23)>1d) {
						result.setResult("23");
						result.setPeilu(v23);
					}else if(Double.parseDouble(v04)>1d) {
						result.setResult("04");
						result.setPeilu(v04);
					}else if(Double.parseDouble(v14)>1d) {
						result.setResult("14");
						result.setPeilu(v14);
					}else if(Double.parseDouble(v24)>1d) {
						result.setResult("24");
						result.setPeilu(v24);
					}else if(isCancel(v10, v20, v21, v30, v31, v32, v40, v41, v42, v01, v02, v12, v03, v13, v23, v04, v14, v24, v00, v11, v22, v33, v90, v99, v09)) {
						result.setResult("*");
						result.setPeilu("1");
						result.setIscancel(BigDecimal.ONE);
					}else {
						continue;
					}
					list.add(result);
				}
				
			}
			
			findAndPersist(list);
		}catch(Exception e) {
			logger.info("抓取出错",e);
		}
	}
	
	
	private boolean isCancel(String ...args) {
		try {
			int equals1 = 0;
			for (String arg:args) {
				if("1".equals(arg)) {
					equals1 = equals1 + 1;
				}else {
					return false;
				}
			}
			if(equals1==args.length) {
				return true;
			}else {
				return false;
			}
		}catch (Exception e) {
			logger.info("抓取赔率出错",e);
		}
		return false;
		
	}
	
	
	
}
