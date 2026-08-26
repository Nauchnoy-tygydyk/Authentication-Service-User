package org.example.orderservice.Repository;

import org.example.orderservice.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
  Page<Order> findAllByUserId(Long userId, Pageable pageable);
  List<Order> findByUserId(Long userId);
}
