/*
 * Copyright Ningbo Qishan Technology Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springblade.person.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 高德地图工具类
 *
 * @author DuHongBo
 */
@Slf4j
public class GaodeMapUtil {

	/**
	 * 高德地图秘钥
	 */
	private static final String KEY = "a855a9d74a183395f475f0fe9a4fbaab";

	/**
	 * 逆地理编码
	 */
	private static String REGEO_URL = "http://restapi.amap.com/v3/geocode/regeo";


	/**
	 * 经纬度获取地区
	 *
	 * @param locStr 经度纬度
	 * @param batch  batch=true为批量查询。batch=false为单点查询
	 * @return area
	 */
	public static List<HashMap<String, String>> getArea(String locStr, Boolean batch) {
		Map<String, Object> map = new HashMap<>();
		map.put("key", KEY);
		map.put("location", locStr.substring(0, locStr.length() - 1));
		map.put("batch", batch);
		String resultData = new String();
		List<HashMap<String, String>> resultList = new ArrayList<>();
		try {
			resultData = HttpUtil.get(REGEO_URL, map);
		}
		catch (Exception e) {
			log.error("高德地图连接异常");
		}
		JSONObject jsonObject = JSONObject.parseObject(resultData);
		if (!jsonObject.getString("status").equalsIgnoreCase("1")) {
			return null;
		}
		JSONArray jsonArray = jsonObject.getJSONArray("regeocodes");
		for (int i = 0; i < jsonArray.size(); i++) {
			HashMap<String, String> resultMap = new HashMap<>();
			JSONObject regJson = (JSONObject) jsonArray.get(i);
			JSONObject acJson = regJson.getJSONObject("addressComponent");
			resultMap.put("province", acJson.getString("province"));
			resultMap.put("city", acJson.getString("city"));
			resultMap.put("district", acJson.getString("district"));
			resultMap.put("town", acJson.getString("township"));
			JSONObject snJson = acJson.getJSONObject("streetNumber");
			resultMap.put("address", snJson.getString("street") + snJson.getString("number"));
			resultList.add(resultMap);
		}
		return resultList;
	}
}
