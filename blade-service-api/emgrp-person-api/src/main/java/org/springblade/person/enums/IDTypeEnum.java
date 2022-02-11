package org.springblade.person.enums;

import java.util.HashMap;
import java.util.Map;

public enum IDTypeEnum {
	GZ_01("01AA","公众证件"),
	//被叫
	GZ_02("0101","居民身份证"),
	GZ_03("0102","户口簿"),
	GZ_04("0103","军人身份证"),
	GZ_05("0104","武装警察身份证"),
	GZ_06("0105","公港澳居民往来内地通行"),
	GZ_07("0106","台湾居民往来大陆通行证"),
	GZ_08("0107","护照"),
	GZ_09("0199","其他个人证件"),
	GZ_10("0108","临时居民身份证"),

	JT_01("02AA","集团证件"),
	JT_02("0201","营业执照原件"),
	JT_03("0202","营业执照副本原件或加盖公章的营业执照复印件"),
	JT_04("0203","组织机构代码证原件或加盖公章的组织机构代码证复印件"),
	JT_05("0204","事业单位法人证书"),
	JT_06("0299","集其他集团证件"),
	JT_07("0205","对于党政军（军队指团级以上单位）用户可使用加盖公章的介绍信"),
	JT_08("0206","集社会团体法人登记证书"),
	JT_09("0207","照会"),
	JT_10("0208","民办非企业单位登记证书（包括法人、合伙、个体）"),

	PT_01("01","身份证"),
	PT_02("02","户口本"),
	PT_03("03","暂住证"),
	PT_04("04","护照"),
	PT_05("05","驾驶证"),
	PT_06("06","营业执照"),
	PT_07("07","警官证"),
	PT_08("08","军人证"),
	PT_09("09","教师证"),
	PT_10("10","学生证"),
	PT_11("11","工作证"),
	PT_12("99","其它"),

		;

	private String value;
	private String name;
	IDTypeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}


	public String getValue() {
		return value;
	}


	public String getName() {
		return name;
	}

	public static Map<String,String> getEnumMap(){
		Map<String,String> resultMap = new HashMap<>();
		IDTypeEnum[] operTypeEnum =  IDTypeEnum.values();
		for (IDTypeEnum typeEnum : operTypeEnum) {
			resultMap.put(String.valueOf(typeEnum.value), typeEnum.name);
		}
		return resultMap;
	}
}
