package com.programmingtechie.inventoryservice.Repositories;

import com.programmingtechie.inventoryservice.Entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>{
    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
