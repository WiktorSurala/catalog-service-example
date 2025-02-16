package de.surala.example.eco.catalog.repository;

import de.surala.example.eco.catalog.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    Page<Item> findAll(Pageable pageable);
}
