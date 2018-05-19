/**
 * Copyright (C) 2017 Newland Group Holding Limited
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
package com.newlandframework.rpc.services.impl;

import com.newlandframework.rpc.services.Cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * service impl
 */
public class CacheImpl implements Cache {
	private final Map<Object, Object> store;

	public CacheImpl() {
		final int max = 256;
		this.store = new LinkedHashMap<Object, Object>() {
			private static final long serialVersionUID = 8068502748474203310L;

			@Override
			protected boolean removeEldestEntry(Entry<Object, Object> eldest) {
				return size() > max;
			}
		};
	}

	@Override
	public void put(Object key, Object value) {
		store.put(key, value);
	}

	@Override
	public Object get(Object key) {
		return store.get(key);
	}
}
