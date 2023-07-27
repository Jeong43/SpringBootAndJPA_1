package jpabook.jpashop.repository;

import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class, id);
  }

  public List<Order> findAll(OrderSearch orderSearch) {
    //TODO private final JPAQueryFactory query; 로 선언 후 생성자 주입으로 개선
    JPAQueryFactory query = new JPAQueryFactory(em);

    return query
        .select(order)
        .from(order)
        .join(order.member, member)
        .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
        .limit(1000)
        .fetch();
  }

  private BooleanExpression statusEq(OrderStatus statusCond) {
    if (statusCond == null) {
      return null;
    }
    return order.status.eq(statusCond);
  }

  private static BooleanExpression nameLike(String memberName) {
    if (!StringUtils.hasText(memberName)) {
      return null;
    }
    return member.name.like(memberName);
  }

}
