package com.commerce.stream.protocol;

public class StreamChannel {

	private StreamChannel() {
		// Do nothing
	}

	/** receive message channel */
	public static final String CUSTOMER_BROADCAST_INPUT = "customer-broadcast-in";
	/** send message channel */
	public static final String CUSTOMER_BROADCAST_OUTPUT = "customer-broadcast-out";

	public static final String CUSTOMER_INPUT = "customer-in";

	public static final String CUSTOMER_OUTPUT = "customer-out";

	public static final String CUSTOMER_GROUP_DLQ = "customer.group.dlq";
	
	public static final String ORDER_INPUT = "order-in";

	public static final String ORDER_OUTPUT = "order-out";

	public static final String ORDER_GROUP_DLQ = "order.group.dlq";
	/** 訂單-回覆對列*/
	public static final String ORDER_REPLY_INPUT = "order-reply-in";

	public static final String ORDER_REPLY_OUTPUT = "order-reply-out";
	
	public static final String ORDER_REPLY_GROUP_DLQ = "order-reply.group.dlq";
	/** 訂單-優先對列 */
	public static final String ORDER_PRIORITY_INPUT = "order-priority-in";

	public static final String ORDER_PRIORITY_OUTPUT = "order-priority-out";

	public static final String ORDER_PRIORITY_GROUP_DLQ = "order-priority.group.dlq";
	
	public static final String PRODUCT_INPUT = "product-in";

	public static final String PRODUCT_OUTPUT = "product-out";

	public static final String PRODUCT_GROUP_DLQ = "product.group.dlq";
}
