package jpabook.jpashop.repository.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderItemQueryDto {

  @JsonIgnore
  private Long orderId;
  private String itemName;
  private int oderPrice;
  private int count;

  public OrderItemQueryDto(Long orderId, String itemName, int oderPrice, int count) {
    this.orderId = orderId;
    this.itemName = itemName;
    this.oderPrice = oderPrice;
    this.count = count;
  }
}
