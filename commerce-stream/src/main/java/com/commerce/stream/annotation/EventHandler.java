package com.commerce.stream.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.core.annotation.AliasFor;

@SuppressWarnings("deprecation")
@StreamListener
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EventHandler {

	@AliasFor(annotation = StreamListener.class, attribute = "condition")
	String value() default "";

	@AliasFor(annotation = StreamListener.class, attribute = "target")
	String target() default "";

	@AliasFor(annotation = StreamListener.class, attribute = "condition")
	String eventType() default "";	
}