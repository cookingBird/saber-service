package org.springblade.data.util;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yiqimin
 * @create 2020/06/04
 */
public class FileUtil {

	protected static Logger log = LoggerFactory.getLogger(FileUtil.class);

	public static final String videoSuffix = "mp4";

	/**
	 * 获取图片属性信息
	 *
	 * @param inputStream
	 * @return
	 * @throws JpegProcessingException
	 * @throws IOException
	 */
	public static Map<String, String> getImageTags(InputStream inputStream) {
		Map<String, String> map = new HashMap<>();

		Metadata metadata = null;
		try {
			metadata = JpegMetadataReader.readMetadata(inputStream);
		} catch (Exception e) {
			log.error("读取图片属性错误", e);
			return map;
		}
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				String tagName = tag.getTagName();
				if (tagName.equals("GPS Latitude") || tagName.equals("GPS Longitude")) {
					map.put(tagName, pointToLatlong(tag.getDescription()));
					continue;
				}
				map.put(tagName, tag.getDescription());
			}
		}
		return map;
	}

	public static String pointToLatlong(String point) {
		Double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());
		Double fen = Double.parseDouble(point.substring(point.indexOf("°") + 1, point.indexOf("'")).trim());
		Double miao = Double.parseDouble(point.substring(point.indexOf("'") + 1, point.indexOf("\"")).trim());
		Double duStr = du + fen / 60 + miao / 60 / 60;
		return duStr.toString();
	}

	public static String getSuffix(String fileName) {
		String[] split = fileName.split("\\.");
		String format = split[split.length - 1];
		return format.toLowerCase();
	}

	public static boolean isPicture(String fileName) {
		String suffix = getSuffix(fileName);
		if (suffix.equals("jpg") || suffix.equals("png") || suffix.equals("jpeg")) {
			return true;
		}
		return false;
	}

	public static boolean isVideo(String fileName) {
		String suffix = getSuffix(fileName);
		if (suffix.equals(videoSuffix) || suffix.equals("mkv")) {
			return true;
		}
		return false;
	}

	/**
	 * 文件转为二进制字符串
	 * @param file
	 * @return
	 */
	public static String fileToBinStr(File file){
		try {
			InputStream fis = new FileInputStream(file);
			byte[] bytes = FileCopyUtils.copyToByteArray(fis);
			return new String(bytes,"ISO-8859-1");
		}catch (Exception ex){
			throw new RuntimeException("transform file into bin String 出错",ex);
		}
	}


	/**
	 * 二进制字符串转文件
	 * @param bin
	 * @param fileName
	 * @param parentPath
	 * @return
	 */
	public static File binToFile(String bin,String fileName,String parentPath){
		try {
			File fout = new File(parentPath,fileName);
			fout.createNewFile();
			byte[] bytes1 = bin.getBytes("ISO-8859-1");
			FileCopyUtils.copy(bytes1,fout);

			//FileOutputStream outs = new FileOutputStream(fout);
			//outs.write(bytes1);
			//outs.flush();
			//outs.close();

			return fout;
		}catch (Exception ex){
			throw new RuntimeException("transform bin into File 出错",ex);
		}
	}

	public static String getImgStr(String imgFile) {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.encodeBase64String(data);
	}

