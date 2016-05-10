package com.whty.taotao.monitor.action;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.JSONUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import com.whty.taotao.monitor.util.HttpTookit;
import com.whty.taotao.monitor.util.HttpUtil;
import com.whty.taotao.monitor.util.PropertyUtils;
import com.whty.taotao.monitor.util.mail.MailSenderFactory;
import com.whty.taotao.monitor.util.mail.SimpleMailSender;
@Component  
public class InterfaceRequester {

	private static Logger logger = Logger.getLogger(InterfaceRequester.class);
	List<String> urlList ;
	List<String> recipients ;
	Map<String, Integer> errorMap = new HashMap<String, Integer>();
	int warnSize = 3;
	
	public void setUrlFromProperty() {
		logger.info("load url …");
		try {
			PropertyUtils.loadProperties();
			String urlString = PropertyUtils.getProperty("url");
			if (StringUtils.isNotBlank(urlString)) {
				urlList = Arrays.asList(urlString.split("@@@"));
			}
			if (urlList==null) {
				urlList = new ArrayList<String>();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Scheduled(cron = "0/5 * * * * *")
    void request(){  
        logger.info("start request!");
        setUrlFromProperty();
        String responsString = null;
        for (String url : urlList) {
			logger.info("trying request url:"+url);
			try {
				responsString = HttpTookit.doGet(url, "", "UTF-8", false);
				if (StringUtils.isBlank(responsString)) {
					throw new Exception("respons body is null");
				}
				logger.info("request right answer!");
			} catch (Exception e) {
				logger.error(e);
				logger.error("request wrong answer!");
				haldException(url,e);
			}finally{
				logger.info("finish request url:"+url);
			}
//			String result = HttpUtil.get(url, "application/json;charset=UTF-8", null, null, "utf-8", "utf-8");
		}
    }


	private void haldException(String url, Exception e) {
		if (errorMap.get(url)==null) {
			errorMap.put(url, 1);
		}else {
			errorMap.put(url, errorMap.get(url)+1);
		}
	}
    
	@Scheduled(cron = "0/5 * * * * *")
    public void checkWarnSize(){ 
		for (Map.Entry<String, Integer> entry : errorMap.entrySet()) {
			logger.error("error times = "+ entry.getValue()+", url = " + entry.getKey() );
			if (entry.getValue()>=warnSize) {
				sendWarnEmail(entry.getKey(),entry.getValue());
				errorMap.remove(entry.getKey());
			}
		}  
	}
	
	@Scheduled(cron = "0 0 0/1 * * *")
    public void clearErrorMap(){ 
		logger.info("errorMap  is  clear now !!!");
		errorMap.clear();
	}


	private void sendWarnEmail(String url, Integer times) {
		logger.error("warn!!! The url error times over "+errorMap.get(url));
		logger.error("The url:"+url);
		SimpleMailSender smsSender = MailSenderFactory.getSender();
	    List<String> recipients = new ArrayList<String>();
	    String mailString = PropertyUtils.getProperty("mail");
	    if (StringUtils.isNotBlank(mailString)) {
	    	recipients = Arrays.asList(mailString.split("@@@"));
		}else {
			recipients = new ArrayList<String>();
		}
	    try {
	    	if (!recipients.isEmpty()) {
	    		logger.error("send mails...");
	    		smsSender.send(recipients, "taotao interface error", "warn!!! The url:"+url+" error times:"+times);
			}
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		errorMap.remove(url);
	}
	
      
      
    
}
