package com.newlandframework.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.newlandframework.rpc.services.AddCalculate;

public class SingleCalcTest {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:rpc-invoke-config-client.xml");
		int add = ((AddCalculate) context.getBean("addCalc")).add(1, 2);
		System.out.println("calc add result:[" + add + "]");
	}
}
