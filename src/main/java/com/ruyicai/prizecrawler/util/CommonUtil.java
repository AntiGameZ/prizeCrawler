package com.ruyicai.prizecrawler.util;

import java.util.Iterator;
import java.util.Set;

public class CommonUtil {

	public static String setToString(Set<String> set) {

		StringBuilder sb = new StringBuilder();
		for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			sb.append(string).append("~~~");
		}
		return sb.toString();
	}
}
