package com.davivienda.kata.repository;

import com.davivienda.kata.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status); // <- esto filtra eventos por estado

}
