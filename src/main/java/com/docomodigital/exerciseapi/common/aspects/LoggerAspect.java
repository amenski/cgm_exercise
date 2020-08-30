package com.docomodigital.exerciseapi.common.aspects;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.docomodigital.exerciseapi.common.annotations.Loggable;
import com.docomodigital.exerciseapi.common.utils.ApiConstants;


/**
 * Implements how to log methods annotated as {@code Loggable}
 * 
 * @author Amanuel
 *
 */

@Aspect
@Component
public class LoggerAspect {

	Logger logger = LoggerFactory.getLogger(LoggerAspect.class);
	
	@Autowired
	private Environment enviroment;
	
	@Around("@annotation(com.docomodigital.exerciseapi.common.annotations.Loggable)") 
	public Object loggerInterceptor(ProceedingJoinPoint jointPoint) throws Throwable{
		List<String> arguments = new ArrayList<>(); 
		String declaringMethod = LoggerAspect.class.getSimpleName();
		Object proceed = null;
		try{
			MethodSignature methodSignature = (MethodSignature) jointPoint.getSignature();
			Method method = methodSignature.getMethod();
			declaringMethod = methodSignature.getDeclaringType().getSimpleName() + "." + method.getName() + "()";
			
			//argument name and value
			String[] argumentNames = methodSignature.getParameterNames();
			Object[] methodArguments = jointPoint.getArgs();
			
			//get exclusions
			Loggable loggable = method.getAnnotation(Loggable.class);
			String[] exclusions = loggable.exclusions();
			
			//add name and value of each parameter
			for(int i=0; i < methodArguments.length; i++){
				if(Arrays.asList(exclusions).contains(argumentNames[i])){
					arguments.add(argumentNames[i] + " = " + "****");
					continue;
				}
				arguments.add(argumentNames[i] + " = " + methodArguments[i]);
			}
			
			//log
			log(declaringMethod, LogLevel.INFO, loggable.format(), ApiConstants.METHOD_START);
			log(declaringMethod, LogLevel.INFO, loggable.format(), arguments.toString());
			
			proceed = jointPoint.proceed();
		}catch (Exception e) {
			if(isDevEnv()) {
				log(declaringMethod,  LogLevel.ERROR, "{} {}", String.valueOf(e));
			}
			log(declaringMethod,  LogLevel.ERROR, "{} {}",  e.getMessage());
			throw e; //should not swallow the thrown exception
		}finally {
			log(declaringMethod,  LogLevel.INFO, "{} {}", ApiConstants.METHOD_END);
		}
		return proceed;
	}
	
	public void log(String declaringMethod, LogLevel level,String format, String message) {
		
		switch (level) {
			case INFO:
				logger.info(format, declaringMethod, message);
				break;
			case DEBUG:
				logger.debug(format, declaringMethod, message);
				break;
			case WARN:
				logger.warn(format, declaringMethod, message);
				break;
			case ERROR:
				logger.error(format, declaringMethod, message);
				break;
			default:
				break;
		}
	}
	
	private boolean isDevEnv() {
		for(String env : enviroment.getActiveProfiles()) {
			if(StringUtils.equalsAny(env, "dev", "development")) 
				return true;
		}
		
		return false;
	}
}
