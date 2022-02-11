package org.springblade.person.util;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class FileReader {


	//private static Map<String, String> hashMap =new HashMap<>();


	/**
	 * 读取tomap
	 * @param pathFile
	 * @param Map
	 * @return
	 */
	public static Map<String, String> readerFileToMap(String pathFile,Map<String, String> Map){

		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(pathFile), Charset.forName("gbk")));
			while (br.ready()){
				String tmp = getUTF8StringFromGBKString(br.readLine());
				String[] tems =tmp.split(",");
				String key = tems[1]+tems[2];
				if (Map.containsKey(key)){
					String[] old =Map.get(key).split(",");
					Date OldTime =  DateUtil.parse(old[0],DateUtil.PATTERN_DATETIME);
					Date newTime = DateUtil.parse(tems[0],DateUtil.PATTERN_DATETIME);
					if (!newTime.before(OldTime)){
						Map.put(key,tmp);
					}
				}else {
					Map.put(key, tmp);
				}
				//stringList.add(getUTF8StringFromGBKString(br.readLine()));
			}
		}catch (Exception e){
			log.error("解析出错！");
		}
		return Map;


	}
	/**
	 * 读取文件
	 * @param pathFile
	 * @return
	 */
	public static List<String> ReaderFile(String pathFile){
		List<String> stringList = new ArrayList<>();

		int count =1;
		try {
			URL url =new URL(pathFile);
			InputStreamReader isReader = null;
			BufferedReader bufReader = null;
			try {
				isReader = new InputStreamReader(url.openStream(),Charset.forName("gbk"));
				bufReader = new BufferedReader(isReader);

				while (bufReader.ready()) {
					String msg = FileReader.getUTF8StringFromGBKString(bufReader.readLine()).trim();
					if (StringUtil.isBlank(msg)){
						continue;
					}
					stringList.add(msg);
				}

			} finally {
				try {
					if (bufReader != null) {
						bufReader.close();
					}
				} catch (Exception e) {

				}
				try {
					if (isReader != null) {
						isReader.close();
					}
				} catch (Exception e) {

				}
			}

		}catch (Exception e){
			log.error("文件，解析出错！，URL:"+pathFile);

			return null;

		}
		return stringList;

	}



	public static String getUTF8StringFromGBKString(String gbkStr) {
		try {
			return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new InternalError();
		}
	}

	private static byte[] getUTF8BytesFromGBKString(String gbkStr) {
		int n = gbkStr.length();
		byte[] utfBytes = new byte[3 * n];
		int k = 0;
		for (int i = 0; i < n; i++) {
			int m = gbkStr.charAt(i);
			if (m < 128 && m >= 0) {
				utfBytes[k++] = (byte) m;
				continue;
			}
			utfBytes[k++] = (byte) (0xe0 | (m >> 12));
			utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
			utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
		}
		if (k < utfBytes.length) {
			byte[] tmp = new byte[k];
			System.arraycopy(utfBytes, 0, tmp, 0, k);
			return tmp;
		}
		return utfBytes;
	}

//	public static void main(String[] args) {
//		Long startime = System.currentTimeMillis();
//
//		Map<String, String> hashMap = new HashMap<>();
//		List<String> list = ReaderFile("file:///C:/Users/Administrator/Desktop/%E7%AC%AC%E4%BA%8C%E6%AC%A1%E9%87%87%E9%9B%86/控制面数据.csv");
////		List<String> list = ReaderFile("file:///C:/Users/29465/Desktop/用户信息.csv");
//		System.out.println(list.size());
//		list.forEach(e -> {
//			System.out.println(e);
//		});
//	}
		/*

		*//*hashMap = readerFileToMap("F:\\失联人员分析相关数据\\o_volte_csfb_20200701_tmp.log\\o_volte_csfb_20200701_tmp.log",hashMap);

		System.out.println("第一次计算："+hashMap.size());

		hashMap = readerFileToMap("F:\\失联人员分析相关数据\\o_volte_csfb_20200701_tmp.log\\o_volte_csfb_20200701_tmp.log",hashMap);
*//*



		//List<String> list2 = ReaderFile("F:\\失联人员分析相关数据\\o_volte_csfb_20200701_tmp.log\\o_volte_csfb_20200701_tmp.log");

		*//*list.forEach(tem->{
			String[] tems =tem.split(",");
			if (hashMap.containsKey(tems[1])){
				String[] old =hashMap.get(tems[1]).split(",");
			 	Date OldTime =  DateUtil.parse(old[0],DateUtil.PATTERN_DATETIME);

			 	Date newTime = DateUtil.parse(tems[0],DateUtil.PATTERN_DATETIME);

			 	if (!newTime.before(OldTime)){
					hashMap.put(tems[1],tem);
				}

			}else {
				hashMap.put(tems[1], tem);
			}

			//System.out.println(tem);
		});

		list2.forEach(tem->{
			String[] tems =tem.split(",");
			if (hashMap.containsKey(tems[1])){
				String[] old =hashMap.get(tems[1]).split(",");
				Date OldTime =  DateUtil.parse(old[0],DateUtil.PATTERN_DATETIME);

				Date newTime = DateUtil.parse(tems[0],DateUtil.PATTERN_DATETIME);

				if (!newTime.before(OldTime)){
					hashMap.put(tems[1],tem);
				}

			}else {
				hashMap.put(tems[1], tem);
			}

			//System.out.println(tem);
		});

		//list.addAll(list2);

		//System.out.println(hashMap.size());*//*



		Long endTime = System.currentTimeMillis();

		Long te = endTime-startime;
		System.out.println(hashMap.size());
		System.out.println("用时！:"+te/1000);
	}*/
}
