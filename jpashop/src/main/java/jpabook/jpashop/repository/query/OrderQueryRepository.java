package jpabook.jpashop.repository.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

  private final EntityManager em;

  public List<OrderQueryDto> findOrderQueryDtos() {
    List<OrderQueryDto> result = findOrders(); //Query 1번

    result.forEach(o -> {
      List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //Query N번
      o.setOrderItems(orderItems);
    });

    return result;
  }

  public List<OrderQueryDto> findAllByDto_optimization() {
    List<OrderQueryDto> result = findOrders(); //Query 1번: 루트

    List<Long> orderIds = toOrderIds(result);
    Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

    //메모리에서 값을 넣어줌
    result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
  }

  private List<Long> toOrderIds(List<OrderQueryDto> result) {
    return result.stream()
        .map(OrderQueryDto::getOrderId)
        .collect(Collectors.toList());
  }

  private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
    //Query 1번: 컬렉션
    List<OrderItemQueryDto> orderItems = em.createQuery(
            "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                + " from OrderItem oi"
                + " join oi.item i"
                + " where oi.order.id in :orderIds", OrderItemQueryDto.class
        ).setParameter("orderIds", orderIds)
        .getResultList();

    //Map 으로 변환
    return orderItems.stream()
        .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
  }

  private List<OrderQueryDto> findOrders() {
    return em.createQuery(
        "select new jpabook.jpashop.repository.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
            + " from Order o"
            + " join o.member m"
            + " join o.delivery d", OrderQueryDto.class
    ).getResultList();
  }

  private List<OrderItemQueryDto> findOrderItems(Long orderId) {
    return em.createQuery(
            "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                + " from OrderItem oi"
                + " join oi.item i"
                + " where oi.order.id = :orderId", OrderItemQueryDto.class
        ).setParameter("orderId", orderId)
        .getResultList();
  }
}
