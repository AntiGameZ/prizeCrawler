package com.ruyicai.prizecrawler.util;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class SendMailUtil {

	public static void sendMail(String subject,String msg) throws EmailException {
		SimpleEmail email = new SimpleEmail();
		email.setHostName("smtp.126.com");
		email.setAuthentication("ruyicaiemail","jrtruyicai");  
		email.addTo("liuhongxing@ruyicai.com", "liuhongxing");
		email.setFrom("ruyicaiemail@126.com", "Me");
		email.setSubject(subject);
		email.setMsg(msg);
		email.send();
	}

}
