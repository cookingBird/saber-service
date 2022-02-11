package org.springblade.uav.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName Md5Utils
 * @Description TODO
 * @Author wt
 * @Date 2020/12/16 16:42
 * @Version 1.0
 **/
public class Md5Utils {
	public static String getMD5(String md5) {
		String strInfoDigest = "";
		MessageDigest ms;
		try {
			ms = MessageDigest.getInstance("MD5");
			ms.update(md5.getBytes());
			byte[] bs = ms.digest();
			strInfoDigest = byteToHex(bs);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("MD5加密失败===================");
		}
		return strInfoDigest.toUpperCase();
	}

	public static String byteToHex(byte[] bs) {
		String str = "";
		String strTemp = "";
		for (int i = 0; i < bs.length; i++) {
			strTemp = Integer.toHexString(bs[i] & 255);
			if (strTemp.length() == 1) {
				str = (new StringBuilder(str)).append("0").append(strTemp).toString();
			} else {
				str = (new StringBuilder(str)).append(strTemp).toString();
			}
		}
		return str.toUpperCase();
	}

}
