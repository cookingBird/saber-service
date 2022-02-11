package org.springblade.person.thread;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class DownFileThread implements Runnable {
	//下载路径
	private String url;
	//保存路径
	private String saveFilePath;
	// 文件名
	private String fileName;

	public DownFileThread(String url, String saveFilePath, String fileName) {
		this.url = url;
		this.saveFilePath = saveFilePath;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		log.info("开始下载文件:%s", url);
		try {
			downRemoteFile(url, saveFilePath, fileName);
		}
		catch (Throwable throwable) {
			log.error("文件保存出错！", throwable);
			throwable.printStackTrace();
		}
	}


	public static String downRemoteFile(String remoteFileUrl, String saveFileName, String saveDir) {
		HttpURLConnection conn = null;
		OutputStream oputstream = null;
		InputStream iputstream = null;
		try {
			// 创建保存文件的目录
			File savePath = new File(saveDir);
			File saveFile = new File(saveFileName);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			}
			// 创建保存的文件
			File file = new File(saveFileName + "/" + savePath);
			log.info("文件路径:" + file + "");
			if (file != null && !file.exists()) {
				file.createNewFile();
			}

			URL url = new URL(remoteFileUrl);
			// 将url以open方法返回的urlConnection连接强转为HttpURLConnection连接(标识一个url所引用的远程对象连接)
			// 此时cnnection只是为一个连接对象,待连接中
			conn = (HttpURLConnection) url.openConnection();
			// 设置是否要从 URL连接读取数据,默认为true
			conn.setDoInput(true);
			// 建立连接
			// (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
			conn.connect();
			// 连接发起请求,处理服务器响应 (从连接获取到输入流)
			iputstream = conn.getInputStream();
			// 创建文件输出流，用于保存下载的远程文件
			oputstream = new FileOutputStream(file);
			//  用来存储响应数据
			byte[] buffer = new byte[4 * 1024];
			int byteRead = -1;
			//  循环读取流
			while ((byteRead = (iputstream.read(buffer))) != -1) {
				oputstream.write(buffer, 0, byteRead);
			}
			//  输出完成后刷新并关闭流
			oputstream.flush();

		}
		catch (Exception e) {
			log.error("下载信令文件出错", e);
		}
		finally {
			try {
				//  重要且易忽略步骤 (关闭流,切记!)
				if (iputstream != null) {
					iputstream.close();
				}
				if (oputstream != null) {
					oputstream.close();
				}
				// 销毁连接
				if (conn != null) {
					conn.disconnect();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 返回保存后的文件路径
		return saveDir + "/" + saveFileName;
	}
}
