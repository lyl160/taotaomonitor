package com.whty.taotao.monitor.util;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertyUtils {

	private static Logger logger = Logger.getLogger(PropertyUtils.class);
	private static Properties properties = new Properties();
	private static String[] locations;
	
	public static String getProperty(String key) {
		return getProperty(key, null);
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	public static int getInt(String key) {
		return Integer.parseInt(getProperty(key));
	}

	@PostConstruct
	public static void loadProperties() {

		Properties properties = new Properties();
		
		if (locations != null && locations.length > 0) {
			PathMatchingResourcePatternResolver resolover = new PathMatchingResourcePatternResolver();
			
			for (String location : locations) {
				try {
					Resource[] resources = resolover.getResources(location);
					for (Resource resource : resources) {
						try {
							PropertiesLoaderUtils.fillProperties(properties, resource);
							logger.info("load properties - " + resource.getFilename());
						} catch (Exception e) {
							logger.error("properties load error - " + resource.getURI());
						}
					}
				} catch (Exception e) {
					logger.error("location read error - " + location);
				}
			}
		}

		PropertyUtils.properties = properties;
	}

	public static void setLocation(String location) {
		PropertyUtils.locations = new String[] { location };
	}
	
	public static void setLocations(String[] locations) {
		PropertyUtils.locations = locations;
	}

}
