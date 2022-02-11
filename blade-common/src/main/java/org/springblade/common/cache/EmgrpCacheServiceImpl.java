package org.springblade.common.cache;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EmgrpCacheServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IEmgrpCacheService<T> {
	private static final long DEFAULT_TIMEOUT = 60 * 60 * 24 * 7;
	private String DEFAULT_CACHE_NAME = entityClass.getName().replace(".", ":");
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Value("${emgrp.serviceCacheLog:true}")
	protected boolean cacheLog = false;
	@Autowired
	protected BladeRedis redis;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		boolean r = super.saveBatch(entityList, batchSize);
		if (r) {
			removeListCache();
			if (isExtCache()) {
				removeExtCaches(entityList);
			}
		}
		return r;
	}

	@Override
	public boolean save(T entity) {
		boolean r = super.save(entity);
		if (r) {
			removeListCache();
			if (isExtCache()) {
				removeExtCache(entity);
			}
		}
		return r;
	}

	@Override
	public boolean removeById(Serializable id) {
		T old = null;
		if (isExtCache()) {
			old = getById(id);
		}
		boolean r = super.removeById(id);
		if (r) {
			removeCache(id);
			if (isExtCache()) {
				removeExtCache(old);
			}
		}
		return r;
	}

	@Override
	public boolean removeByMap(Map<String, Object> columnMap) {
		List<T> list = getBaseMapper().selectByMap(columnMap);
		boolean r = super.removeByMap(columnMap);
		if (r) {
			removeCache(list.stream().map(e -> getIdVal(e)).collect(Collectors.toList()).toArray());
			if (isExtCache()) {
				removeExtCaches(list);
			}
		}
		return r;
	}

	@Override
	public boolean remove(Wrapper<T> queryWrapper) {
		List<T> list = getBaseMapper().selectList(queryWrapper);
		boolean r = super.remove(queryWrapper);
		if (r) {
			removeCache(list.stream().map(e -> getIdVal(e)).collect(Collectors.toList()).toArray());
			if (isExtCache()) {
				removeExtCaches(list);
			}
		}
		return r;
	}

	@Override
	public boolean removeByIds(Collection<? extends Serializable> idList) {
		List<T> list = null;
		if (isExtCache()) {
			list = getBaseMapper().selectBatchIds(idList);
		}
		boolean r = super.removeByIds(idList);
		if (r) {
			removeCache(idList.toArray());
			if (isExtCache()) {
				removeExtCaches(list);
			}
		}
		return r;
	}

	@Override
	public boolean updateById(T entity) {
		Serializable idVal = getIdVal(entity);
		T old = null;
		if (isExtCache()) {
			old = getById(idVal);
		}
		boolean r = super.updateById(entity);
		if (r) {
			removeCache(idVal);
			if (isExtCache()) {
				removeExtCache(old);
			}
		}
		return r;
	}

	@Override
	public boolean update(T entity, Wrapper<T> updateWrapper) {
		List<T> list = getBaseMapper().selectList(updateWrapper);
		boolean r = super.update(entity, updateWrapper);
		if (r) {
			removeCache(getIdVal(list).toArray());
			if (isExtCache()) {
				removeExtCaches(list);
			}
		}
		return r;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		for (T entity : entityList) {
			saveOrUpdate(entity);
		}
		return true;
	}

	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		List<Serializable> idList = getIdVal(entityList);
		List<T> oldList = null;
		if (isExtCache()) {
			oldList = getBaseMapper().selectBatchIds(idList);
		}
		boolean r = super.updateBatchById(entityList, batchSize);
		if (r) {
			removeCache(idList.toArray());
			if (isExtCache()) {
				removeExtCaches(oldList);
			}
		}
		return true;
	}

	@Override
	public T getCache(Serializable id) {
		// 去缓存查询
		T t = deserializationVal(redis.get(buildCacheKey(id)));
		if (null != t) {
			printLog("====命中缓存 -> key:{}, val:{}", id, t);
			return t;
		}
		printLog(">>>>未命中缓存 -> key:{}", id);
		// 缓存没有，就查询数据库
		t = this.getById(id);
		if (null != t) {
			// 加载到缓存
			putCache(id, t);
		}
		return t;
	}

	@Override
	public List<T> getCacheList() {
		return getExtCacheList(getListCacheKey(), () -> {
			QueryWrapper<T> queryWrapper =  new QueryWrapper<>();
			queryWrapper.select("id");
			List<T> list =  getBaseMapper().selectList(queryWrapper);
			return list.stream().map(e -> getIdVal(e)).collect(Collectors.toList());
		});
	}

	// TODO 批量处理
	@Override
	public List<T> getCacheList(Collection<Object> ids) {
		if (CollectionUtil.isEmpty(ids)) return new ArrayList<>();
		List<T> list = new ArrayList<>(ids.size());
		for (Object id : ids) {
			T t = getCache((Serializable) id);
			if (null != t) {
				list.add(t);
			}
		}
		return list;
	}


	protected T getExtCache(String key, Supplier<Object> supplier) {
		return getExtCache(key, getCacheTimeoutSecond(), supplier);
	}

	protected T getExtCache(String key, long timeOut, Supplier<Object> supplier) {
		if (!isExtCache()) throw new RuntimeException("子类不支持扩展缓存");
		Object object = redis.get(key);
		if (null != object) {
			printLog("====命中Ext缓存 -> key:{}, val:{}", key, object);
			if (isId(object)) {
				return getCache((Serializable) object);
			}
			return deserializationVal(object);
		}
		object = supplier.get();
		if (null == object) {
			return null;
		}
		printLog("++++加载Ext缓存 -> key:{}, val:{}", key, object);
		if (isId(object)) {
			redis.setEx(key, object, timeOut);
			return getCache((Serializable) object);
		} else {
			redis.setEx(key, serializeVal(object), timeOut);
			return (T)object;
		}
	}

	protected List<T> getExtCacheList(String key, Supplier<List<Object>> supplier) {
		return getExtCacheList(key, getCacheTimeoutSecond(), supplier);
	}

	protected List<T> getExtCacheList(String key, long timeOut, Supplier<List<Object>> supplier) {
		if (!isExtCache()) throw new RuntimeException("子类不支持扩展缓存");
		Set<Object> cacheSet = redis.sMembers(key);
		if (CollectionUtil.isNotEmpty(cacheSet)) {
			printLog("====命中Ext缓存 -> key:{}, val:{}", key, cacheSet);
			// 如果返回的是ID集合，就去主存再查一下。
			if (isIdSet(cacheSet)) {
				return getCacheList(cacheSet);
			}
			return deserializationListVal(cacheSet);
		}
		// 加载
		List<Object> objects = supplier.get();
		if (CollectionUtil.isEmpty(objects)) {
			return new ArrayList<>();
		}
		boolean isIdSet = isIdSet(objects);
		if (isIdSet) {
			// 添加到缓存
			redis.sAdd(key, objects.toArray());
		} else {
			// 序列化后添加
			redis.sAdd(key, serializeListVal(objects).toArray());
		}

		redis.expire(key, timeOut);
		printLog("++++加载Ext缓存 -> key:{}, val:{}", key, objects);
		// 如果返回的是ID集合，就去主存再查一下。
		if (isIdSet) {
			return getCacheList(objects);
		}
		return (List<T>) objects;
	}


	protected void removeCache(Object... ids) {
		if (null == ids || ids.length == 0) return;
		redis.del(Stream.of(ids).map(e -> buildCacheKey(e.toString())).collect(Collectors.toList()).toArray(new String[ids.length]));
		removeListCache();
		printLog("----删除缓存 -> key:{}", Arrays.toString(ids));
	}

	protected void removeListCache() {
		redis.del(getListCacheKey());
	}

	protected void removeExtCaches(Collection<T> list) {
		if (CollectionUtil.isEmpty(list)) return;
		List<String> allKeys = Lists.newArrayList();
		for (T t : list) {
			List<String> keys = getRemoveExtKeys(t);
			if (CollectionUtil.isNotEmpty(keys)) {
				allKeys.addAll(keys);
			}
		}
		if (!allKeys.isEmpty()) {
			redis.del(allKeys);
			printLog("----删除Ext缓存 -> key:{}", allKeys);
			for (T t : list) {
				afterRemoveExtCahce(t);
			}
		}

	}

	protected void removeExtCache(T t) {
		if (null == t) return;
		List<String> extkeys = getRemoveExtKeys(t);
		if (CollectionUtil.isNotEmpty(extkeys)) {
			redis.del(extkeys);
			printLog("----删除Ext缓存 -> key:{}", extkeys);
			afterRemoveExtCahce(t);
		}

	}

	protected List<String> getRemoveExtKeys(T t) {
		return null;
	}

	protected long getCacheTimeoutSecond() {
		return DEFAULT_TIMEOUT;
	}

	protected abstract boolean isExtCache();

	protected void afterRemoveExtCahce(T t) {

	}

	private void putCache(Serializable id, T t) {
		redis.setEx(buildCacheKey(id), serializeVal(t), getCacheTimeoutSecond());
		printLog("++++put缓存 -> key:{}, val:{}", id, t);
	}

	protected String getCacheName() {
		return DEFAULT_CACHE_NAME;
	}

	private T deserializationVal(Object cacheVal) {
		if (Objects.isNull(cacheVal)) return null;
		return (T) JSON.parseObject(cacheVal.toString(), entityClass);
	}

	private List<T> deserializationListVal(Set<Object> cacheSet) {
		List<T> list = new ArrayList<>(cacheSet.size());
		for (Object jsonStr : cacheSet) {
			list.add(deserializationVal(jsonStr));
		}
		return list;
	}

	private List<String> serializeListVal(List<Object> list) {
		return list.stream().map(e -> serializeVal(e)).collect(Collectors.toList());
	}

	private String serializeVal(Object val) {
		return JSON.toJSONString(val);
	}

	private boolean isIdSet(Collection<Object> objects) {
		for (Object object : objects)
			return isId(object);
		return true;
	}

	private boolean isId(Object object) {
		return object instanceof Long;
	}

	private String buildCacheKey(Serializable id) {
		return getCacheName() + ":" + id;
	}

	private String getListCacheKey() {
		return getCacheName() + ":alllist";
	}

	private Serializable getIdVal(T entity) {
		Class<?> cls = entity.getClass();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
		Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
		String keyProperty = tableInfo.getKeyProperty();
		Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
		Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
		return (Serializable) idVal;
	}

	private List<Serializable> getIdVal(Collection<T> list) {
		List<Serializable> ids = new ArrayList<>(list.size());
		for (T t : list) {
			ids.add(getIdVal(t));
		}
		return ids;
	}

	protected void printLog(String msg, Object... param) {
		if (cacheLog) {
			logger.info(msg, param);
		}
	}

}
