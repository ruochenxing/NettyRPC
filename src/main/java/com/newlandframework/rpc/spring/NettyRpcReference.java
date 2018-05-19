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

import com.google.common.eventbus.EventBus;
import com.newlandframework.rpc.event.ClientStopEvent;
import com.newlandframework.rpc.event.ClientStopEventListener;
import com.newlandframework.rpc.netty.MessageSendExecutor;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * nettyrpc:reference自定义标签实体类
 * 
 * 封装自己定制的实例化逻辑(例如你想用工厂模式来实例化，或者Class.getInstance())，然后让spring统一管理( Spirng提供的工厂Bean, 方便)
 */
public class NettyRpcReference implements FactoryBean<Object>, InitializingBean, DisposableBean {
	private String interfaceName;
	private String ipAddr;
	private String protocol;
	private EventBus eventBus = new EventBus();

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public void destroy() throws Exception {
		eventBus.post(new ClientStopEvent(0));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MessageSendExecutor.getInstance().setRpcServerLoader(ipAddr, RpcSerializeProtocol.valueOf(protocol));
		ClientStopEventListener listener = new ClientStopEventListener();
		eventBus.register(listener);
	}

	@Override
	public Object getObject() throws Exception {
		return MessageSendExecutor.getInstance().execute(getObjectType());
	}

	@Override
	public Class<?> getObjectType() {
		try {
			return this.getClass().getClassLoader().loadClass(interfaceName);
		} catch (ClassNotFoundException e) {
			System.err.println("spring analyze fail!");
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
