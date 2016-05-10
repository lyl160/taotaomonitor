package com.whty.taotao.monitor.util.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 * 发件箱工厂
 * 
 * @author MZULE
 * 
 */
public class MailSenderFactory {

	/**
	 * 服务邮箱
	 */
	private static SimpleMailSender serviceSms = null;

	/**
	 * 获取邮箱
	 * 
	 * @param type
	 *            邮箱类型
	 * @return 符合类型的邮箱
	 */
	public static SimpleMailSender getSender() {
			if (serviceSms == null) {
				serviceSms = new SimpleMailSender("liyulong160@139.com", "lyl123");
			}
			return serviceSms;
	}
	
	public static void main(String[] args) {
		SimpleMailSender smsSender = MailSenderFactory.getSender();
	    List<String> recipients = new ArrayList<String>();
	    recipients.add("295848719@qq.com");
	    try {
			smsSender.send(recipients, "123", "1345");
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}