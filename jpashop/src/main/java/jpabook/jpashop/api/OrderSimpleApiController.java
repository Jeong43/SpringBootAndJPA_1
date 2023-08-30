package jpabook.jpashop.api;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * xToOne(ManyToOne, OneToOne) 1. Order 2. Order -> Member 3. Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository orderRepository;
  private final OrderSimpleQueryRepository orderSimpleQueryRepository;

  @GetMapping("/api/v1/simple-orders")
  public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAll(new OrderSearch());

    for (Order order : all) {
      order.getMember().getName(); //Lazy 강제 초기화
      order.getDelivery().getAddress(); //Lazy 강제 초기화
    }
    return all;
  }

  @GetMapping("/api/v2/simple-orders")
  public List<SimpleOrderDto> ordersV2() {
    //N + 1 -> 1 + 회원 N + 배송 N
    return orderRepository.findAll(new OrderSearch()).stream()
        .map(SimpleOrderDto::new)
        .collect(toList());
  }

  @GetMapping("/api/v3/simple-orders")
  public List<SimpleOrderDto> ordersV3() {
    return orderRepository.findAllWithMemberDelivery().stream()
        .map(SimpleOrderDto::new)
        .collect(toList());
  }

  @GetMapping("/api/v4/simple-orders")
  public List<OrderSimpleQueryDto> ordersV4() {
    return orderSimpleQueryRepository.findOrderDtos();
  }

  @Data
  static class SimpleOrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderDto(Order order) {
      orderId = order.getId();
      name = order.getMember().getName(); //LAZY 초기화
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress(); //LAZY 초기화
    }
  }

}
