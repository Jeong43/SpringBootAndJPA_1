package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

  @Autowired
  EntityManager em;
  @Autowired
  private OrderService orderService;
  @Autowired
  private OrderRepository orderRepository;


  @Test
  public void 상품주문() throws Exception {
    //given
    Member member = new Member();
    member.setName("회원1");
    member.setAddress(new Address("서울", "강가", "123-123"));
    em.persist(member);

    Book book = new Book();
    book.setName("시골 JAP");
    book.setPrice(10000);
    book.setStockQuantity(10);
    em.persist(book);

    int orderCount = 2;

    //when
    Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //then
    Order getOrder = orderRepository.findOne(orderId);

    assertEquals("상품 주문 시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
  }


  @Test
  public void 주문취소() throws Exception {
    //given

    //when

    //then
  }

  @Test
  public void 상품주문_재고수량초과() throws Exception {
    //given

    //when

    //then
  }


}