//	@SneakyThrows
//	public static void main(String[] args) {
//		double a = 103.90909;
//		double b = 30.58971;
////		for (int i = 0; i < 361 ; i++ ) {
////			System.out.println(new BigDecimal(a).setScale(6, RoundingMode.UP));
////			a = a + 0.000090;
////		}
//
//		// 1、创建客户端的 Socket 服务
//		Socket socket = new Socket("211.149.129.108", 11000);
//
//		// 2、获取 Socket 流中输入流
//		OutputStream out = socket.getOutputStream();
//		for (int i = 0; i < 361 ; i++ ) {
//			BigDecimal decimal = new BigDecimal(a).setScale(6, RoundingMode.UP);
//			System.out.println(decimal.floatValue());
//			String str =  "{\n" +
//				"\"action\":\"UAVDATA\",\n" +
//				"\"key\":\"+nuG(k8I1)Ia< /8\",\n" +
//				"\"uav_no\":\"u002\",\n" +
//				"\"fly_status\":1,\n" +
//				"\"time\":\""+ DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"\",\n" +
//				"\"lon\":"+decimal.floatValue()+",\n" +
//				"\"lat\":30.58971,\n" +
//				"\"alt\":115.872,\n" +
//				"\"ground_alt\":0.0,\n" +
//				"\"course\":0.0,\n" +
//				"\"pitch\":-3.0,\n" +
//				"\"roll\":4.0,\n" +
//				"\"yaw\":270.0,\n" +
//				"\"true_airspeed\":0.0,\n" +
//				"\"ground_speed\":0.0,\n" +
//				"\"remaining_oil\":80.0,\n" +
//				"\"remaining_dis\":500.0,\n" +
//				"\"remaining_time\":80.0,\n" +
//				"\"mot_status\":0,\n" +
//				"\"nav_status\":0,\n" +
//				"\"temperature\":12.3,\n" +
//				"\"humidity\":12.3,\n" +
//				"\"wind_speed\":0.0\n" +
//				"}SWOOLEFN";
//			// 3、使用输出流将指定的数据写出去
//			out.write(str.getBytes());
//			a = a + 0.000590;
//
//
////			BigDecimal decimal2 = new BigDecimal(b).setScale(6, RoundingMode.UP);
////			System.out.println(decimal2.floatValue());
////			String str2 =  "{\n" +
////				"\"action\":\"UAVDATA\",\n" +
////				"\"key\":\"FeY*T;#iq\\\"=(upHn\",\n" +
////				"\"uav_no\":\"u005\",\n" +
////				"\"fly_status\":1,\n" +
////				"\"time\":\""+ DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")+"\",\n" +
////				"\"lon\":103.90909,\n" +
////				"\"lat\":"+b+",\n" +
////				"\"alt\":115.872,\n" +
////				"\"ground_alt\":0.0,\n" +
////				"\"course\":0.0,\n" +
////				"\"pitch\":-3.0,\n" +
////				"\"roll\":4.0,\n" +
////				"\"yaw\":270.0,\n" +
////				"\"true_airspeed\":0.0,\n" +
////				"\"ground_speed\":0.0,\n" +
////				"\"remaining_oil\":80.0,\n" +
////				"\"remaining_dis\":500.0,\n" +
////				"\"remaining_time\":80.0,\n" +
////				"\"mot_status\":0,\n" +
////				"\"nav_status\":0,\n" +
////				"\"temperature\":12.3,\n" +
////				"\"humidity\":12.3,\n" +
////				"\"wind_speed\":0.0\n" +
////				"}SWOOLEFN";
////			// 3、使用输出流将指定的数据写出去
////			out.write(str2.getBytes());
////			b = b + 0.000090;
//
//
//
//			Thread.sleep(30000);
//		}
////"+nuG(k8I1)Ia< /8"-------002
////		FeY*T;#iq\"=(upHn ---------u005
////		String s = "{\n" +
////			"  \"action\": \"START\",\n" +
////			"  \"uav_no\": \"u005\",\n" +
////			"  \"time\": \"2020-09-23 22:02:00\",\n" +
////			"\"airline_no\": \" AR_20200713142514\",\n" +
////			"  \"lon\": 150.7840271,\n" +
////			"  \"lat\": 108.4068375,\n" +
////			"    \"alt\": 1154383.872,\n" +
////			"  \"waypoints\": [{\n" +
////			"	\"id\": \" 1\",\n" +
////			"   \"lon\": 150.7840271,\n" +
////			"   \"lat\": 108.4068375,\n" +
////			"     \"alt\": 115.4383872,\n" +
////			"\"ground_alt\":100\n" +
////			"},{\n" +
////			"\"id\": \" 2\", \n" +
////			"   \"lon\": 150.7840271,\n" +
////			"   \"lat\": 108.4068375,\n" +
////			"     \"alt\": 115.4383872,\n" +
////			"\"ground_alt\":100\n" +
////			"}]\n" +
////			"}SWOOLEFN ";
////		out.write(s.getBytes());
//
////		InputStream ips = socket.getInputStream();
////		InputStreamReader ipsr = new InputStreamReader(ips);
////		BufferedReader br = new BufferedReader(ipsr);
////		String ss = "";
////		while((ss = br.readLine()) != null)
////			System.out.println(ss);
////		socket.close();
//
//
////		ByteArrayOutputStream os = new ByteArrayOutputStream();
////		InputStream  is = socket.getInputStream();
////		byte[] buffer = new byte[1024];
////		is.read(buffer);
////		os.write(buffer);
////		System.out.println(os.toString());
//		socket.close();
//
//
//	}
}
