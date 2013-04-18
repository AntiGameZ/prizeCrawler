package com.ruyicai.prizecrawler.lottype.gdhappy10;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.ruyicai.prizecrawler.consts.SystemCode;

public class Gdhappy10Cailele {

	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect("http://www.cailele.com/lottery/klsf/").userAgent(SystemCode.UA_IE9)
				.timeout(4000).get();
		
		Element ele = doc.select("#openPanel").first();
		
		Element div = ele.select("div").get(1);
		
		System.out.println(div);
	}
}
