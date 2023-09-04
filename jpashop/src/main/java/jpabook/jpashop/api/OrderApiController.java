package jpabook.jpashop.api;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.query.OrderQueryDto;
import jpabook.jpashop.repository.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;
  private final OrderQueryRepository orderQueryRepository;

  @GetMapping("api/v1/orders")
  public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAll(new OrderSearch());

    all.forEach(order -> {
      order.getMember().getName();
      order.getDelivery().getAddress();

      order.getOrderItems()
          .forEach(orderItem -> orderItem.getItem().getName());
    });

    return all;
  }

  @GetMapping("api/v2/orders")
  public List<OrderDto> ordersV2() {
    return orderRepository.findAll(new OrderSearch()).stream()
        .map(OrderDto::new)
        .collect(toList());
  }

  @GetMapping("api/v3/orders")
  public List<OrderDto> ordersV3() {
    return orderRepository.findAllWithItem().stream()
        .map(OrderDto::new)
        .collect(toList());
  }

  @GetMapping("api/v3.1/orders")
  public List<OrderDto> ordersV3_page(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "100") int limit) {
    return orderRepository.findAllWithMemberDelivery(offset, limit).stream()
        .map(OrderDto::new)
        .collect(toList());
  }


  @GetMapping("api/v4/orders")
  public List<OrderQueryDto> ordersV4_page() {
    return orderQueryRepository.findOrderQueryDtos();
  }


  @Data
  static class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName();
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress();
      orderItems = order.getOrderItems().stream().map(OrderItemDto::new).collect(toList());
    }
  }

  @Data
  static class OrderItemDto {

    private String itemName; //상품명
    private int orderPrice; //주문가격
    private int count; //주문 수량

    public OrderItemDto(OrderItem orderItem) {
      itemName = orderItem.getItem().getName();
      orderPrice = orderItem.getOrderPrice();
      count = orderItem.getCount();
    }
  }
}
