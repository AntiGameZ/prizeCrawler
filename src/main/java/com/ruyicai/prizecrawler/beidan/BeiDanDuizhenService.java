package com.ruyicai.prizecrawler.beidan;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import com.ruyicai.prizecrawler.util.SendSMS;

@Service
public class BeiDanDuizhenService {
	
	private Logger logger = LoggerFactory.getLogger(BeiDanDuizhenService.class);

	private static String SHENGPINGFU = "http://www.bjlot.com/ssm/ssm200.shtml";//B00001
	private static String ZONGJINQIU = "http://www.bjlot.com/ssm/ssm230.shtml";//B00002
	private static String BANQUANCHANG = "http://www.bjlot.com/ssm/ssm240.shtml";//B00003
	private static String DANSHUANG = "http://www.bjlot.com/ssm/ssm210.shtml";//B00004
	private static String BIFEN = "http://www.bjlot.com/ssm/ssm250.shtml";//B00005
	
	private static String REGEX1 = "([1-9][1-9]){1}[-](0[1-9]|1[0-2]){1}[-](0[1-9]|1[0-9]|2[0-9]|3[0-1]){1}";
	
	@Autowired
	private TBeidanDao beidandao;
	
	@Value("${msg.station}")
	private String msgstation;
	
	@Autowired
	private SendSMS sendSMS;
	
	
	public void duizhenTimer() {
		logger.info("北单对阵抓取定时开始");
		crawlShengFP();
		crawlZongJQ();
		crawlBanQC();
		crawlDanShuang();
		crawlBiFen();
		logger.info("北单对阵抓取定时结束");	
	}
	
	
	private void findAndPersist(List<TBeiDanMatches> list) {
		int newmatch = 0;
		for(TBeiDanMatches match:list) {
			TBeiDanMatches oldMatch = beidandao.findBeidanMatch(match.getLotno(), match.getBatchcode(), match.getNo());
			if(oldMatch==null) {
				logger.info("保存北单赛事"+match.toString());
				beidandao.persist(match);
				newmatch = newmatch + 1;
			}else {
				logger.info("北单赛事{} {} {}已经存在,比较结束时间是否相同",new String[]{match.getLotno(),match.getBatchcode(),match.getNo()});
				if(match.getEndtime()!=null&&oldMatch.getEndtime()!=null&&(!match.getEndtime().equals(oldMatch.getEndtime()))) {
					logger.info("北单赛事{} {} {}结束时间不同,进行更改,原始时间={} 更改时间={}",new String[]{match.getLotno(),match.getBatchcode(),match.getNo(),oldMatch.getEndtime().toString(),match.getEndtime().toString()});
					beidandao.updateBeidanMatcheEndtime(match);
				}
			}
		}
		
		if(newmatch>0) {
			sendSMS.sendSMS(msgstation+sendSMS.beidanMatchMsg);
		}
	}
	
