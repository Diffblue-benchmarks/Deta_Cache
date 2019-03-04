package org.lyg.vpc.process.portImpl;
import java.util.HashMap;
import java.util.Map;
import org.lyg.common.utils.OperatingSystemInfoUtil;
/*
 *     git�����ļ����ߣ�³����
 *     ������ļ����ߣ������⣬ȫ��������ȫ�����ܵ�ͨ���޸ģ�
 *     20190301
 * */
public class DevopsStatus {
	public static Object getServerInf(String token, String  usremail, String usrpswd) {
		Map<String, Object> map = new HashMap<>();
		map.put("showJvmInfo",  OperatingSystemInfoUtil.showJvmInfo());
		map.put("showMemoryInfo", OperatingSystemInfoUtil.showMemoryInfo()); 
		map.put("showSystem",  OperatingSystemInfoUtil.showSystem()); 
		map.put("showClassLoading", OperatingSystemInfoUtil.showClassLoading()); 
		map.put("showCompilation", OperatingSystemInfoUtil.showCompilation()); 
		map.put("showThread", OperatingSystemInfoUtil.showThread()); 
		map.put("showGarbageCollector", OperatingSystemInfoUtil.showGarbageCollector()); 
		map.put("showMemoryManager", OperatingSystemInfoUtil.showMemoryManager()); 
		map.put("showMemoryPool",  OperatingSystemInfoUtil.showMemoryPool()); 	
		map.put("visitMBean", OperatingSystemInfoUtil.visitMBean()); 
		return map;
	}
}