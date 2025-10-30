package in.ashokit.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient razorpayClient;

    public String createRazorpayOrder(Double amount) throws Exception {

        this.razorpayClient = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amount * 100); // amount in paisa
        orderRequest.put("currency", "INR");
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.Orders.create(orderRequest);

        return order.get("id");
    }

    public String refundPayment(String razorpayPaymentId, Integer amount) throws Exception {

        this.razorpayClient = new RazorpayClient(keyId, keySecret);

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", amount);

        Refund refund = razorpayClient.Payments.refund(razorpayPaymentId, refundRequest);

        return refund.toString();
    }
}
