package org.springblade.person.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 援灾人员出现次数
 */
@Data
public class InEscpDangStation implements Serializable {

	private String LACorTAC;

	private String CIorECI;
    /** 经度*/
	private BigDecimal longitude;
	/** 纬度*/
	private BigDecimal latitude;
    /**次数*/
	private Integer count;
	/**基站名称*/
	private String lastStationName;

}
