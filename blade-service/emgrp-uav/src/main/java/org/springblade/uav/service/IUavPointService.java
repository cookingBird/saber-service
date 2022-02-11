package org.springblade.uav.service;

import org.springblade.uav.entity.UavDevinfo;
import org.springblade.uav.entity.UavPoint;
import org.springblade.uav.server.DataRequest;

import java.util.List;

/**
 * 轨迹业务接口
 *
 * @author pengziyuan
 */
public interface IUavPointService {

    /**
     * 保存轨迹数据
     *
     * @param id
     * @param data
     */
    void save(long id, DataRequest data);

    /**
     * 查询轨迹集合
     *
     * @param id        无人机ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @throws Exception
     */
    public List<UavPoint> list(long id, String startTime, String endTime) throws Exception;

    /**
     * 获取最新的一条轨迹数据
     *
     * @param id 无人机ID
     * @return
     * @throws Exception
     */
    UavPoint getNew(long id) throws Exception;

	/**
	 * 发送无人机数据到AI
	 * @param uav
	 * @param data
	 */
	void sendToAi(UavDevinfo uav, DataRequest data);
}
