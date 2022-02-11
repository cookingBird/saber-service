package org.springblade.person.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: DuHongBo
 * @Date: 2021/1/21 15:46
 */
@Data
public class StatPersonnelTotle implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 区县
	 */
	private String area;
	/**
	 * 乡镇
	 */
	private String town;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 人数
	 */
	private Integer num;
	/**
	 * 经度
	 */
	private Double longitude;
	/**
	 * 纬度
	 */
	private Double latitude;
}
