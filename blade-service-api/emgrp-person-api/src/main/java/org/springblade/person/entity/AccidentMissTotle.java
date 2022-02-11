package org.springblade.person.entity;

/**
 * 失联人员统计数据
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 疑似失联人员统计数据
 */
@Data
public class AccidentMissTotle implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 失联人员总数量
	 */
	private int counts;
    /**男数量*/
	private int manNum;
	/**女数量*/
	private int womanNum;
    /**60岁以上人员数量*/
	private int oldMan;


}
