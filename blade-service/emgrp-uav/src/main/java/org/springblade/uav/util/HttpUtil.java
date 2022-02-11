package org.springblade.uav.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求实用类
 *
 *
 */
public final class HttpUtil {
	/** 日志记录对象性*/
	private static Logger log = Logger.getLogger(HttpUtil.class);
	/** get请求 */
	public static final String HTTP_GET = "GET";
	/** post请求 */
	public static final String HTTP_POST = "POST";
	/** put请求 */
	public static final String HTTP_PUT = "PUT";
	/** HTTP请求默认超时时间 */
	private static final int DEFAULT_TIMEOUT = 5000;
	/** HTTP默认字符编码集 */
	private static final String DEFAULT_CHARSET = "UTF-8";
	/** HTTP默认内容类型 */
	private static final String DEFAULT_CONTENTTYPE = "application/json";

	private static TrustManager myX509TrustManager = new X509TrustManager() {
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		}
	};
	/**
	 * 执行Get请求
	 * @param requestUrl	请求地址
	 * @param params		参数
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl, Map<String,String> params) throws Exception
	{
		return doGet(requestUrl, params, DEFAULT_TIMEOUT);
	}
	/**
	 * 执行Get请求
	 * @param requestUrl	请求地址
	 * @param params		参数
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl, Map<String,String> params, int timeout) throws Exception
	{
		return doGet(requestUrl, params, null, DEFAULT_CHARSET, timeout);
	}
	/**
	 * 执行Get请求
	 * @param requestUrl	请求地址
	 * @param params		参数
	 * @param contentType	请求内容类型
	 * @param charset		字符编码集
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl, Map<String,String> params, String contentType, String charset, int timeout) throws Exception
	{
		String paramsStr = getParamsStr(params);
		requestUrl = buildReqUrl(requestUrl, paramsStr);
		return httpRequest(requestUrl, HTTP_GET, "", contentType, charset, timeout);
	}
	/**
	 * 执行Post请求
	 * @param requestUrl
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, Map<String,String> params) throws Exception
	{
		return doPost(requestUrl, params, DEFAULT_TIMEOUT);
	}
	/**
	 * 执行Post请求
	 * @param requestUrl	请求地址
	 * @param params		参数
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, Map<String,String> params, int timeout) throws Exception
	{
		return doPost(requestUrl, params, DEFAULT_CONTENTTYPE, DEFAULT_CHARSET, timeout);
	}
	/**
	 * 执行Post请求
	 * @param requestUrl	请求地址
	 * @param params		参数
	 * @param contentType	请求内容类型
	 * @param charset		字符编码集
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, Map<String,String> params, String contentType, String charset, int timeout) throws Exception
	{
		String paramsStr = getParamsStr(params);
		return doPost(requestUrl, paramsStr, contentType, charset, timeout);
	}
	/**
	 * 执行Post请求
	 * @param requestUrl	请求地址
	 * @param data			请求数据
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, String data) throws Exception
	{
		return doPost(requestUrl, data, DEFAULT_TIMEOUT);
	}
	/**
	 * 执行Post请求
	 * @param requestUrl	请求地址
	 * @param data			请求数据
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, String data, int timeout) throws Exception
	{
		return doPost(requestUrl, data, DEFAULT_CONTENTTYPE, DEFAULT_CHARSET, timeout);
	}
	/**
	 * 执行Post请求
	 * @param requestUrl	请求地址
	 * @param data			请求数据
	 * @param contentType	请求内容类型
	 * @param charset		字符编码集
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, String data, String contentType, String charset, int timeout) throws Exception
	{
		return httpRequest(requestUrl, HTTP_POST, data, contentType, charset, timeout);
	}
	/**
	 * 执行HTTP请求
	 * @param requestUrl	请求地址
	 * @param requestMethod	请求类型（POST、GET）
	 * @param params		请求数据
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod, Map<String,String> params) throws Exception
	{
		return httpRequest(requestUrl, requestMethod, params, DEFAULT_TIMEOUT);
	}
	/**
	 * 执行HTTP请求
	 * @param requestUrl	请求地址
	 * @param requestMethod	请求类型（POST、GET）
	 * @param params		请求数据
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod, Map<String,String> params, int timeout) throws Exception
	{
		String outStr = getParamsStr(params);
		if(requestMethod.equalsIgnoreCase(HTTP_GET))
		{
			requestUrl = buildReqUrl(requestUrl,outStr);
			outStr = "";
		}
		return httpRequest(requestUrl, requestMethod, outStr, timeout);
	}
	/**
	 * 执行HTTP请求
	 * @param requestUrl	请求地址
	 * @param requestMethod	请求类型（POST、GET）
	 * @param data			请求数据
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod, String data) throws Exception
	{
		return httpRequest(requestUrl, requestMethod, data, DEFAULT_TIMEOUT);
	}
	/**
	 * 执行HTTP请求
	 * @param requestUrl	请求地址
	 * @param requestMethod	请求类型（POST、GET）
	 * @param data			请求数据
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod, String data, int timeout) throws Exception
	{
		return httpRequest(requestUrl, requestMethod, data, null, DEFAULT_CHARSET, timeout);
	}
	/**
	 * 执行HTTP请求
	 * @param requestUrl	请求地址
	 * @param requestMethod	请求类型（POST、GET）
	 * @param data			请求数据
	 * @param contentType	请求内容类型
	 * @param charset		字符编码集
	 * @param timeout		请求超时间，单位：毫秒，0：表示不超时
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod, String data, String contentType, String charset, int timeout) throws Exception
	{
		CloseableHttpClient httpClient = null;
		try
		{
			if(StringUtils.isEmpty(charset))
			{
				charset = DEFAULT_CHARSET;
			}
			HttpClientBuilder httpBuilder = HttpClientBuilder.create();
			httpClient = httpBuilder.build();
			RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(timeout)//设置连接超时时间，单位毫秒
				.setConnectionRequestTimeout(1000)//设置从connect Manager获取Connection 超时时间，单位毫秒
				.setSocketTimeout(timeout).build();//请求获取数据的超时时间，单位毫秒
			HttpUriRequest request = null;
			if(HTTP_POST.equalsIgnoreCase(requestMethod))
			{
				HttpPost post = new HttpPost(requestUrl);
				post.setConfig(requestConfig);
				if(data != null && data.trim().length() > 0)
				{
					post.setEntity(new StringEntity(data, charset));
				}
				request = post;
			}
			else
			{
				if(data != null && data.trim().length() > 0)
				{
					requestUrl = buildReqUrl(requestUrl, data);
				}
				HttpGet get = new HttpGet(requestUrl);
				get.setConfig(requestConfig);
				request = get;
			}
			if(StringUtils.isNotEmpty(contentType))
			{
				request.addHeader(HTTP.CONTENT_TYPE, ContentType.create(contentType, charset).toString());
			}
			log.info("[http] 请求开始，请求url:" + requestUrl);
			HttpResponse httpResponse = httpClient.execute(request);
			//获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			//响应状态
			log.debug("[http] status:" + httpResponse.getStatusLine());
			//判断响应实体是否为空
			if (entity != null) {
				String content = EntityUtils.toString(entity);
				log.info("[http] response content:" + content);
				return content;
			}
			return null;
		}
		finally
		{
			if(httpClient != null)
			{
				try
				{
					httpClient.close();
				}catch(Exception e){}
			}
		}
	}
	/**
	 * 执行http请求(Java源版)，不建议使用
	 * @param requestUrl
	 * @param requestMethod
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String httpRequestForJava(String requestUrl,String requestMethod,String data) throws Exception{
		String ret = null;
		HttpURLConnection httpUrlConn = null;
		InputStream is = null;
		BufferedReader bReader = null;
		InputStreamReader isr = null;
		try {
			log.info("[http] 请求开始，请求url:" + requestUrl);
			URL url = new URL(requestUrl);
			if(requestUrl.startsWith("https://")){
				SSLContext sslcontext = SSLContext.getInstance("TLS");
				sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
				httpUrlConn = (HttpsURLConnection)url.openConnection();
				((HttpsURLConnection)httpUrlConn).setSSLSocketFactory(sslcontext.getSocketFactory());

			}else{
				httpUrlConn=(HttpURLConnection)url.openConnection();
			}
			//设定连接超时时间
			httpUrlConn.setConnectTimeout(30*1000);
			//设置读取超时时间
			httpUrlConn.setReadTimeout(30*1000);
			//设置是否向httpUrlConnection输出
			httpUrlConn.setDoOutput(true);
			//设置是否从httpUrlConnection读入，默认情况下是true;
			httpUrlConn.setDoInput(true);

			httpUrlConn.setUseCaches(false);
			//设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if(data != null && data.length() > 0){
				httpUrlConn.getOutputStream().write(data.getBytes(DEFAULT_CHARSET));
			}

			// 将返回的输入流转换成字符串
			is = httpUrlConn.getInputStream();
			isr = new InputStreamReader(is, DEFAULT_CHARSET);
			bReader = new BufferedReader(isr);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bReader.readLine()) != null) {
				buffer.append(str).append("\n");
			}
			ret = buffer.length() > 0 ? buffer.substring(0, buffer.length() - 1) : buffer.toString();
			log.info("[http] 请求完成");
		}finally{
			if(bReader != null){
				try{
					bReader.close();
					bReader = null;
				}catch(Exception e){

				}
			}
			if(isr != null){
				try{
					isr.close();
					isr = null;
				}catch(Exception e){

				}
			}
			if(is != null){
				try{
					is.close();
					is = null;
				}catch(Exception e){

				}
			}
			if(httpUrlConn != null){
				try{
					httpUrlConn.disconnect();
					httpUrlConn = null;
				}catch(Exception e){

				}
			}
		}
		return ret;
	}
	/**
	 * 根据map获取请求参数串
	 * @param params
	 * @return
	 */
	private static String getParamsStr(Map<String,String> params){
		StringBuilder sb = new StringBuilder("");
		if(params != null){
			Iterator<Map.Entry<String, String>> ite = params.entrySet().iterator();
			while(ite.hasNext()){
				Map.Entry<String, String> entry = ite.next();
				if(entry.getValue() != null){
					sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
				}
			}
		}
		if(sb.length() > 1){
			return sb.toString().substring(1);
		}else{
			return "";
		}
	}
	/**
	 * 合成请求url
	 * @param requestUri
	 * @param params
	 * @return
	 */
	private static String buildReqUrl(String requestUri,String params){
		String retuUrl = requestUri;
		if(params.length() > 0){
			if(requestUri.indexOf("?") > 0){//已经存在参数
				retuUrl += "&" + params.toString();
			}else{
				retuUrl += "?" + params;
			}
		}
		return retuUrl;
	}


	/**
	 * 解析HTTP的Multipart格式的响应
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String processMultipartResponse(HttpResponse response) throws Exception {
		StringBuffer buffer = new StringBuffer();

		HttpEntity entity = response.getEntity();
		BufferedReader is = new BufferedReader(new InputStreamReader(entity.getContent(),DEFAULT_CHARSET));
		String str = null;
		while((str = is.readLine()) != null){
			buffer.append(str);
		}
		return buffer.toString();
	}

	/**
	 * 解析类型是Multipart请求数据
	 * @param request
	 * @return
	 */
	public static Map<String,String> analyzeRequestMultipart(HttpServletRequest request) {
		Map<String,String> params = null;
		try{
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);
			params = new HashMap<String,String>();
			for(FileItem fileItem:items){
				if (fileItem.isFormField()) {
					params.put(fileItem.getFieldName(), fileItem.getString(DEFAULT_CHARSET));//如果你页面编码是utf-8的
				}
			}
		}catch(Exception e){
			log.error("[http] 解析Request出错", e);
		}
		return params;
	}

	/**
	 * 设置cookie
	 * @param param
	 * @param value
	 */
	public static void setCookieValue(HttpServletRequest request, HttpServletResponse response, String param, String value) {
		setCookieValue(request, response, param, value, 24 * 60 * 60 * 30);
	}
	/**
	 * 设置cookie
	 * @param request
	 * @param response
	 * @param param
	 * @param value
	 * @param cookieMaxAge
	 */
	public static void setCookieValue(HttpServletRequest request, HttpServletResponse response, String param, String value, int cookieMaxAge) {
		String host = request.getServerName();
		setCookieValue(response, param, value, host, cookieMaxAge);
	}
	public static void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue, String cookieDomain, int cookieMaxAge)
	{
		setCookieValue(response, cookieName, cookieValue, cookieDomain, cookieMaxAge, true);
	}
	/**
	 * 设置cookie
	 * @param response
	 * @param cookieName	cookie名称
	 * @param cookieValue	cookie值
	 * @param cookieDomain	cookie有效的域名
	 * @param cookieMaxAge	cookie的有效期，单位：秒，大于等于0：则使用时间有效期，否则与session有效期相同
	 * @param httpOnly		cookie是否允许页面读取
	 *
	 */
	public static void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue, String cookieDomain, int cookieMaxAge, boolean httpOnly)
	{
		try {
			Cookie cookie = new Cookie(cookieName, URLEncoder.encode(cookieValue,DEFAULT_CHARSET));
			cookie.setPath("/");// cookie有效路径是网站根目录
			cookie.setDomain(cookieDomain);
			if(cookieMaxAge >= 0)
			{
				cookie.setMaxAge(cookieMaxAge);
			}
			cookie.setHttpOnly(httpOnly);
			response.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
			log.info("[http] 设置Cookie异常-->" + e.getMessage());
		}
	}

	/**
	 * 获取coolie
	 * @param param
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String param) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (param.equals(cookie.getName())) {
				try {
					return URLDecoder.decode(cookie.getValue(), DEFAULT_CHARSET);
				} catch (UnsupportedEncodingException e) {
					log.info("[http] 转换Cookie值异常-->" + e.getMessage());
				}
			}
		}
		return null;
	}

	/**
	 * 删除cookie
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
		String host = request.getServerName();
		Cookie[] cs = request.getCookies();
		if (cs != null && cs.length > 0) {
			for (int i = 0; i < cs.length; i++) {
				Cookie cookie = cs[i];
				Cookie cookies = new Cookie(cookie.getName(), null);
				cookies.setMaxAge(0);
				cookies.setPath("/");
				cookies.setDomain(host);
				response.addCookie(cookies);
			}
		}
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		String host = request.getServerName();
		Cookie[] cs = request.getCookies();
		if (cs != null && cs.length > 0) {
			for (int i = 0; i < cs.length; i++) {
				Cookie cookie = cs[i];
				if(name.equals(cookie.getName())) {
					Cookie cookienew = new Cookie(cookie.getName(), null);
					cookienew.setMaxAge(0);
					cookienew.setPath("/");
					cookienew.setDomain(host);
					response.addCookie(cookienew);
				}
			}
		}
	}

	/**
	 * 获取发送请求的源IP地址
	 * @param request
	 * @return
	 */
	public static String getRequestIP(HttpServletRequest request)
	{
		try
		{
			String ip = request.getHeader("x-forwarded-for");
			if(StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip))
			{
				//多次反向代理后会有多个ip值，第一个ip才是真实ip
				int index = ip.indexOf(",");
				if (index != -1)
				{
					return ip.substring(0, index);
				}
				else
				{
					return ip;
				}
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getRemoteAddr();
			}
			return ip;
		}
		catch(Throwable e)
		{
			log.info("[http] 获取请求IP地址异常-->" + e.getMessage());
			return "";
		}
	}
	/**
	 * 是否是ajax提交请求
	 * @param request
	 * @return
	 */
	public static boolean isAjaxSubmit(HttpServletRequest request)
	{
		try
		{
			String accept = request.getHeaders("Accept").nextElement().toString();
			if(accept.indexOf("application/json") != -1)
			{
				return true;
			}
		}
		catch(Exception e){
			log.info("[http] 请求类型识别(普通/Ajax)异常-->" + e.getMessage());
		}
		return false;
	}

	/**
	 * 执行微信信用租借HTTP请求
	 *
	 * @param certUrl 证书路径
	 * @param mchId 商户id
	 * @param url url地址
	 * @param requestStr  请求参数
	 * @return java.lang.String
	 */
	public static String httpWxRentbillRequest(String certUrl, String mchId, String url, String requestStr) throws Exception{
		CloseableHttpClient httpclient = null;
		String respXmlStr = "";
		try {
			//证书
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			//退款证书的路径
			FileInputStream instream = new FileInputStream(new File(certUrl));
			try {
				keyStore.load(instream, mchId.toCharArray());
			} finally {
				instream.close();
			}
			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
			SSLConnectionSocketFactory sslf = new SSLConnectionSocketFactory(sslcontext);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslf).build();
			HttpPost httpPost = new HttpPost(url);
			StringEntity reqEntity = new StringEntity(requestStr, DEFAULT_CHARSET);
			reqEntity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(reqEntity);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					respXmlStr += line;
				}
			}
		}
		finally{
			if(httpclient != null)
			{
				try
				{
					httpclient.close();
				}catch(Exception e){}
			}
		}
		return respXmlStr;
	}

	/**
	 * 执行Get请求
	 *
	 * @param requestUrl 请求地址
	 * @param params     参数
	 * @param headers    请求头
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl, Map<String, String> params, Map<String, String> headers) throws Exception {
		return doGet(requestUrl, params, DEFAULT_TIMEOUT, headers);
	}

	/**
	 * 执行Get请求
	 *
	 * @param requestUrl 请求地址
	 * @param params     参数
	 * @param timeout    请求超时间，单位：毫秒，0：表示不超时
	 * @param headers    请求头
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl, Map<String, String> params, int timeout, Map<String, String> headers) throws Exception {
		return doGet(requestUrl, params, null, DEFAULT_CHARSET, timeout, headers);
	}

	/**
	 * 执行Get请求
	 *
	 * @param requestUrl  请求地址
	 * @param params      参数
	 * @param contentType 请求内容类型
	 * @param charset     字符编码集
	 * @param timeout     请求超时间，单位：毫秒，0：表示不超时
	 * @param headers     请求头
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl, Map<String, String> params, String contentType, String charset, int timeout, Map<String, String> headers) throws Exception {
		String paramsStr = getParamsStr(params);
		requestUrl = buildReqUrl(requestUrl, paramsStr);
		return httpRequest(requestUrl, HTTP_GET, "", contentType, charset, timeout, headers);
	}


	/**
	 * 执行Post请求
	 *
	 * @param requestUrl 请求地址
	 * @param data       请求数据
	 * @param headers    请求头
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, String data, Map<String, String> headers) throws Exception {
		return doPost(requestUrl, data, DEFAULT_TIMEOUT, headers);
	}

	/**
	 * 执行Post请求
	 *
	 * @param requestUrl 请求地址
	 * @param data       请求数据
	 * @param timeout    请求超时间，单位：毫秒，0：表示不超时
	 * @param headers    请求头
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, String data, int timeout, Map<String, String> headers) throws Exception {
		return doPost(requestUrl, data, DEFAULT_CONTENTTYPE, DEFAULT_CHARSET, timeout, headers);
	}

	/**
	 * 执行Post请求
	 *
	 * @param requestUrl  请求地址
	 * @param data        请求数据
	 * @param contentType 请求内容类型
	 * @param charset     字符编码集
	 * @param timeout     请求超时间，单位：毫秒，0：表示不超时
	 * @param headers     请求头
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl, String data, String contentType, String charset, int timeout, Map<String, String> headers) throws Exception {
		return httpRequest(requestUrl, HTTP_POST, data, contentType, charset, timeout, headers);
	}

	/**
	 * 执行HTTP请求
	 *
	 * @param requestUrl    请求地址
	 * @param requestMethod 请求类型（POST、GET）
	 * @param data          请求数据
	 * @param contentType   请求内容类型
	 * @param charset       字符编码集
	 * @param timeout       请求超时间，单位：毫秒，0：表示不超时
	 * @param headers       请求头
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod, String data, String contentType, String charset, int timeout, Map<String, String> headers) throws Exception {
		CloseableHttpClient httpClient = null;
		try {
			if (StringUtils.isEmpty(charset)) {
				charset = DEFAULT_CHARSET;
			}
			HttpClientBuilder httpBuilder = HttpClientBuilder.create();
			httpClient = httpBuilder.build();
			RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(timeout)//设置连接超时时间，单位毫秒
				.setConnectionRequestTimeout(1000)//设置从connect Manager获取Connection 超时时间，单位毫秒
				.setSocketTimeout(timeout).build();//请求获取数据的超时时间，单位毫秒
			HttpUriRequest request = null;
			if (HTTP_POST.equalsIgnoreCase(requestMethod)) {
				HttpPost post = new HttpPost(requestUrl);
				post.setConfig(requestConfig);
				if (data != null && data.trim().length() > 0) {
					post.setEntity(new StringEntity(data, charset));
				}
				request = post;
			} else if (HTTP_PUT.equalsIgnoreCase(requestMethod)) {
				HttpPut put = new HttpPut(requestUrl);
				put.setConfig(requestConfig);
				if (data != null && data.trim().length() > 0) {
					put.setEntity(new StringEntity(data, charset));
				}
				request = put;
			} else {
				if (data != null && data.trim().length() > 0) {
					requestUrl = buildReqUrl(requestUrl, data);
				}
				HttpGet get = new HttpGet(requestUrl);
				get.setConfig(requestConfig);
				request = get;
			}
			if (StringUtils.isNotEmpty(contentType)) {
				request.addHeader(HTTP.CONTENT_TYPE, ContentType.create(contentType, charset).toString());
			}
			// 迭代遍历添加请求头
			for (Map.Entry<String, String> info : headers.entrySet()) {
				request.addHeader(info.getKey(), info.getValue());
				log.info("[http] 添加的请求头信息为:" + info.getKey() + " " + info.getValue());
			}
			log.info("[http] 请求开始，请求url:" + requestUrl);
			HttpResponse httpResponse = httpClient.execute(request);
			//获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			//响应状态
			log.debug("[http] status:" + httpResponse.getStatusLine());
			//判断响应实体是否为空
			if (entity != null) {
				String content = EntityUtils.toString(entity);
				log.info("[http] response content:" + content);
				return content;
			}
			return null;
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}


	/**
	 * 执行Put请求
	 *
	 * @param requestUrl 请求地址
	 * @param data       请求数据
	 * @param headers    请求头
	 * @return
	 * @throws Exception
	 */
	public static String doPut(String requestUrl, String data, Map<String, String> headers) throws Exception {
		return doPut(requestUrl, data, DEFAULT_TIMEOUT, headers);
	}

	/**
	 * 执行Put请求
	 *
	 * @param requestUrl 请求地址
	 * @param data       请求数据
	 * @param timeout    请求超时间，单位：毫秒，0：表示不超时
	 * @param headers    请求头
	 * @return
	 * @throws Exception
	 */
	public static String doPut(String requestUrl, String data, int timeout, Map<String, String> headers) throws Exception {
		return doPut(requestUrl, data, DEFAULT_CONTENTTYPE, DEFAULT_CHARSET, timeout, headers);
	}

	/**
	 * 执行Put请求
	 *
	 * @param requestUrl  请求地址
	 * @param data        请求数据
	 * @param contentType 请求内容类型
	 * @param charset     字符编码集
	 * @param timeout     请求超时间，单位：毫秒，0：表示不超时
	 * @param headers     请求头
	 * @return
	 * @throws Exception
	 */
	public static String doPut(String requestUrl, String data, String contentType, String charset, int timeout, Map<String, String> headers) throws Exception {
		return httpRequest(requestUrl, HTTP_PUT, data, contentType, charset, timeout, headers);
	}

}
