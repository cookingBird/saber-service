package org.springblade.person.util;

/**
 * 工具类
 */
public class UtilsTool {
	private static double EARTH_RADIUS = 6378138.0;
	//基站信息保存(全部基站信息) redis
	public static final String BASE_STATION="emgrp:person:station:%s";
	//public static final String PERSON_CACHE="emgrp:person:";
	public static final String BASE_CONFIG="emgrp:person:config:";
	/**控制面数据时间后*/
	public static final String BASE_CONTROL_AFTER="emgrp:person:control:after:";



	/**用户面信息保存时间后*/
	public static final String BASE_PERSON_AFTER="emgrp:person:person:after:";

    /**援灾人员数据分析*/
	public static final String IN_ESCAPE_DANGER="emgrp:person:person:inescapedanger:";


	/**援宅人员 原住地分析*/
	public static final String IN_ESCAPE_DANGER_COUNT="emgrp:count:inescapedanger:";


    /**事故涉及人员redis*/
	public static final String BASE_ACCIDENT="emgrp:person:accident:";
	/**
	 * 脱险人员
	 */
	public static final String BASE_ACCIDENT_OUT="emgrp:person:out:";
	/**
	 * 安置地
	 */
	public static final String ESCAPE_PLACE="安置地";
	/**
	 * 迁入地
	 */
	public static final String ESCAPE_MIGRATE="迁入地";

	/**
	 * 高德地图KEY
	 */
	public static final String KEY = "bb95f00a5339041490010fa7246f31cb";

	/**
	 * 援灾乡镇
	 */
	public static final String AID_TOWN="援灾乡镇";

	/**
	 * 中讯演示专用taskID
	 */
	public static final String showTaskId = "1326782589129723906";

	/**安置地*/
	public static final String LARGE_TOWN="emgrp:person:largetown:";
	/**迁入地*/
	public static final String SMALL_TOWN="emgrp:person:smalltown:";
	/**
	 *
	 * @param yLat 圆纬度
	 * @param yLng 圆经度
	 * @param raduis 圆半径
	 * @param lat  纬度
	 * @param lng  经度
	 * @return
	 */
	public static boolean isInCircle(double yLng,double yLat,double raduis,double lng,double lat){
		boolean falg = false;
		double R = EARTH_RADIUS;
		double dLat = (yLat - lat) * Math.PI / 180;
		double dLng = (yLng - lng) * Math.PI / 180;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat * Math.PI / 180) * Math.cos(yLat * Math.PI / 180) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		double dis = Math.round(d);
		//System.out.println(dis);

		if (dis <= raduis){
			//点在圆内
			falg =true;
		}else {
			//点不在圆内
			falg =false;
		}
		return falg;
	}
	//public static List<>



	/*public static void main(String[] args) {
		//System.out.println("方法二"+isInCircle(2,104.072811, 30.663383,104.072748,30.66221));
		System.out.println("方法一"+isInCircle(104.072811,30.663383,135,104.072748,30.66221));

	}*/
}
