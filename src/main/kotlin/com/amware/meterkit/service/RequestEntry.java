package com.amware.meterkit.service;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Documented;

/**
 * 用于标记请求入口的注解类。
 */
@Documented
public @interface RequestEntry {

	String value();

	RequestMethod method() default RequestMethod.GET;

}
