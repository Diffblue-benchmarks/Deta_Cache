package org.lyg.vpc.process.portImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lyg.cache.DetaCacheManager;
//import org.lyg.vpc.controller.port.RestCachePort;
//import org.springframework.web.bind.annotation.RestController;
//@RestController
public class RestCachePortImpl {

	public static String get(String key, String email, String password) throws Exception {
		if(email.equalsIgnoreCase("313699483@qq.com") && password.equalsIgnoreCase("Fengyue1985!")){
			return DetaCacheManager.getCache(key);
		}
		return "unsuccess";
	}

	public static String put(String key, String value, String time, String email, String password)
			throws Exception {
		if(email.equalsIgnoreCase("313699483@qq.com") && password.equalsIgnoreCase("Fengyue1985!")){
			DetaCacheManager.putCache(key, value, Long.valueOf(time));
			return "success";
		}
		return "unsuccess";
	}

	@SuppressWarnings("rawtypes")
	public static List<String> getAskers(String email, String password) throws Exception {
		if(email.equalsIgnoreCase("313699483@qq.com") && password.equalsIgnoreCase("Fengyue1985!")){
			List<String> output = new ArrayList<>();
			Iterator iterator  = DetaCacheManager.getCacheIterator();
			while(iterator.hasNext()){
				Map.Entry entry = (Map.Entry) iterator.next();
				String key=String.valueOf(entry.getKey());
				String[] entryStrings = key.split("\\.");
				if(entryStrings.length == 4
						&& key.contains("Ask:")){
					output.add( entry.getKey().toString());	
				}
			}
			return output;
		}
		return new ArrayList<>();
	}
}