package in.ashokit.service;

import in.ashokit.dto.*;
import in.ashokit.entity.CustomerEntity;
import in.ashokit.entity.OrderEntity;
import in.ashokit.entity.OrderItemEntity;
import in.ashokit.entity.ShippingAddressEntity;
import in.ashokit.mapper.AddressMapper;
import in.ashokit.mapper.CustomerMapper;
import in.ashokit.mapper.OrderItemMapper;
import in.ashokit.mapper.OrderMapper;
import in.ashokit.repo.CustomerRepo;
import in.ashokit.repo.OrderItemRepo;
import in.ashokit.repo.OrderRepo;
import in.ashokit.repo.ShippingAddressRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderItemRepo itemRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ShippingAddressRepo addressRepo;

    @Autowired
    private RazorpayService razorpayService;

    @Override
    public PurchaseOrderResponseDto createOrder(PurchaseOrderRequestDto orderRequestDto) throws Exception {

        CustomerDto customerDto = orderRequestDto.getCustomerDto();
        AddressDto addressDto = orderRequestDto.getAddressDto();
        OrderDto orderDto = orderRequestDto.getOrderDto();
        List<OrderItemDto> orderItemDtoList = orderRequestDto.getOrderItemDtoList();

        // Saving Customer
        CustomerEntity customerEntity = customerRepo.findByEmail(customerDto.getEmail());
        if (customerEntity == null) {
            customerEntity = CustomerMapper.convertToEntity(customerDto);
            customerRepo.save(customerEntity);
            // TODO : Interservice communication
        }

        // Saving Address
        ShippingAddressEntity addressEntity = null;

        if (addressDto.getAddrId() == null) {
            addressEntity = AddressMapper.toEntity(addressDto);
            addressEntity.setCustomer(customerEntity); // association mapping
            addressRepo.save(addressEntity);
        } else {
            addressEntity = addressRepo.findById(addressDto.getAddrId()).get();
        }

        // Saving ORDER
        String orderTrackingNum = generateRandomTrackingNumber();
        orderDto.setOrderTrackingNum(orderTrackingNum);

        String razorpayOrderId = razorpayService.createRazorpayOrder(orderDto.getTotalPrice());
        orderDto.setRazorpayOrderId(razorpayOrderId);
        orderDto.setOrderStatus("CREATED");
        orderDto.setPaymentStatus("PENDING");

        OrderEntity orderEntity = OrderMapper.convertToEntity(orderDto);
        orderEntity.setCustomer(customerEntity); // ASSOCIATION MAPPING
        orderEntity.setShippingAddress(addressEntity); // ASSOCIATION MAPPING

        orderEntity.setCustomerEmail(customerEntity.getEmail());

        orderEntity = orderRepo.save(orderEntity);

        // SAVE ORDER ITEMS
        for (OrderItemDto itemDto : orderItemDtoList) {
            OrderItemEntity orderItemEntity = OrderItemMapper.convertToEntity(itemDto);
            orderItemEntity.setOrder(orderEntity); // Association Mapping
            itemRepo.save(orderItemEntity);
        }

        // prepare final response
        PurchaseOrderResponseDto responseDto = new PurchaseOrderResponseDto();
        responseDto.setRazorpayOrderId(razorpayOrderId);
        responseDto.setOrderTrackingNumber(orderTrackingNum);
        responseDto.setOrderStatus("CREATED");

        return responseDto;
    }

    @Override
    public PurchaseOrderResponseDto updateOrder(PurchaseOrderRequestDto orderRequestDto) {
        return null;
    }

    @Override
    public PurchaseOrderRequestDto cancelOrder(String orderTrackingNumber) {
        return null;
    }

    @Override
    public List<OrderDto> getCustomerOrders(String customerEmail) {
        return List.of();
    }


    private String generateRandomTrackingNumber() {

        // get the current timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        // generate random uuid
        String uuid = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        // combine OD with timestamp and UUID
        return "OD" + timestamp + uuid;
    }
}
