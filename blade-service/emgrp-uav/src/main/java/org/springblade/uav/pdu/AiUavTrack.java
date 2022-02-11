package org.springblade.uav.pdu;

import lombok.Data;

/**
 * Ai下发无人机信息
 */
@Data
public class AiUavTrack {
	/**无人机编号 */
	private String uavCode;
	/** 相机焦距*/
	private String cameralFocalLength;
	/**x轴单位像素长度mm/pix */
	private String pixLengthX;
	/**y轴单位像素长度mm/pix */
	private String pixLengthY;
	/**俯仰角 */
	private String cameralPitchAngle;
	/**偏航角 */
	private String cameralYawAngle;
	/** 滚动角*/
	private String cameralRollingAngle;
	/**无人机距地面高度 */
	private String uavHeight;
	/** 经度*/
	private String uavLongitude;
	/**纬度 */
	private String uavLatitude;
}
