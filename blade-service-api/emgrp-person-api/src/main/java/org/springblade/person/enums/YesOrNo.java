package org.springblade.person.enums;

import lombok.Getter;

/**
 * @author yiqimin
 * @create 2021/01/11
 */
@Getter
public enum YesOrNo {
	YES(1, "是"),
	NO(0, "否");

	private int value;
	private String name;

	YesOrNo(int value, String name) {
		this.value = value;
		this.name = name;
	}

}
