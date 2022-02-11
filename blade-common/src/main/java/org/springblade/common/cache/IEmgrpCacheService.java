package org.springblade.common.cache;

import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IEmgrpCacheService<T> extends IService<T> {
	T getCache(Serializable id);

	List<T> getCacheList();

	List<T> getCacheList(Collection<Object> ids);

}
