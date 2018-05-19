/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.newlandframework.rpc.serialize.hessian;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MAX_TOTAL;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MIN_IDLE;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MAX_WAIT_MILLIS;
import static com.newlandframework.rpc.core.RpcSystemConfig.SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS;

/**
 * Hessian序列化/反序列化池
 */
public class HessianSerializePool {

	// Netty采用Hessian序列化/反序列化的时候，为了避免重复产生对象，提高JVM内存利用率，故引入对象池技术，经过测试
	// 遇到高并发序列化/反序列化的场景的时候，序列化效率明显提升不少。
	private GenericObjectPool<HessianSerialize> hessianPool;
	private static volatile HessianSerializePool poolFactory = null;

	private HessianSerializePool() {
		hessianPool = new GenericObjectPool<HessianSerialize>(new HessianSerializeFactory());
	}

	public static HessianSerializePool getHessianPoolInstance() {
		if (poolFactory == null) {
			synchronized (HessianSerializePool.class) {
				if (poolFactory == null) {
					poolFactory = new HessianSerializePool(SERIALIZE_POOL_MAX_TOTAL, SERIALIZE_POOL_MIN_IDLE,
							SERIALIZE_POOL_MAX_WAIT_MILLIS, SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS);
				}
			}
		}
		return poolFactory;
	}

	// 预留接口，后续可以通过Spring Property Placeholder依赖注入
	public HessianSerializePool(final int maxTotal, final int minIdle, final long maxWaitMillis,
			final long minEvictableIdleTimeMillis) {
		hessianPool = new GenericObjectPool<HessianSerialize>(new HessianSerializeFactory());

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		// 最大池对象总数
		config.setMaxTotal(maxTotal);
		// 最小空闲数
		config.setMinIdle(minIdle);
		// 最大等待时间， 默认的值为-1，表示无限等待
		config.setMaxWaitMillis(maxWaitMillis);
		// 退出连接的最小空闲时间 默认1800000毫秒
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

		hessianPool.setConfig(config);
	}

	public HessianSerialize borrow() {
		try {
			return getHessianPool().borrowObject();
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void restore(final HessianSerialize object) {
		getHessianPool().returnObject(object);
	}

	public GenericObjectPool<HessianSerialize> getHessianPool() {
		return hessianPool;
	}
}
