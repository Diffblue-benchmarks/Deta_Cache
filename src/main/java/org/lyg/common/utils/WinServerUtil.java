package org.lyg.common.utils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
//import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class WinServerUtil {

	private static final int CPUTIME = 500;
	private static final int PERCENT = 100;
	private static final int FAULTLENGTH = 10;

	public static void main(String[] args) {
		System.out.println(getCpuRatioForWindows());
		//System.out.println(getMemery());
		System.out.println(getDisk());
	}

//	public static String getWinServerStatus(){
//
//	//	return "CpuInfo "+getCpuRatioForWindows()+", MemoryInfo "+getMemery()+", DiskInfo"+getDisk();
//	}
//
//	//��ȡ�ڴ�ʹ����
//	public static String getMemery(){
//		OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//		// �ܵ������ڴ�+�����ڴ�
//		long totalvirtualMemory = osmxb.getTotalSwapSpaceSize();
//		// ʣ��������ڴ�
//		long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
//		Double compare=(Double)(1-freePhysicalMemorySize*1.0/totalvirtualMemory)*100;
//		String str="�ڴ���ʹ��:"+compare.intValue()+"%";
//		return str;
//	}

	//��ȡ�ļ�ϵͳʹ����
	public static List<String> getDisk() {
		// ����ϵͳ
		List<String> list=new ArrayList<String>();
		for (char c = 'A'; c <= 'Z'; c++) {
			String dirName = c + ":/";
			File win = new File(dirName);
			if(win.exists()){
				long total=(long)win.getTotalSpace();
				long free=(long)win.getFreeSpace();
				Double compare=(Double)(1-free*1.0/total)*100;
				String str=c+":��  ��ʹ�� "+compare.intValue()+"%";
				list.add(str);
			}
		}
		return list;
	}

	//���cpuʹ����
	public static String getCpuRatioForWindows() {
		try {
			String procCmd = System.getenv("windir") + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
			// ȡ������Ϣ
			long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
			Thread.sleep(CPUTIME);
			long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
			if (c0 != null && c1 != null) {
				long idletime = c1[0] - c0[0];
				long busytime = c1[1] - c0[1];
				return "CPUʹ����:"+Double.valueOf(PERCENT * (busytime)*1.0 / (busytime + idletime)).intValue()+"%";
			} else {
				return "CPUʹ����:"+0+"%";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "CPUʹ����:"+0+"%";
		}
	}

	//��ȡcpu�����Ϣ
	private static long[] readCpu(final Process proc) {
		long[] retn = new long[2];
		try {
			proc.getOutputStream().close();
			InputStreamReader ir = new InputStreamReader(proc.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line = input.readLine();
			if (line == null || line.length() < FAULTLENGTH) {
				return null;
			}
			int capidx = line.indexOf("Caption");
			int cmdidx = line.indexOf("CommandLine");
			int rocidx = line.indexOf("ReadOperationCount");
			int umtidx = line.indexOf("UserModeTime");
			int kmtidx = line.indexOf("KernelModeTime");
			int wocidx = line.indexOf("WriteOperationCount");
			long idletime = 0;
			long kneltime = 0;
			long usertime = 0;
			while ((line = input.readLine()) != null) {
				if (line.length() < wocidx) {
					continue;
				}
				// �ֶγ���˳��Caption,CommandLine,KernelModeTime,ReadOperationCount,
				// ThreadCount,UserModeTime,WriteOperation
				String caption =substring(line, capidx, cmdidx - 1).trim();
				String cmd = substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("wmic.exe") >= 0) {
					continue;
				}
				String s1 = substring(line, kmtidx, rocidx - 1).trim();
				String s2 = substring(line, umtidx, wocidx - 1).trim();
				if (caption.equals("System Idle Process") || caption.equals("System")) {
					if (s1.length() > 0) {
						idletime += Long.valueOf(s1).longValue();
					}
					if (s2.length() > 0) {
						idletime += Long.valueOf(s2).longValue();
					}
					continue;
				}
				if (s1.length() > 0) {
					kneltime += Long.valueOf(s1).longValue();
				}
				if (s2.length() > 0) {
					usertime += Long.valueOf(s2).longValue();
				}
			}
			retn[0] = idletime;
			retn[1] = kneltime + usertime;
			return retn;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				proc.getInputStream().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * ����String.subString�Ժ��ִ����������⣨��һ��������Ϊһ���ֽ�)������� �������ֵ��ַ���ʱ�����������ֵ������£�
	 * @param src Ҫ��ȡ���ַ���
	 * @param start_idx ��ʼ���꣨����������)
	 * @param end_idx ��ֹ���꣨���������꣩
	 * @return
	 */
	private static String substring(String src, int start_idx, int end_idx) {
		byte[] b = src.getBytes();
		String tgt = "";
		for (int i = start_idx; i <= end_idx; i++) {
			tgt += (char) b[i];
		}
		return tgt;
	}

}