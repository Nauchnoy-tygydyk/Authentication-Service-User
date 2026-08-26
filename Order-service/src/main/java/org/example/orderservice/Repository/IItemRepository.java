package org.example.orderservice.Repository;

import org.example.orderservice.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IItemRepository extends JpaRepository<Item, Long> {
}
