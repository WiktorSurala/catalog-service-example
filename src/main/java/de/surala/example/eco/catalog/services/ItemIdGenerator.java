package de.surala.example.eco.catalog.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class ItemIdGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    // I like single letters at the beginning, but 'I' not a good letter to start with,
    // so it's 'A' like "Artikel" (german word for Item)
    private static final String ITEM_ID_FORMAT = "A%05d";

    public String generateId() {
        Long nextVal = ((Number) entityManager.createNativeQuery("SELECT nextval('item_id_seq')").getSingleResult()).longValue();
        return String.format(ITEM_ID_FORMAT, nextVal); // Generates IDs like "A00001"
    }
}

