package org.springblade.data.service.impl;

import io.minio.MinioClient;
import io.minio.ServerSideEncryption;
import lombok.SneakyThrows;
import org.springblade.core.oss.minio.MinioTemplate;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.oss.props.OssProperties;
import org.springblade.core.oss.rule.OssRule;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.FileUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

/**
 * 自定义minio
 *
 * @author yiqimin
 * @create 2020/08/13
 */

@Component
public class MyMinioTemplate extends MinioTemplate{

	@Value("${oss.network}")
	private String MINIO_URL;
	private MinioClient client;
	private OssRule ossRule;
	private OssProperties ossProperties;

	public MyMinioTemplate(MinioClient client, OssRule ossRule, OssProperties ossProperties) {
		super(client, ossRule, ossProperties);
		this.client = client;
		this.ossRule = ossRule;
		this.ossProperties = ossProperties;
	}

	@Override
	public String fileLink(String bucketName, String fileName) {
		try {
			return MINIO_URL.concat("/").concat(bucketName).concat("/").concat(fileName);
		} catch (Throwable var4) {
			throw var4;
		}
	}

	@SneakyThrows
	public BladeFile putFile(String bucketName, String fileName, InputStream stream, Long taskId) {
		try {
			this.makeBucket(bucketName);
			String originalName = fileName;
			fileName = this.getMinioFileName(taskId, fileName);
			this.client.putObject(bucketName, fileName, stream, (long)stream.available(), (Map)null, (ServerSideEncryption)null, "application/octet-stream");
			BladeFile file = new BladeFile();
			file.setOriginalName(originalName);
			file.setName(fileName);
			file.setDomain(this.getOssHost(bucketName));
			file.setLink(this.fileLink(bucketName, fileName));
			return file;
		} catch (Throwable var6) {
			throw var6;
		}
	}

	public static String getMinioFileName(Long taskId, String originalFilename) {
		return taskId+ "/" + DateUtil.today() + "/" + StringUtil.randomUUID() + "." + FileUtil.getFileExtension(originalFilename);
	}
}
