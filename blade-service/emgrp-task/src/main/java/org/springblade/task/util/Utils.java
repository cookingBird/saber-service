package org.springblade.task.util;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
@Slf4j
public class Utils {
	public static String downlaodFile(String url, String localPath, String fileName) {
		String fullPath = null != fileName ? localPath + File.separator + fileName : localPath;
		HttpUtil.downloadFile(url, new File(fullPath), 1000 * 60 * 10, new StreamProgress(){
			@Override
			public void start() {
				log.info("{} -> {} 开始下载" , url, fullPath);
			}

			@Override
			public void progress(long progressSize) {
				log.info("{} -> {} 下载中：{}", url, fullPath, FileUtil.readableFileSize(progressSize));
			}

			@Override
			public void finish() {
				log.info("{} -> {} 下载完成", url, fullPath);
			}
		});
		return fullPath;
	}
}
