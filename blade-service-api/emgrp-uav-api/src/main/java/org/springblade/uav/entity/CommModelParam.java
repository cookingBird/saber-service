package org.springblade.uav.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName CommModelParam
 * @Description TODO
 * @Author wt
 * @Date 2020/10/13 9:28
 * @Version 1.0
 **/
@Data
@ApiModel(value = "CommModelParam对象", description = "通讯方式")
public class CommModelParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 通讯方式类型对应的值
	 */
	@ApiModelProperty(value = "通讯方式类型对应的值")
	private Integer value;

	/**
	 * 通讯方式
	 */
	@ApiModelProperty(value = "通讯方式")
	private String type;
}
