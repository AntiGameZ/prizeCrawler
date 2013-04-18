package com.ruyicai.prizecrawler.lottype.shuangseqiu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class SSQ500wan implements IAgency{

	private static Logger logger = LoggerFactory.getLogger(SSQ500wan.class);
	private static final String URL = "http://kaijiang.500.com/static/info/kaijiang/xml/ssq/list.xml";
	
	
	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从500万抓取开奖,彩种F47104,期号"+batchcode);
			Document doc = Jsoup.connect(URL).timeout(3500).get();
			Elements eles = doc.select("body > xml > row");
			for(Element e:eles) {
				if(batchcode.equals("20"+e.attr("expect"))) {
					prizeInfo.setBatchcode(batchcode);
					prizeInfo.setLotno("F47104");
					prizeInfo.setWinbasecode(e.attr("opencode").split("\\|")[0].replace(",", ""));
					prizeInfo.setWinspecialcode(e.attr("opencode").split("\\|")[1]);
				}
			}
			logger.info("从500万抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从五百万抓取第" + batchcode + "期双色球开奖出错", e);
		}
		
		return prizeInfo;
	}
	
	
	public static void main(String[] args) {
		System.out.println(new SSQ500wan().crawlFromAgency("F47104", "2012151"));
	}
}
