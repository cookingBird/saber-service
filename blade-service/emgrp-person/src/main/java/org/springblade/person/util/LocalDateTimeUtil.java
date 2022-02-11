package org.springblade.person.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * LocalDateTime
 * 时间转换
 */
public class LocalDateTimeUtil {



	/**
	 *
	 * @param strTime 时间
	 * @param pattern 格式 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static LocalDateTime strPaseLocalDateTime(String strTime,String pattern){

			Date returnDate = null;
			if (pattern == null) {
				pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS";
			}
			if (strTime != null && strTime.trim().length() != 0) {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				try {
					returnDate = sdf.parse(strTime);
				} catch (Exception var5) {
				}
			}
		Instant instant = returnDate.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		return localDateTime;
	};

	/**
	 * 时间转换
	 * @param time
	 * @return
	 */
	public static String localTimeToSrc(LocalDateTime time){
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		String localTime = df.format(time);

		return localTime;
	}

}
