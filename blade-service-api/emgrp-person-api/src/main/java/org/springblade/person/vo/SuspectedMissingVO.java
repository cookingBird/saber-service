package org.springblade.person.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: DuHongBo
 * @Date: 2020/12/4 17:12
 */
@Data

@ApiModel(value = "SuspectedMissingVO对象", description = "SuspectedMissingVO")
public class SuspectedMissingVO {
	private static final long serialVersionUID = 1L;
	private BigDecimal longitude;
	private BigDecimal latitude ;
	private Long peopleNum;

}
