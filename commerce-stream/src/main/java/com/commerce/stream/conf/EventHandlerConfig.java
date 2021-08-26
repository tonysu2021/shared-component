package com.commerce.stream.conf;

import static org.springframework.cloud.stream.config.BindingServiceConfiguration.STREAM_LISTENER_ANNOTATION_BEAN_POST_PROCESSOR_NAME;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.StreamListenerAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

@SuppressWarnings("deprecation")
@Configuration
public class EventHandlerConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerConfig.class);

	/*
	 * The SpEL expression used to allow the Spring Cloud Stream Binder to dispatch
	 * to methods Annotated with @EventHandler
	 */

	private String eventHandlerSpelPattern = "headers['x_event_type']=='%s'";

	private String eventReplyHandlerSpelPattern = "headers['x_event_type']=='%s' and headers['x_server_id']=='%s'";

	private String matchWord = "-reply";
	
	/**
	 * Override the default {@link StreamListenerAnnotationBeanPostProcessor} to
	 * inject value of 'eventType' attribute into 'condition' expression.
	 * 
	 * @return
	 */
	@Bean(name = STREAM_LISTENER_ANNOTATION_BEAN_POST_PROCESSOR_NAME)
	public StreamListenerAnnotationBeanPostProcessor streamListenerAnnotationBeanPostProcessor(
			@Qualifier("serverInfo") ServerInfo serverInfo) {
		return new StreamListenerAnnotationBeanPostProcessor() {
			@Override
			protected StreamListener postProcessAnnotation(StreamListener originalAnnotation, Method annotatedMethod) {
				Map<String, Object> attributes = new HashMap<>(
						AnnotationUtils.getAnnotationAttributes(originalAnnotation));
				if (StringUtils.hasText(originalAnnotation.condition())) {
					String target = originalAnnotation.target();
					String spelExpression = target.contains(matchWord)
							? String.format(eventReplyHandlerSpelPattern, originalAnnotation.condition(), serverInfo.getServerId().toString())
							: String.format(eventHandlerSpelPattern, originalAnnotation.condition());
					attributes.put("condition", spelExpression);
					LOGGER.info("[EventHandler] target:{} , condition :{}", target, spelExpression);
				}
				return AnnotationUtils.synthesizeAnnotation(attributes, StreamListener.class, annotatedMethod);
			}
		};
	}
}
