package com.ruyicai.prizecrawler.beidan;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class BeiDanPeiluService {

	private Logger logger = LoggerFactory.getLogger(BeiDanPeiluService.class);

	private static String SHENGPINGFU = "http://www.bjlot.com/ssm/ssm200.shtml";// B00001
	private static String ZONGJINQIU = "http://www.bjlot.com/ssm/ssm230.shtml";// B00002
	private static String BANQUANCHANG = "http://www.bjlot.com/ssm/ssm240.shtml";// B00003
	private static String DANSHUANG = "http://www.bjlot.com/ssm/ssm210.shtml";// B00004
	private static String BIFEN = "http://www.bjlot.com/ssm/ssm250.shtml";// B00005
	
	
	private static Map<String,PeiLv> peilvmap = new HashMap<String,PeiLv>();
	
	public static void main(String[] args) throws IOException {
//		PeiLv shengPFPeilv = new BeiDanPeiluService().getBiFenPeilv();
//		XStream xstream = new XStream(new DomDriver());
//		xstream.alias("Football_Vs", Football_Vs.class);
//		xstream.alias("PeiLv", PeiLv.class);
//		
//		xstream.autodetectAnnotations(true);
//		String xml = xstream.toXML(shengPFPeilv);
//		System.out.println(xml);
		
		PeiLv zongJQPeilv = new BeiDanPeiluService().getZongJQPeilv();
	}
	
	
	public String getPeilvXMl(String lotno,String batchcode) {
		PeiLv peilv = peilvmap.get(lotno+"_"+batchcode);
		if(peilv==null) {
			throw new RuntimeException("beidan peilu is null");
		}
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("Football_Vs", Football_Vs.class);
		xstream.alias("DanShuang", DanShuang.class);
		xstream.alias("Goal", Goal.class);
		xstream.alias("Half", Half.class);
		xstream.alias("Score", Score.class);
		xstream.alias("PeiLv", PeiLv.class);
		String xml = xstream.toXML(peilv);
		
		return xml;
	}
	
	public void peilvTimer() {
		try {
			logger.info("开始抓取胜平负赔率");
			PeiLv<Football_Vs> shengPFPeilv = getShengPFPeilv();
			peilvmap.put(shengPFPeilv.getLotno()+"_"+shengPFPeilv.getBatchcode(), shengPFPeilv);
		}catch(Exception e) {
			logger.info("抓取胜平负赔率出错",e);
		}
		
		try {
			logger.info("开始抓取总进球赔率");
			 PeiLv<Goal> zongJQPeilv = getZongJQPeilv();
			 peilvmap.put(zongJQPeilv.getLotno()+"_"+zongJQPeilv.getBatchcode(), zongJQPeilv);
		}catch(Exception e) {
			logger.info("抓取总进球赔率出错",e);
		}
		
		try {
			logger.info("开始抓取半全场赔率");
			PeiLv<Half> banQCPeilv = getBanQCPeilv();
			peilvmap.put(banQCPeilv.getLotno()+"_"+banQCPeilv.getBatchcode(), banQCPeilv);
		}catch(Exception e) {
			logger.info("抓取半全场赔率出错",e);
		}
		
		try {
			logger.info("开始抓取上下单双赔率");
			PeiLv<DanShuang> danShuangPeilv = getDanShuangPeilv();
			peilvmap.put(danShuangPeilv.getLotno()+"_"+danShuangPeilv.getBatchcode(), danShuangPeilv);
		}catch(Exception e) {
			logger.info("抓取上下单双赔率出错",e);
		}
		
		try {
			logger.info("开始抓取比分赔率");
			PeiLv<Score> biFenPeilv = getBiFenPeilv();
			peilvmap.put(biFenPeilv.getLotno()+"_"+biFenPeilv.getBatchcode(), biFenPeilv);
		}catch(Exception e) {
			logger.info("抓取比分赔率出错",e);
		}
	}


	/**
	 * 获取胜负平赔率
	 * @return
	 * @throws IOException
	 */
	public PeiLv<Football_Vs> getShengPFPeilv() throws IOException {
		PeiLv<Football_Vs> peilv = new PeiLv<Football_Vs>();
		Document doc = Jsoup.connect(SHENGPINGFU).timeout(5000).get();
		String batchcode = doc.select("#drawNo").text();
		Elements tbody = doc.select("#ssm200Table").get(0).select("tbody");
		if (tbody.size() % 2 != 0) {
			throw new RuntimeException("抓取页面出错");
		}
		
		Map<String,Football_Vs> map = new HashMap<String,Football_Vs>();
		for(int i=0;i<tbody.size();i=i+2) {
			Element openbody = tbody.get(i + 1);
			Elements openmatches = openbody.select("tr");
			for (Element openmatch : openmatches) {
				Elements tds = openmatch.select("td");
				String no = tds.get(0).text().trim();
				String letPoint = tds.get(5).text().trim();
				if(Integer.parseInt(letPoint)>0) {
					letPoint = "+"+letPoint;
				}
				String v3 = tds.get(7).text().trim();
				String v1 = tds.get(8).text().trim();
				String v0 = tds.get(9).text().trim();
				map.put(no, new Football_Vs(v0, v1, v3, letPoint));
			}
		}
		
		peilv.setLotno("B00001");
		peilv.setBatchcode("201"+batchcode);
		peilv.setMap(map);
		return peilv;
	}
	
	
	/**
	 * 获取总进球赔率
	 * @return
	 * @throws IOException
	 */
	public PeiLv<Goal> getZongJQPeilv() throws IOException {
		PeiLv<Goal> peilv = new PeiLv<Goal>();
		Document doc = Jsoup.connect(ZONGJINQIU).timeout(5000).get();
		String batchcode = doc.select("#drawNo").text();
		Elements tbody = doc.select("#ssm200Table").get(0).select("tbody");
		if (tbody.size() % 2 != 0) {
			throw new RuntimeException("抓取页面出错");
		}
		
		Map<String,Goal> map = new HashMap<String,Goal>();
		for(int i=0;i<tbody.size();i=i+2) {
			Element openbody = tbody.get(i + 1);
			Elements openmatches = openbody.select("tr");
			for (Element openmatch : openmatches) {
				Elements tds = openmatch.select("td");
				String no = tds.get(0).text().trim();
				String v0 = tds.get(6).text().trim(); 
				String v1 = tds.get(7).text().trim();
				String v2 = tds.get(8).text().trim();
				String v3 = tds.get(9).text().trim();
				String v4 = tds.get(10).text().trim();
				String v5 = tds.get(11).text().trim();
				String v6 = tds.get(12).text().trim();
				String v7 = tds.get(13).text().trim();
				map.put(no, new Goal(v0, v1, v2, v3, v4, v5, v6, v7));
			}
		}
		
		peilv.setLotno("B00002");
		peilv.setBatchcode("201"+batchcode);
		peilv.setMap(map);
		return peilv;
	}
	
	
	/**
	 * 获取半全场赔率
	 * @return
	 * @throws IOException
	 */
	public PeiLv<Half> getBanQCPeilv() throws IOException {
		PeiLv<Half> peilv = new PeiLv<Half>();
		Document doc = Jsoup.connect(BANQUANCHANG).timeout(5000).get();
		String batchcode = doc.select("#drawNo").text();
		Elements tbody = doc.select("#ssm200Table").get(0).select("tbody");
		if (tbody.size() % 2 != 0) {
			throw new RuntimeException("抓取页面出错");
		}
		
		Map<String,Half> map = new HashMap<String,Half>();
		for(int i=0;i<tbody.size();i=i+2) {
			Element openbody = tbody.get(i + 1);
			Elements openmatches = openbody.select("tr");
			for (Element openmatch : openmatches) {
				Elements tds = openmatch.select("td");
				String no = tds.get(0).text().trim();
				String v33 = tds.get(6).text().trim();
				String v31 = tds.get(7).text().trim();
				String v30 = tds.get(8).text().trim();
				String v13 = tds.get(9).text().trim();
				String v11 = tds.get(10).text().trim();
				String v10 = tds.get(11).text().trim();
				String v03 = tds.get(12).text().trim();
				String v01 = tds.get(13).text().trim();
				String v00 = tds.get(14).text().trim();
				
				map.put(no, new Half(v00, v01, v03, v10, v11, v13, v30, v31, v33));
			}
		}
		
		peilv.setLotno("B00003");
		peilv.setBatchcode("201"+batchcode);
		peilv.setMap(map);
		return peilv;
	}
	
	
	/**
	 * 获取单双赔率
	 * @return
	 * @throws IOException
	 */
	public PeiLv<DanShuang> getDanShuangPeilv() throws IOException {
		PeiLv<DanShuang> peilv = new PeiLv<DanShuang>();
		Document doc = Jsoup.connect(DANSHUANG).timeout(5000).get();
		String batchcode = doc.select("#drawNo").text();
		Elements tbody = doc.select("#ssm200Table").get(0).select("tbody");
		if (tbody.size() % 2 != 0) {
			throw new RuntimeException("抓取页面出错");
		}
		
		Map<String,DanShuang> map = new HashMap<String,DanShuang>();
		for(int i=0;i<tbody.size();i=i+2) {
			Element openbody = tbody.get(i + 1);
			Elements openmatches = openbody.select("tr");
			for (Element openmatch : openmatches) {
				Elements tds = openmatch.select("td");
				String no = tds.get(0).text().trim();
				String v1 = tds.get(6).text().trim();
				String v2 = tds.get(7).text().trim();
				String v3 = tds.get(8).text().trim();
				String v4 = tds.get(9).text().trim();
				map.put(no, new DanShuang(v1, v2, v3, v4));
			}
		}
		
		peilv.setLotno("B00004");
		peilv.setBatchcode("201"+batchcode);
		peilv.setMap(map);
		return peilv;
	}
	
	
	/**
	 * 获取比分赔率
	 * @return
	 * @throws IOException
	 */
	public PeiLv<Score> getBiFenPeilv() throws IOException {
		PeiLv<Score> peilv = new PeiLv<Score>();
		Document doc = Jsoup.connect(BIFEN).timeout(5000).get();
		String batchcode = doc.select("#drawNo").text();
		Elements tbody = doc.select("#ssm200Table").get(0).select("tbody");
		if (tbody.size() % 2 != 0) {
			throw new RuntimeException("抓取页面出错");
		}
		
		Map<String,Score> map = new HashMap<String,Score>();
		for(int i=0;i<tbody.size();i=i+2) {
			Element openbody = tbody.get(i + 1);
			Elements openmatches = openbody.select("tr");
			
			for(int j=0;j<openmatches.size();j=j+4) {
				Elements tds0 = openmatches.get(j).select("td");
				Elements tds1 = openmatches.get(j+1).select("td");
				Elements tds2 = openmatches.get(j+2).select("td");
				Elements tds3 = openmatches.get(j+3).select("td");
				
				String no = tds0.get(0).text().trim();
				String v90 = tds1.get(0).select("span").get(0).text().trim();
				String v10 = tds1.get(1).select("span").get(0).text().trim();
				String v20 = tds1.get(2).select("span").get(0).text().trim();
				String v21 = tds1.get(3).select("span").get(0).text().trim();
				String v30 = tds1.get(4).select("span").get(0).text().trim();
				String v31 = tds1.get(5).select("span").get(0).text().trim();
				String v32 = tds1.get(6).select("span").get(0).text().trim();
				String v40 = tds1.get(7).select("span").get(0).text().trim();
				String v41 = tds1.get(8).select("span").get(0).text().trim();
				String v42 = tds1.get(9).select("span").get(0).text().trim();
				
				String v99 = tds2.get(0).select("span").get(0).text().trim();
				String v00 = tds2.get(1).select("span").get(0).text().trim();
				String v11 = tds2.get(2).select("span").get(0).text().trim();
				String v22 = tds2.get(3).select("span").get(0).text().trim();
				String v33 = tds2.get(4).select("span").get(0).text().trim();
				
				String v09 = tds3.get(0).select("span").get(0).text().trim();
				String v01 = tds3.get(1).select("span").get(0).text().trim();
				String v02 = tds3.get(2).select("span").get(0).text().trim();
				String v12 = tds3.get(3).select("span").get(0).text().trim();
				String v03 = tds3.get(4).select("span").get(0).text().trim();
				String v13 = tds3.get(5).select("span").get(0).text().trim();
				String v23 = tds3.get(6).select("span").get(0).text().trim();
				String v04 = tds3.get(7).select("span").get(0).text().trim();
				String v14 = tds3.get(8).select("span").get(0).text().trim();
				String v24 = tds3.get(9).select("span").get(0).text().trim();
				map.put(no, new Score(v10, v20, v21, v30, v31, v32, v40, v41, v42, v01, v02, v12, v03, v13, v23, v04, v14, v24, v00, v11, v22, v33, v90, v99, v09));
			}
			
		}
		
		peilv.setLotno("B00005");
		peilv.setBatchcode("201"+batchcode);
		peilv.setMap(map);
		return peilv;
	}

	

	static class PeiLv<T> {
		private String lotno;
		private String batchcode;
		private Map<String, T> map;
		public String getLotno() {
			return lotno;
		}
		public void setLotno(String lotno) {
			this.lotno = lotno;
		}
		public String getBatchcode() {
			return batchcode;
		}
		public void setBatchcode(String batchcode) {
			this.batchcode = batchcode;
		}
		public Map<String, T> getMap() {
			return map;
		}
		public void setMap(Map<String, T> map) {
			this.map = map;
		}
		

	}

	static class DanShuang {

		private String v1, v2, v3, v4;

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

		public DanShuang(String v1, String v2, String v3, String v4) {
			super();
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
			this.v4 = v4;
		}
	}

	static class Football_Vs {

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

	static class Goal {

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

	static class Half {

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
	
	static class Score {

		private String v10, v20, v21, v30, v31, v32, v40, v41, v42, v01, v02, v12,
				v03, v13, v23, v04, v14, v24, v00, v11, v22, v33, v90, v99, v09;

		public String getV10() {
			return v10;
		}

		public void setV10(String v10) {
			this.v10 = v10;
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

		public String getV12() {
			return v12;
		}

		public void setV12(String v12) {
			this.v12 = v12;
		}

		public String getV03() {
			return v03;
		}

		public void setV03(String v03) {
			this.v03 = v03;
		}

		public String getV13() {
			return v13;
		}

		public void setV13(String v13) {
			this.v13 = v13;
		}

		public String getV23() {
			return v23;
		}

		public void setV23(String v23) {
			this.v23 = v23;
		}

		public String getV04() {
			return v04;
		}

		public void setV04(String v04) {
			this.v04 = v04;
		}

		public String getV14() {
			return v14;
		}

		public void setV14(String v14) {
			this.v14 = v14;
		}

		public String getV24() {
			return v24;
		}

		public void setV24(String v24) {
			this.v24 = v24;
		}

		public String getV00() {
			return v00;
		}

		public void setV00(String v00) {
			this.v00 = v00;
		}

		public String getV11() {
			return v11;
		}

		public void setV11(String v11) {
			this.v11 = v11;
		}

		public String getV22() {
			return v22;
		}

		public void setV22(String v22) {
			this.v22 = v22;
		}

		public String getV33() {
			return v33;
		}

		public void setV33(String v33) {
			this.v33 = v33;
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

		public String getV09() {
			return v09;
		}

		public void setV09(String v09) {
			this.v09 = v09;
		}

		public Score(String v10, String v20, String v21, String v30, String v31,
				String v32, String v40, String v41, String v42, String v01,
				String v02, String v12, String v03, String v13, String v23,
				String v04, String v14, String v24, String v00, String v11,
				String v22, String v33, String v90, String v99, String v09) {
			super();
			this.v10 = v10;
			this.v20 = v20;
			this.v21 = v21;
			this.v30 = v30;
			this.v31 = v31;
			this.v32 = v32;
			this.v40 = v40;
			this.v41 = v41;
			this.v42 = v42;
			this.v01 = v01;
			this.v02 = v02;
			this.v12 = v12;
			this.v03 = v03;
			this.v13 = v13;
			this.v23 = v23;
			this.v04 = v04;
			this.v14 = v14;
			this.v24 = v24;
			this.v00 = v00;
			this.v11 = v11;
			this.v22 = v22;
			this.v33 = v33;
			this.v90 = v90;
			this.v99 = v99;
			this.v09 = v09;
		}
	}
}
