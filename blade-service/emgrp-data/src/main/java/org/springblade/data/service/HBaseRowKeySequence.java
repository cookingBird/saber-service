package org.springblade.data.service;

import org.apache.hadoop.hbase.util.Bytes;

import java.util.Random;

/**
 * @author yiqimin
 * @create 2020/06/03
 */
public class HBaseRowKeySequence {

	public static byte[] getRowKey(Long datetime) {
		datetime = datetime / 1000;
		int i = datetime.intValue();
		byte[] time = Bytes.toBytes(i);

		byte[] b = new byte[4];
		Random random = new Random();
		random.nextBytes(b);

		byte[] rowKey = new byte[8];
		System.arraycopy(time, 0, rowKey, 0, time.length);
		System.arraycopy(b, 0, rowKey, 4, b.length);
		return rowKey;
	}

	public static byte[] getRowKey(Long id, byte[] sourceRowKey) {
		byte[] head = Bytes.toBytes(id);

		byte[] rowKey = new byte[16];
		System.arraycopy(head, 0, rowKey, 0, head.length);
		System.arraycopy(sourceRowKey, 0, rowKey, 8, sourceRowKey.length);
		return rowKey;
	}

	public static byte[] getStartRowKey(Long id) {
		byte[] top = Bytes.toBytes(id);

		byte[] end = Bytes.toBytes(0l);

		byte[] rowKey = new byte[16];
		System.arraycopy(top, 0, rowKey, 0, top.length);
		System.arraycopy(end, 0, rowKey, 8, end.length);
		return rowKey;
	}

	public static byte[] getStopRowKey(Long id) {
		byte[] top = Bytes.toBytes(id);

		byte[] end = Bytes.toBytes(Long.MAX_VALUE);

		byte[] rowKey = new byte[16];
		System.arraycopy(top, 0, rowKey, 0, top.length);
		System.arraycopy(end, 0, rowKey, 8, end.length);
		return rowKey;
	}


	public static byte[] getStartRowKeyDatetime(Long datetime) {
		datetime = datetime / 1000;
		int i = datetime.intValue();
		byte[] top = Bytes.toBytes(i);

		byte[] end = Bytes.toBytes(0);

		byte[] rowKey = new byte[8];
		System.arraycopy(top, 0, rowKey, 0, top.length);
		System.arraycopy(end, 0, rowKey, 4, end.length);
		return rowKey;
	}

	public static byte[] getStopRowKeyDatetime(Long datetime) {
		datetime = datetime / 1000;
		int i = datetime.intValue();
		byte[] top = Bytes.toBytes(i);

		byte[] end = Bytes.toBytes(Integer.MAX_VALUE);

		byte[] rowKey = new byte[8];
		System.arraycopy(top, 0, rowKey, 0, top.length);
		System.arraycopy(end, 0, rowKey, 4, end.length);
		return rowKey;
	}
}
