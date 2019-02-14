package org.lyg.common.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.json.JSONObject;
/*
 * 
 *     ����³����
 * */
@SuppressWarnings("unused")
public class DevopsUtil {
	private static final int CPUTIME = 5000;
	/**
	 * ���ܽ�����Ϣ�ɼ�����(ע�⣺PERIOD_TIME һ��Ҫ���� SLEEP_TIME )
	 */
	private static final int PERIOD_TIME = 1000 * 60 * 15;
	/**
	 * ������Thread.sleep()����߳�˯��ʱ��
	 */
	private static final int SLEEP_TIME = 1000 * 60 * 9;
	private static final int PERCENT = 100;
	private static final int FAULTLENGTH = 10;
	private String isWindowsOrLinux = isWindowsOrLinux();
	private String pid = "";

	public Logger log = Logger.getLogger(DevopsUtil.class);

	/**
	 * �ж��Ƿ�������ϵͳ������Windows ���� Linux
	 *
	 * @return
	 */
	public String isWindowsOrLinux() {
		String osName = System.getProperty("os.name");
		String sysName = "";
		if (osName.toLowerCase().startsWith("windows")) {
			sysName = "windows";
		} else if (osName.toLowerCase().startsWith("linux")) {
			sysName = "linux";
		}
		return sysName;
	}

	/**
	 * ��ȡJVM ��CPUռ���ʣ�%��
	 *
	 * @return
	 */
	public String getCPURatio() {
		String cpuRatio = "";
		if (isWindowsOrLinux.equals("windows")) { // �жϲ���ϵͳ�����Ƿ�Ϊ��windows
			cpuRatio = getCPURatioForWindows();
		} else {
			cpuRatio = getCPURatioForLinux();
		}
		return cpuRatio;
	}

	//��ȡ�ļ�ϵͳʹ����
	public List<String> getDisk() {
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

	/**
	 * windows�����»�ȡJVM��PID
	 */
	public void getJvmPIDOnWindows() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		pid = runtime.getName().split("@")[0];
		log.info("PID of JVM:" + pid);
	}

