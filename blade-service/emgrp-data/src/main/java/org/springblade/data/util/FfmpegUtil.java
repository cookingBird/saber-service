package org.springblade.data.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 视频转码工具
 *
 * @author yiqimin
 * @create 2020/06/16
 */
@Component
@Slf4j
public class FfmpegUtil {


	private static String ffmpegInstallPath;

	@Value("${ffmpeg.install.path}")
	public void setFfmpegInstallPath(String path) {
		ffmpegInstallPath = path;
	}

	public static Map<String, String> getEncodingFormat(String filePath) {
		String processFLVResult = processFLV(filePath);
		Map<String, String> retMap = new HashMap();
		if (StringUtils.isNotBlank(processFLVResult)) {
			PatternCompiler compiler = new Perl5Compiler();
			try {
				String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
				String regexVideo = "Video: (.*?), (.*?(?:\\(.*?\\))*?), (.*?), (.*?), (.*?), (.*?), (.*?)[,\\s]";
				String regexAudio = "Audio: (.*?), (\\d*) Hz";
				String regexCreationTime = "creation_time\\s*:\\s*(\\d+-\\d+-\\w+:\\d+:\\d+.\\w+)";
				Pattern patternTime = compiler.compile(regexCreationTime, Perl5Compiler.CASE_INSENSITIVE_MASK);
				PatternMatcher matcherTime = new Perl5Matcher();
				if (matcherTime.contains(processFLVResult, patternTime)) {
					MatchResult re = matcherTime.getMatch();
					String group = re.group(1);
					retMap.put("creation_time", dealDateFormat(group));
				}

				Pattern patternDuration = compiler.compile(regexDuration, Perl5Compiler.CASE_INSENSITIVE_MASK);
				PatternMatcher matcherDuration = new Perl5Matcher();
				if (matcherDuration.contains(processFLVResult, patternDuration)) {
					MatchResult re = matcherDuration.getMatch();
					retMap.put("duration", getTimeLen(re.group(1)) + "");
					retMap.put("start", re.group(2));
					retMap.put("bitrate", re.group(3));
				}

				Pattern patternVideo = compiler.compile(regexVideo, Perl5Compiler.CASE_INSENSITIVE_MASK);
				PatternMatcher matcherVideo = new Perl5Matcher();
				if (matcherVideo.contains(processFLVResult, patternVideo)) {
					MatchResult re = matcherVideo.getMatch();
					retMap.put("videoCodec", re.group(1));
					retMap.put("resolution", re.group(3));
					retMap.put("fps", re.group(6));
				}

				Pattern patternAudio = compiler.compile(regexAudio, Perl5Compiler.CASE_INSENSITIVE_MASK);
				PatternMatcher matcherAudio = new Perl5Matcher();
				if (matcherAudio.contains(processFLVResult, patternAudio)) {
					MatchResult re = matcherAudio.getMatch();
					retMap.put("audioCodec", re.group(1));
					retMap.put("samplerate", re.group(2));
				}
			} catch (MalformedPatternException e) {
				e.printStackTrace();
			}
		}
		return retMap;
	}

	// ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
	public static String processFLV(String inputPath) {
		List commend = new ArrayList();
		commend.add(ffmpegInstallPath);
		commend.add("-i");
		commend.add(inputPath);
		return command(commend);

	}

	private static int getTimeLen(String timelen) {
		int min = 0;
		String strs[] = timelen.split(":");
		if (strs[0].compareTo("0") > 0) {
			min += Integer.valueOf(strs[0]) * 60 * 60;//秒
		}
		if (strs[1].compareTo("0") > 0) {
			min += Integer.valueOf(strs[1]) * 60;
		}
		if (strs[2].compareTo("0") > 0) {
			min += Math.round(Float.valueOf(strs[2]));
		}
		return min;
	}


	public static String command(List commend) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			// 保存ffmpeg的输出结果流
			BufferedReader buf = null;
			String line = null;

			buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

			StringBuffer sb = new StringBuffer();
			while ((line = buf.readLine()) != null) {
				System.out.println(line);
				sb.append(line);
				continue;
			}
			//这里线程阻塞，将等待外部转换进程运行成功运行结束后，才往下执行
			int ret = p.waitFor();
			p.destroy();
			return sb.toString();
		} catch (Exception e) {
			log.error("-- ffmpeg  error, message is {}", e);
			return null;
		}
	}

	/**
	 * mkv转MP4
	 *
	 * @param source 源文件地址
	 * @param target 目标文件地址
	 */
	public static String mkvToMp4(String source, String target) {
		List commend = new ArrayList();
		commend.add(ffmpegInstallPath);
		commend.add("-i");
		commend.add(source);
		commend.add("-vcodec");
		commend.add("copy");
		commend.add("-acodec");
		commend.add("copy");
		commend.add(target);
		return command(commend);
	}

	/**
	 * 转为640*480分辨率的视频
	 *
	 * @param source 源文件地址
	 * @param target 目标文件地址
	 */
	public static String resolutionRatioTo480(String source, String target) {
		List commend = new ArrayList();
		commend.add(ffmpegInstallPath);
		commend.add("-i");
		commend.add(source);
		commend.add("-vf");
		commend.add("scale=640:480,setdar=4:3");
		commend.add(target);
		commend.add("-hide_banner");
		return command(commend);
	}

	/**
	 * 转换为1280*720的视频
	 *
	 * @param source 源文件地址
	 * @param target 目标文件地址
	 */
	public static String resolutionRatioTo720(String source, String target) {
		List commend = new ArrayList();
		commend.add(ffmpegInstallPath);
		commend.add("-i");
		commend.add(source);
		commend.add("-vf");
		commend.add("scale=1280:720,setdar=4:3");
		commend.add(target);
		commend.add("-hide_banner");
		return command(commend);
	}

	/**
	 * 截屏
	 *
	 * @param source 源文件地址
	 * @param target 目标文件地址
	 */
	public static String screenshot(String source, String target) {
		//ffmpeg -ss 0.1 -t 0.001 -i 1.mp4 -y -f image2 -frames:v 1 0.jpg
		List commend = new ArrayList();
		commend.add(ffmpegInstallPath);
		commend.add("-ss");
		commend.add("0.1");
		commend.add("-t");
		commend.add("0.001");
		commend.add("-i");
		commend.add(source);
		commend.add("-y");
		commend.add("-f");
		commend.add("image2");
		commend.add("-frames:v");
		commend.add("1");
		commend.add(target);
		return command(commend);
	}

	public static String dealDateFormat(String oldDate) {
		Date date1 = null;
		DateFormat df2 = null;
		try {
			oldDate = oldDate.replace("Z", " UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
			Date date = df.parse(oldDate);
			SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
			date1 = df1.parse(date.toString());
			df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return df2.format(date1);
	}


//	public static void main(String[] args) {
//		System.out.println(getEncodingFormat("E:\\无人机视频演示\\城管.mp4"));
//	}

}
