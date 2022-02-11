package org.springblade.uav.yunhe;

import lombok.Data;

/**
 * @author yiqimin
 * @create 2021/01/08
 */
@Data
public class MessageContent {

	private int type; // 1:int,2:long,3:char,4:short

	private Object content;

	public MessageContent(int type, Object content) {
		this.type = type;
		this.content = content;
	}

}
