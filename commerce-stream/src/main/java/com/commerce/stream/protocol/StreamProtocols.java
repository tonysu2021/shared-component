package com.commerce.stream.protocol;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@SuppressWarnings("deprecation")
public interface StreamProtocols {

	@Input(StreamChannel.CUSTOMER_BROADCAST_INPUT)
	SubscribableChannel inboundCustomerBroadcast();

	@Output(StreamChannel.CUSTOMER_BROADCAST_OUTPUT)
	MessageChannel outboundCustomerBroadcast();
	
	@Input(StreamChannel.CUSTOMER_INPUT)
	SubscribableChannel inboundCustomer();

	@Output(StreamChannel.CUSTOMER_OUTPUT)
	MessageChannel outboundCustomer();
	
	@Input(StreamChannel.ORDER_INPUT)
	SubscribableChannel inboundOrder();

	@Output(StreamChannel.ORDER_OUTPUT)
	MessageChannel outboundOrder();
	
	@Input(StreamChannel.ORDER_REPLY_INPUT)
	SubscribableChannel inboundOrderReply();

	@Output(StreamChannel.ORDER_REPLY_OUTPUT)
	MessageChannel outboundOrderReply();
	
	@Input(StreamChannel.ORDER_PRIORITY_INPUT)
	SubscribableChannel inboundOrderPriority();

	@Output(StreamChannel.ORDER_PRIORITY_OUTPUT)
	MessageChannel outboundOrderPriority();
	
	@Input(StreamChannel.PRODUCT_INPUT)
	SubscribableChannel inboundProduct();

	@Output(StreamChannel.PRODUCT_OUTPUT)
	MessageChannel outboundProduct();
	
}
