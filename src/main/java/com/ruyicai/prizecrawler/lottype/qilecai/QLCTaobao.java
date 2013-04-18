package com.ruyicai.prizecrawler.lottype.qilecai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public class QLCTaobao implements IAgency{

	private static Logger logger = LoggerFactory.getLogger(QLCTaobao.class);
	private static final String URL = "http://caipiao.taobao.com/lottery/awardresult/lottery_qlc.htm";
	
	@Override
	public PrizeInfo crawlFromAgency(String lotno, String batchcode) {
		PrizeInfo prizeInfo = new PrizeInfo();
		try {
			logger.info("开始从淘宝抓取开奖,彩种F47102,期号"+batchcode);
			Document doc = Jsoup.connect(URL).timeout(3500).get();

			if(isIssueExist(batchcode, doc)) {
				buildPrizeInfo(batchcode, prizeInfo, doc);
			}else {
				logger.info("从淘宝抓取开奖,彩种F47102,期号"+batchcode+"不存在");
			}
			
			logger.info("从淘宝抓取开奖结束:"+prizeInfo.toString());
		}catch(Exception e) {
			prizeInfo = new PrizeInfo();
			logger.info("从淘宝抓取第" + batchcode + "期七乐彩开奖出错", e);
		}
		
		return prizeInfo;
	}


	private void buildPrizeInfo(String batchcode, PrizeInfo prizeInfo,
			Document doc) {
		Elements lis = doc.select(".kk-a");
		Elements spans = lis.get(0).select("span");
		String[] winnum = new String[8];
		for(int i = 1;i < 9;i++) {
			winnum[i-1] = spans.get(i).text();
		}
		String wincodered = winnum[0]+winnum[1]+winnum[2]+winnum[3]+winnum[4]+winnum[5]+winnum[6];
		prizeInfo.setBatchcode(batchcode);
		prizeInfo.setLotno("F47102");
		prizeInfo.setWinbasecode(wincodered);
		prizeInfo.setWinspecialcode(winnum[7]);
	}


	private boolean isIssueExist(String batchcode, Document doc) {
		boolean flag = false;
		Element select = doc.select(".J_select_op").first();
		Elements options = select.select("option");
		for(Element option:options) {
			if(batchcode.equals(option.text())&&"selected".equals(option.attr("selected"))) {
				flag = true;
			}
		}
		return flag;
	}
	
	public static void main(String[] args) {
		System.out.println(new QLCTaobao().crawlFromAgency("F47102", "2012150"));
	}

}
