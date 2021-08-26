package com.commerce.stream.protocol;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@SuppressWarnings("deprecation")
public interface LiveProtocols {
	
	@Input(LiveChannel.WS_BROADCAST_INPUT)
	SubscribableChannel inboundWsBroadcast();

	@Output(LiveChannel.WS_BROADCAST_OUTPUT)
	MessageChannel outboundWSBroadcast();
}
