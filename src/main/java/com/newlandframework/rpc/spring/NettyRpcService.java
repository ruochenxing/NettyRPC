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
package com.newlandframework.rpc.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.newlandframework.rpc.event.ServerStartEvent;
import com.newlandframework.rpc.filter.Filter;
import com.newlandframework.rpc.filter.ServiceFilterBinder;
import com.newlandframework.rpc.netty.MessageRecvExecutor;

/**
 * nettyrpc:service自定义标签实体类
 * 
 * ApplicationListener ?? 当容器初始化完成之后，需要处理一些操作，比如一些数据的加载、初始化缓存、特定任务的注册等等。
 * 这个时候我们就可以使用Spring提供的ApplicationListener来进行操作。
 * 
 * 事件监听器
 */
public class NettyRpcService implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {
	private String interfaceName;
	private String ref;
	private String filter;
	private ApplicationContext applicationContext;

	/***
	 * 对消息进行接受处理
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		System.out.println("onApplicationEvent \t" + interfaceName);
		ServiceFilterBinder binder = new ServiceFilterBinder();

		if (StringUtils.isBlank(filter) || !(applicationContext.getBean(filter) instanceof Filter)) {
			binder.setObject(applicationContext.getBean(ref));
		} else {
			binder.setObject(applicationContext.getBean(ref));
			binder.setFilter((Filter) applicationContext.getBean(filter));
		}

		MessageRecvExecutor.getInstance().getHandlerMap().put(interfaceName, binder);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("setApplicationContext publishEvent");
		this.applicationContext = applicationContext;
		// 事件发布
		applicationContext.publishEvent(new ServerStartEvent(new Object()));
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}
