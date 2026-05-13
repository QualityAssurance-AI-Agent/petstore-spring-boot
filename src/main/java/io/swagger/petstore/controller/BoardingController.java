package io.swagger.petstore.controller;

import io.swagger.petstore.model.BoardingReservation;
import io.swagger.petstore.model.Kennel;
import io.swagger.petstore.service.BoardingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/boarding")
public class BoardingController {

    private final BoardingService boardingService;

    public BoardingController(BoardingService boardingService) {
        this.boardingService = boardingService;
    }

    // --- Kennel endpoints ---

    @PostMapping(value = "/kennel",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Kennel addKennel(@Valid @RequestBody Kennel kennel) {
        return boardingService.addKennel(kennel);
    }

    @GetMapping(value = "/kennel", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Kennel> getAllKennels() {
        return boardingService.getAllKennels();
    }

    @GetMapping(value = "/kennel/available", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Kennel> getAvailableKennels(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return boardingService.getAvailableKennels(checkIn, checkOut);
    }

    @GetMapping(value = "/kennel/{kennelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Kennel getKennel(@PathVariable Long kennelId) {
        return boardingService.getKennel(kennelId);
    }

    @DeleteMapping("/kennel/{kennelId}")
    public void deleteKennel(@PathVariable Long kennelId) {
        boardingService.deleteKennel(kennelId);
    }

    // --- Reservation endpoints ---

    @PostMapping(value = "/reservation",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BoardingReservation createReservation(@Valid @RequestBody BoardingReservation reservation) {
        return boardingService.createReservation(reservation);
    }

    @GetMapping(value = "/reservation/{reservationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BoardingReservation getReservation(@PathVariable Long reservationId) {
        return boardingService.getReservation(reservationId);
    }

    @GetMapping(value = "/reservation/pet/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardingReservation> getReservationsByPet(@PathVariable Long petId) {
        return boardingService.getReservationsByPet(petId);
    }

    @GetMapping(value = "/reservation/owner/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BoardingReservation> getReservationsByOwner(@PathVariable String username) {
        return boardingService.getReservationsByOwner(username);
    }

    @PostMapping(value = "/reservation/{reservationId}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public BoardingReservation checkIn(@PathVariable Long reservationId) {
        return boardingService.checkIn(reservationId);
    }

    @PostMapping(value = "/reservation/{reservationId}/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
    public BoardingReservation checkOut(@PathVariable Long reservationId) {
        return boardingService.checkOut(reservationId);
    }

    @PostMapping(value = "/reservation/{reservationId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public BoardingReservation cancelReservation(@PathVariable Long reservationId) {
        return boardingService.cancelReservation(reservationId);
    }
}