	/**
	 * linux�����»�ȡJVM��PID
	 */
	public void getJvmPIDOnLinux() {
		String command = "pidof java";
		BufferedReader in = null;
		Process pro = null;
		try {
			pro = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
			in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			StringTokenizer ts = new StringTokenizer(in.readLine());
			pid = ts.nextToken();
			log.info("PID of JVM:" + pid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡJVM���ڴ�ռ���ʣ�%��
	 *
	 * @return
	 */
	public String getMemoryRatio() {
		String memRatio = "";
		if (isWindowsOrLinux.equals("windows")) { // �жϲ���ϵͳ�����Ƿ�Ϊ��windows
			memRatio = getMemoryRatioForWindows();// ��ѯwindowsϵͳ��cpuռ����
		} else {
			memRatio = getMemoryRatioForLinux();// ��ѯlinuxϵͳ��cpuռ����
		}
		return memRatio;
	}

	/**
	 * ��ȡJVM �߳���
	 *
	 * @return
	 */
	public int getThreadCount() {
		int threadCount = 0;
		if (isWindowsOrLinux.equals("windows")) { // �жϲ���ϵͳ�����Ƿ�Ϊ��windows
			threadCount = getThreadCountForWindows();// ��ѯwindowsϵͳ���߳���
		} else {
			threadCount = getThreadCountForLinux();// ��ѯlinuxϵͳ���߳���
		}
		return threadCount;
	}

	/**
	 * ��ȡ���̶�д���ʣ�MB/s��
	 *
	 * @return
	 */
	// public String getDiskAccess() { // TODO:�����
	// String diskAccess = "";
	// if (isWindowsOrLinux.equals("windows")) { // �жϲ���ϵͳ�����Ƿ�Ϊ��windows
	// // diskAccess = getDiskAccessForWindows(); // ��ѯwindowsϵͳ�Ĵ��̶�д����
	// } else {
	// diskAccess = getDiskAccessForLinux(); // ��ѯlinuxϵͳ�Ĵ��̶�д����
	// }
	// return diskAccess;
	// }

	/**
	 * ��ȡ������������MB/s��
	 *
	 * @return
	 */
	public String getNetworkThroughput() {
		String throughput = "";
		if (isWindowsOrLinux.equals("windows")) { // �жϲ���ϵͳ�����Ƿ�Ϊ��windows
			throughput = getNetworkThroughputForWindows(); // ��ѯwindowsϵͳ�Ĵ��̶�д����
		} else {
			throughput = getNetworkThroughputForLinux(); // ��ѯlinuxϵͳ�Ĵ��̶�д����
		}
		return throughput;
	}

	/**
	 * ��ȡWindows���������ڵ�����������
	 *
	 * @return
	 */
	public String getNetworkThroughputForWindows() {
		Process pro1 = null;
		Process pro2 = null;
		Runtime r = Runtime.getRuntime();
		BufferedReader input = null;
		String rxPercent = "";
		String txPercent = "";
		JSONObject jsonObject = new JSONObject();
		try {
			String command = "netstat -e";
			pro1 = r.exec(command);
			input = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String result1[] = readInLine(input, "windows");
			Thread.sleep(SLEEP_TIME);
			pro2 = r.exec(command);
			input = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			String result2[] = readInLine(input, "windows");
			rxPercent = formatNumber((Long.parseLong(result2[0]) - Long.parseLong(result1[0]))
					/ (float) (1024 * 1024 * (SLEEP_TIME / 1000))); // ��������(MB/s)
			txPercent = formatNumber((Long.parseLong(result2[1]) - Long.parseLong(result1[1]))
					/ (float) (1024 * 1024 * (SLEEP_TIME / 1000))); // ��������(MB/s)
			input.close();
			pro1.destroy();
			pro2.destroy();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		jsonObject.put("rxPercent", rxPercent); // ��������
		jsonObject.put("txPercent", txPercent); // ��������
		//        return JSON.toJSONStringWithDateFormat(jsonObject, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteMapNullValue);
		return jsonObject.toString();
	}

	/**
	 * ��ȡLinux���������ڵ�����������
	 *
	 * @return
	 */
	public String getNetworkThroughputForLinux() {
		Process pro1 = null;
		Process pro2 = null;
		Runtime r = Runtime.getRuntime();
		BufferedReader input = null;
		String rxPercent = "";
		String txPercent = "";
		JSONObject jsonObject = new JSONObject();
		try {
			String command = "watch ifconfig";
			pro1 = r.exec(command);
			input = new BufferedReader(new InputStreamReader(pro1.getInputStream()));

			String result1[] = readInLine(input, "linux");
			Thread.sleep(SLEEP_TIME);
			pro2 = r.exec(command);
			input = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			String result2[] = readInLine(input, "linux");
			rxPercent = formatNumber((Long.parseLong(result2[0]) - Long.parseLong(result1[0]))
					/ (float) (1024 * 1024 * (SLEEP_TIME / 1000))); // ��������(MB/s)
			txPercent = formatNumber((Long.parseLong(result2[1]) - Long.parseLong(result1[1]))
					/ (float) (1024 * 1024 * (SLEEP_TIME / 1000))); // ��������(MB/s)
			input.close();
			pro1.destroy();
			pro2.destroy();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		jsonObject.put("rxPercent", rxPercent); // ��������
		jsonObject.put("txPercent", txPercent); // ��������
		//        return JSON.toJSONStringWithDateFormat(jsonObject, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteMapNullValue);
		return jsonObject.toString();
	}

	/**
	 * ��ȡwindows������JVM��cpuռ����
	 *
	 * @return
	 */
	public String getCPURatioForWindows() {
		try {
			String procCmd = System.getenv("windir") + "\\system32\\wbem\\wmic.exe process "
					+ " get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
			// ȡ������Ϣ
			long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
			Thread.sleep(CPUTIME);
			long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
			if (c0 != null && c1 != null) {
				long idletime = c1[0] - c0[0];
				long busytime = c1[1] - c0[1];
				long cpuRatio = PERCENT * (busytime) / (busytime + idletime);
				if (cpuRatio > 100) {
					cpuRatio = 100;
				} else if (cpuRatio < 0) {
					cpuRatio = 0;
				}
				return String.valueOf(PERCENT * (busytime) / (busytime + idletime));

			} else {
				return "0.0";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "0.0";
		}
	}

	/**
	 * ��ȡlinux������JVM��cpuռ����
	 *
	 * @return
	 */
	public String getCPURatioForLinux() {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader brStat = null;
		StringTokenizer tokenStat = null;
		String user = "";
		String linuxVersion = System.getProperty("os.version");
		try {
			System.out.println("Linux�汾: " + linuxVersion);

			Process process = Runtime.getRuntime().exec(new String[] { "sh", "-c", "top -b -p " + pid });
			try {
				// top����Ĭ��3�붯̬���½����Ϣ�����߳�˯��5���Ա��ȡ���½��
				Thread.sleep(CPUTIME);
				is = process.getInputStream();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isr = new InputStreamReader(is);
			brStat = new BufferedReader(isr);

			if (linuxVersion.equals("2.4")) {
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();

				tokenStat = new StringTokenizer(brStat.readLine());
				tokenStat.nextToken();
				tokenStat.nextToken();
				user = tokenStat.nextToken();
				tokenStat.nextToken();
				String system = tokenStat.nextToken();
				tokenStat.nextToken();
				String nice = tokenStat.nextToken();

				System.out.println(user + " , " + system + " , " + nice);

				user = user.substring(0, user.indexOf("%"));
				system = system.substring(0, system.indexOf("%"));
				nice = nice.substring(0, nice.indexOf("%"));

				float userUsage = new Float(user).floatValue();
				float systemUsage = new Float(system).floatValue();
				float niceUsage = new Float(nice).floatValue();
				return String.valueOf((userUsage + systemUsage + niceUsage) / 100);
			} else {
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				tokenStat = new StringTokenizer(brStat.readLine());
				tokenStat.nextToken();
				String userUsage = tokenStat.nextToken(); // �û��ռ�ռ��CPU�ٷֱ�
				user = userUsage.substring(0, userUsage.indexOf("%"));
				process.destroy();
			}

		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			freeResource(is, isr, brStat);
			return "100";
		} finally {
			freeResource(is, isr, brStat);
		}
		return user; // jvm cpuռ����
	}

	/**
	 * ��ȡLinux������JVM���ڴ�ռ����
	 *
	 * @return
	 */
	public String getMemoryRatioForLinux() {
		Process pro = null;
		Runtime r = Runtime.getRuntime();
		String remCount = "";
		try {
			String command = "top -b -n 1 -H -p" + pid;
			pro = r.exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			StringTokenizer ts = new StringTokenizer(in.readLine());
			int i = 1;
			while (ts.hasMoreTokens()) {
				i++;
				ts.nextToken();
				if (i == 10) {
					remCount = ts.nextToken();
				}
			}
			in.close();
			pro.destroy();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return remCount;
	}

	/**
	 * ��ȡwindows������jvm���ڴ�ռ����
	 *
	 * @return
	 */
	public String getMemoryRatioForWindows() {
		String command = "TASKLIST /NH /FO CSV /FI \"PID EQ " + pid + " \"";
		String remCount = ""; // jvm�����ڴ�ռ����
		BufferedReader in = null;
		String result = "";
		try {
			Process pro = Runtime.getRuntime().exec(command);
			in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			StringTokenizer ts = new StringTokenizer(in.readLine(), "\"");
			int i = 1;
			while (ts.hasMoreTokens()) {
				i++;
				ts.nextToken();
				if (i == 9) {
					remCount = ts.nextToken().replace(",", "").replace("K", "").trim();
				}
			}
			long physicalJvmMem = Long.parseLong(remCount) / 1024; // jvm�����ڴ�ռ����(MB)
			OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			//  long physicalTotal = osmxb.getTotalPhysicalMemorySize() / (1024 * 1024); // ��ȡ�������������ڴ�(MB)
			//   result = formatNumber(physicalJvmMem / (float) physicalTotal);
			//            if (Float.parseFloat(result) > 1) { // ռ�������ֻ����100%
			//                result = "1";
			//            } else if (Float.parseFloat(result) < 0) {
			//                result = "0";
			//            }
			in.close();
			pro.destroy();
		} catch (Exception e) {
			log.error("getThreadCountForWindows()���쳣��" + e);
		}
		return String.valueOf((Float.parseFloat(result) * 100));
		//        
		//        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		//        String osName=System.getProperty("os.name");
		//        long totalMemorySize = osmxb..getTotalPhysicalMemorySize() / kb;
		//        long freePhysicalMemorySize=osmxb.getSystemLoadAverage()/kb;
	}

	/**
	 * ��ȡlinux���̶�д����
	 *
	 * @return
	 */
	// public String getDiskAccessForLinux() {
	// Process pro = null;
	// Runtime r = Runtime.getRuntime();
	// String command = "time dd if=/dev/zero of=/temp.log bs=1024k count=1000";
	// // ��ȡ��д��1G����
	// BufferedReader in = null;
	// float result = 0.0f;
	// try {
	// pro = r.exec(new String[] { "sh", "-c", command });
	// // pro.getInputStream()ȡ����������ݣ�������getErrorStream()����ȡ��ֵ
	// in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
	// in.readLine();
	// in.readLine();
	// if (getLocale().indexOf("zh") != -1) { // �������Ի���
	// StringTokenizer ts = new StringTokenizer(in.readLine(), "��");
	// ts.nextToken();
	// ts.nextToken();
	// result = Float.parseFloat(ts.nextToken().split(" ")[0]);
	// System.out.println("���Ļ���");
	// } else { // Ӣ�����Ի���
	// StringTokenizer ts = new StringTokenizer(in.readLine());
	// ts.nextToken();
	// ts.nextToken();
	// ts.nextToken();
	// ts.nextToken();
	// ts.nextToken();
	// ts.nextToken();
	// ts.nextToken();
	// result = Float.parseFloat(ts.nextToken().split(" ")[0]);
	// System.out.println("Ӣ�Ļ���");
	// }
	// r.exec("rm -f /temp.log"); // �����ɵ��ļ�ɾ��
	// in.close();
	// pro.destroy();
	// } catch (IOException e) {
	// System.out.println(e.getMessage());
	// }
	// return String.valueOf(result);
	// }

	/**
	 * ��ȡLinux�����������Ի���
	 *
	 * @return
	 */
	public String getLocale() {
		Process pro = null;
		Runtime r = Runtime.getRuntime();
		String command = "locale";
		BufferedReader in = null;
		StringTokenizer ts = null;
		try {
			pro = r.exec(command);
			in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			ts = new StringTokenizer(in.readLine());
			in.close();
			pro.destroy();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return ts.nextToken();
	}

	/**
	 * ��ȡLinux������JVM���߳���
	 *
	 * @return
	 */
	public int getThreadCountForLinux() {
		Process pro = null;
		Runtime r = Runtime.getRuntime();
		String command = "top -b -n 1 -H -p " + pid;
		BufferedReader in = null;
		int result = 0;
		try {
			pro = r.exec(new String[] { "sh", "-c", command });
			in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			in.readLine();
			StringTokenizer ts = new StringTokenizer(in.readLine());
			ts.nextToken();
			result = Integer.parseInt(ts.nextToken());
			in.close();
			pro.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * ��ȡWindows������JVM���߳���
	 *
	 * @return
	 */
	public int getThreadCountForWindows() {
		String command = "wmic process " + pid + " list brief";
		int count = 0;
		BufferedReader in = null;
		try {
			Process pro = Runtime.getRuntime().exec(command);
			in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			// testGetInput(in);
			in.readLine();
			in.readLine();
			StringTokenizer ts = new StringTokenizer(in.readLine());
			int i = 1;

			while (ts.hasMoreTokens()) {
				i++;
				ts.nextToken();
				if (i == 5) {
					count = Integer.parseInt(ts.nextToken());
				}
			}
			in.close();
			pro.destroy();
		} catch (Exception e) {
			log.error("getThreadCountForWindows()���쳣��" + e);
		}
		return count;
	}

	private void freeResource(InputStream is, InputStreamReader isr, BufferedReader br) {
		try {
			if (is != null) {
				is.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (br != null) {
				br.close();
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	/**
	 *
	 * ��ȡCPU��Ϣ
	 *
	 * @param proc
	 * @return
	 *
	 */
	private long[] readCpu(final Process proc) {
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
			// Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount
			long idletime = 0;
			long kneltime = 0;
			long usertime = 0;
			while ((line = input.readLine()) != null) {
				if (line.length() < wocidx) {
					continue;
				}
				// �ֶγ���˳��Caption,CommandLine,KernelModeTime,ReadOperationCount,
				// ThreadCount,UserModeTime,WriteOperation
				String caption = this.substring(line, capidx, cmdidx - 1).trim();
				String cmd = this.substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("javaw.exe") >= 0) {
					continue;
				}
				// log.info("line="+line);
				if (caption.equals("System Idle Process") || caption.equals("System")) {
					idletime += Long.valueOf(this.substring(line, kmtidx, rocidx - 1).trim()).longValue();
					idletime += Long.valueOf(this.substring(line, umtidx, wocidx - 1).trim()).longValue();
					continue;
				}

				kneltime += Long.valueOf(this.substring(line, kmtidx, rocidx - 1).trim()).longValue();
				usertime += Long.valueOf(this.substring(line, umtidx, wocidx - 1).trim()).longValue();
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
	 * ��ȡ��������������
	 *
	 * @param input
	 * @return
	 */
	public String[] readInLine(BufferedReader input, String osType) {
		String rxResult = "";
		String txResult = "";
		StringTokenizer tokenStat = null;
		try {
			if (osType.equals("linux")) { // ��ȡlinux�����µ���������������
				String result[] = input.readLine().split(" ");
				int j = 0, k = 0;
				for (int i = 0; i < result.length; i++) {
					if (result[i].indexOf("RX") != -1) {
						j++;
						if (j == 2) {
							rxResult = result[i + 1].split(":")[1];
						}
					}
					if (result[i].indexOf("TX") != -1) {
						k++;
						if (k == 2) {
							txResult = result[i + 1].split(":")[1];
							break;
						}
					}
				}

			} else { // ��ȡwindows�����µ���������������
				input.readLine();
				input.readLine();
				input.readLine();
				input.readLine();
				tokenStat = new StringTokenizer(input.readLine());
				tokenStat.nextToken();
				rxResult = tokenStat.nextToken();
				txResult = tokenStat.nextToken();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		String arr[] = { rxResult, txResult };
		return arr;
	}

	/**
	 * ����String.subString�Ժ��ִ���������⣨��һ��������Ϊһ���ֽ�)������� �������ֵ��ַ���ʱ�����������ֵ������£�
	 *
	 * @param src
	 * Ҫ��ȡ���ַ���
	 * @param start_idx
	 * ��ʼ���꣨����������)
	 * @param end_idx
	 * ��ֹ���꣨���������꣩
	 * @return
	 */
	private String substring(String src, int start_idx, int end_idx) {
		byte[] b = src.getBytes();
		String tgt = "";
		for (int i = start_idx; i <= end_idx; i++) {
			tgt += (char) b[i];
		}
		return tgt;
	}

	/**
	 * ��ʽ��������(float �� double)��������λС��
	 *
	 * @param obj
	 * @return
	 */
	private String formatNumber(Object obj) {
		String result = "";
		if (obj.getClass().getSimpleName().equals("Float")) {
			result = new Formatter().format("%.2f", (float) obj).toString();
		} else if (obj.getClass().getSimpleName().equals("Double")) {
			result = new Formatter().format("%.2f", (double) obj).toString();
		}
		return result;
	}

	/**
	 * ���Է��� �����javaִ�����������Ƿ��ܻ�ȡ�������(ע���˷���ִ�к���жϳ����ִ�У����������ע�͵�)
	 *
	 */
	public void testGetInput(BufferedReader in) {
		int y = 0;
		try {
			while ((y = in.read()) != -1) {
				System.out.print((char) y);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}