package com.davivienda.kata.service.impl;

import com.davivienda.kata.dto.ReservationRequestDTO;
import com.davivienda.kata.dto.ReservationResponseDTO;
import com.davivienda.kata.model.Event;
import com.davivienda.kata.model.Reservation;
import com.davivienda.kata.repository.EventRepository;
import com.davivienda.kata.repository.ReservationRepository;
import com.davivienda.kata.service.IReservationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository       eventRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  EventRepository eventRepository) {
        this.reservationRepository = reservationRepository;
        this.eventRepository       = eventRepository;
    }

    /* -------------------------------------------------------------
       1. Crear reserva
    --------------------------------------------------------------*/
    @Override
    public ReservationResponseDTO createReservation(ReservationRequestDTO request) {

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        int qtySolicitada = request.getQty() == null ? 1 : request.getQty();
        int disponibles   = event.getCapacity() - event.getReservedCount();

        if (qtySolicitada <= 0)
            throw new RuntimeException("Cantidad inválida");

        if (disponibles < qtySolicitada)
            throw new RuntimeException("El evento está lleno");

        /* Actualiza contadores */
        event.setReservedCount(event.getReservedCount() + qtySolicitada);

        /* Cambia estado si ya no hay cupo */
        if (event.getReservedCount() >= event.getCapacity()) {
            event.setStatus("FULL");
        }

        eventRepository.save(event);

        /* Crea la reserva */
        Reservation reservation = new Reservation();
        reservation.setUserEmail(request.getUserEmail());
        reservation.setEvent(event);
        Reservation saved = reservationRepository.save(reservation);

        return new ReservationResponseDTO(
                saved.getId(),
                saved.getUserEmail(),
                saved.getEvent().getName(),
                saved.getEvent().getStatus()
        );
    }

    /* -------------------------------------------------------------
       2. Listar reservas por usuario
    --------------------------------------------------------------*/
    @Override
    public List<ReservationResponseDTO> getReservationsByUser(String email) {
        return reservationRepository.findDtoByUserEmail(email);
    }

}
