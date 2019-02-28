package org.lyg.common.utils;

import java.io.*;
@SuppressWarnings({"resource","unused"})
public class FileReader {
	public static String readTxtFile(String filePath){
		try {
			String encoding="GBK";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),encoding);//���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) != null){
					System.out.println(lineTxt); // һ��һ�еĶ�
					return lineTxt;
				}

			}else{
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
		return "���ļ�������";
	}

	public static void writeTxtFile(String filePath){
		try {
			String encoding="GBK";
			File writename = new File(".\\result\\en\\output.txt"); // ���·�������û����Ҫ����һ���µ�output.txt�ļ�
			writename.createNewFile(); // �������ļ�
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write("д���ļ�\r\n"); // \r\n��Ϊ����
			out.flush(); // �ѻ���������ѹ���ļ�
			out.close(); // ���ǵùر��ļ�
		} catch (Exception e) {
			System.out.println("д���ļ����ݳ���");
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		readTxtFile("/Users/sunybert/CMakeCache.txt");
	}
}