  package org.example.orderservice.specification;


  import org.example.orderservice.Entity.Order;
  import org.example.orderservice.Entity.OrderStatus;
  import org.springframework.data.jpa.domain.Specification;

  import java.time.LocalDateTime;

  public class OrderSpecification {

    public static Specification<Order> hasStatus(OrderStatus status) {

      return (root, query, cb) -> {
        if (status == null){ return null;}
        return cb.equal(root.get("status"), status);
      };
    }

    public static Specification<Order> createdBetween(LocalDateTime start, LocalDateTime end){
      return (root, query, cb) -> {
        if (start == null || end == null){
          return null;
        }
        return cb.between(root.get("createdAt"), start, end);
      };
    }
      }

