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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.w3c.dom.Element;

/**
 * Spring的自定义标签的实现 parser实现
 */
public class NettyRpcServiceParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String interfaceName = element.getAttribute("interfaceName");
		String ref = element.getAttribute("ref");
		String filter = element.getAttribute("filter");

		// 代表一个从配置源（XML，Java Config等）中生成的BeanDefinition
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(NettyRpcService.class);
		beanDefinition.setLazyInit(false);
		beanDefinition.getPropertyValues().addPropertyValue("interfaceName", interfaceName);
		beanDefinition.getPropertyValues().addPropertyValue("ref", ref);
		beanDefinition.getPropertyValues().addPropertyValue("filter", filter);

		// BeanDefinitionRegistry的作用主要是向注册表中注册 BeanDefinition 实例，完成 注册的过程
		parserContext.getRegistry().registerBeanDefinition(interfaceName, beanDefinition);

		return beanDefinition;
	}
}
