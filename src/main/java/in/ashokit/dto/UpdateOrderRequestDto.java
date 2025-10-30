package in.ashokit.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateOrderRequestDto {
    private Integer orderId;
    private String orderTrackingNum;
    private String orderStatus;
    private String paymentStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
}