	/**
	 * 抓取胜负平对阵
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public void crawlShengFP(){
		try {
			logger.info("开始抓取北单胜负平赛事");
			findAndPersist(findMatches(SHENGPINGFU,"B00001",true));
			logger.info("抓取北单胜负平赛事结束");
		}catch(Exception e) {
			logger.info("抓取北单胜负平赛事出错",e);
		}
		
	}
	
	
	/**
	 * 抓取总进球对阵
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public void crawlZongJQ(){
		try {
			logger.info("开始抓取北单总进球赛事");
			findAndPersist(findMatches(ZONGJINQIU,"B00002",false));
			logger.info("抓取北单总进球赛事结束");
		}catch(Exception e) {
			logger.info("抓取北单总进球赛事出错",e);
		}
		
	}
	
	
	/**
	 * 抓取半全场对阵
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public void crawlBanQC(){
		try {
			logger.info("开始抓取北单半全场赛事");
			findAndPersist(findMatches(BANQUANCHANG,"B00003",false));
			logger.info("抓取北单半全场赛事结束");
		}catch(Exception e) {
			logger.info("抓取北单半全场赛事出错",e);
		}
		
	}
	
	/**
	 * 抓取单双对阵
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public void crawlDanShuang(){
		try {
			logger.info("开始抓取北单上下单双赛事");
			findAndPersist(findMatches(DANSHUANG,"B00004",false));
			logger.info("抓取北单上下单双赛事结束");
		}catch(Exception e) {
			logger.info("抓取北单上下单双赛事出错",e);
		}
		
	}
	
	
	/**
	 * 抓取比分对阵
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public void crawlBiFen(){
		logger.info("开始抓取北单比分对阵");
		try {
			List<TBeiDanMatches> matches = new ArrayList<TBeiDanMatches>();
			Document doc = Jsoup.connect(BIFEN).timeout(5000).get();
			String batchcode = doc.select("#drawNo").text();
			Elements tbody = doc.select("#ssm200Table").get(0).select("tbody");
			if(tbody.size()%2!=0) {
				throw new RuntimeException("抓取页面出错");
			}
			
			for (int i=0;i<tbody.size();i=i+2) {
				String timestart = tbody.get(i).select("tr>th").text();
				Pattern p = Pattern.compile(REGEX1);
				Matcher m = p.matcher(timestart);
				if(m.find()) {
					timestart = "20"+m.group();
				}
				
				
				Element openbody = tbody.get(i+1);
				Elements openmatches = openbody.select("tr");
				
				for(int j=0;j<openmatches.size();j=j+4) {
					Element openmatch = openmatches.get(j);
					Elements tds = openmatch.select("td");
					TBeiDanMatches match = new TBeiDanMatches();
					match.setLotno("B00005");
					match.setBatchcode("201"+batchcode);//奖期年
					match.setNo(tds.get(0).select("span").get(0).text().trim());
					match.setDay(timestart.replace("-", ""));
					String league_time = tds.get(2).text().trim().replace(" ", "");
					match.setLeaguename(league_time.substring(0, league_time.length()-5));
					match.setHost(tds.get(3).text().trim().replace(" ", "").substring(3));
					match.setHandicap(BigDecimal.ZERO);
					match.setGuest(tds.get(4).text().trim().replace(" ", "").substring(3));
					match.setCreatetime(new Date());
					String enddatestr = league_time.substring(league_time.length()-5);
					
					String realtime = timestart;
					if(enddatestr.startsWith("0")) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						Date current = format.parse(timestart);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(current);
						calendar.add(Calendar.DAY_OF_MONTH, 1);
						realtime = format.format(calendar.getTime());
					}
					Date endtime = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(realtime+" "+enddatestr);
					match.setEndtime(getBeforeDate(-10, endtime));
					
					match.setState(BigDecimal.ZERO);
					match.setSalestate(BigDecimal.ZERO);
					match.setAudit(BigDecimal.ONE);
					
					matches.add(match);
				}
			}
			
			findAndPersist(matches);
			logger.info("抓取北单比分对阵结束");
		}catch(Exception e) {
			logger.info("抓取北单比分对阵出错",e);
		}
		
	}



	private List<TBeiDanMatches> findMatches(String url,String lotno,boolean isHandicap) throws IOException,
			ParseException {
		List<TBeiDanMatches> matches = new ArrayList<TBeiDanMatches>();
		Document doc = Jsoup.connect(url).timeout(5000).get();
		String batchcode = doc.select("#drawNo").text();
		Elements tbody = doc.select("#ssm200Table").get(0).select("tbody");
		if(tbody.size()%2!=0) {
			throw new RuntimeException("抓取页面出错");
		}
		
		for (int i=0;i<tbody.size();i=i+2) {
			String timestart = tbody.get(i).select("tr>td").text();
			Pattern p1 = Pattern.compile(REGEX1);
			Matcher m1 = p1.matcher(timestart);
			if(m1.find()) {
				timestart = "20"+m1.group();
			}
			
			Element openbody = tbody.get(i+1);
			Elements openmatches = openbody.select("tr");
			
			for(Element openmatch:openmatches) {
				Elements tds = openmatch.select("td");
				TBeiDanMatches match = new TBeiDanMatches();
				match.setLotno(lotno);
				match.setBatchcode("201"+batchcode);//奖期年
				match.setNo(tds.get(0).select("span").get(0).text().trim());
				match.setLeaguename(tds.get(2).text().trim().replace(" ", ""));
				match.setHost(tds.get(4).text().trim().replace(" ", ""));
				match.setDay(timestart.replace("-", ""));
				if(isHandicap) {
					match.setHandicap(new BigDecimal(tds.get(5).text().trim()));
					match.setGuest(tds.get(6).text().trim().replace(" ", ""));
				}else {
					match.setHandicap(BigDecimal.ZERO);
					match.setGuest(tds.get(5).text().trim().replace(" ", ""));
				}
				
				match.setCreatetime(new Date());
				String enddatestr = tds.get(3).text().trim();
				
				String realtime = timestart;
				if(enddatestr.startsWith("0")) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date current = format.parse(timestart);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(current);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					realtime = format.format(calendar.getTime());
				}
				Date endtime = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(realtime+" "+enddatestr);
				match.setEndtime(getBeforeDate(-10, endtime));
				
				match.setState(BigDecimal.ZERO);
				match.setSalestate(BigDecimal.ZERO);
				match.setAudit(BigDecimal.ONE);
				
				matches.add(match);
			}
		}
		
		return matches;
	}
	
	
	private Date getBeforeDate(int minutebefore,Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(Calendar.MINUTE, minutebefore);
		return now.getTime();
	}
}
