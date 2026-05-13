package io.swagger.petstore.service;

import io.swagger.petstore.exception.BadRequestException;
import io.swagger.petstore.exception.NotFoundException;
import io.swagger.petstore.model.BoardingReservation;
import io.swagger.petstore.model.BoardingReservation.Status;
import io.swagger.petstore.model.Kennel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BoardingService {

    private final ConcurrentMap<Long, Kennel> kennels = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, BoardingReservation> reservations = new ConcurrentHashMap<>();
    private final AtomicLong kennelIdSeq = new AtomicLong(10);
    private final AtomicLong reservationIdSeq = new AtomicLong(500);

    private final PetService petService;
    private final UserService userService;

    public BoardingService(PetService petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
        seedKennels();
    }

    // --- Kennel management ---

    public Kennel addKennel(Kennel kennel) {
        if (kennel.getId() == null || kennel.getId() == 0L) {
            kennel.setId(kennelIdSeq.incrementAndGet());
        }
        kennels.put(kennel.getId(), kennel);
        return kennel;
    }

    public Kennel getKennel(long id) {
        Kennel k = kennels.get(id);
        if (k == null) throw new NotFoundException("Kennel not found");
        return k;
    }

    public List<Kennel> getAllKennels() {
        return List.copyOf(kennels.values());
    }

    public List<Kennel> getAvailableKennels(LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return kennels.values().stream()
                .filter(k -> availableSlots(k.getId(), checkIn, checkOut) > 0)
                .collect(Collectors.toList());
    }

    public void deleteKennel(long id) {
        if (kennels.remove(id) == null) throw new NotFoundException("Kennel not found");
    }

    // --- Reservation management ---

    public BoardingReservation createReservation(BoardingReservation r) {
        validateDates(r.getCheckInDate(), r.getCheckOutDate());
        petService.getById(r.getPetId());
        userService.getByUsername(r.getOwnerUsername());

        Kennel kennel = getKennel(r.getKennelId());
        if (availableSlots(kennel.getId(), r.getCheckInDate(), r.getCheckOutDate()) <= 0) {
            throw new BadRequestException("Kennel is fully booked for the requested dates");
        }

        if (r.getId() == null || r.getId() == 0L) {
            r.setId(reservationIdSeq.incrementAndGet());
        }
        r.setStatus(Status.CONFIRMED);
        reservations.put(r.getId(), r);
        return r;
    }

    public BoardingReservation getReservation(long id) {
        BoardingReservation r = reservations.get(id);
        if (r == null) throw new NotFoundException("Reservation not found");
        return r;
    }

    public List<BoardingReservation> getReservationsByPet(long petId) {
        return reservations.values().stream()
                .filter(r -> petId == r.getPetId())
                .collect(Collectors.toList());
    }

    public List<BoardingReservation> getReservationsByOwner(String username) {
        return reservations.values().stream()
                .filter(r -> username.equals(r.getOwnerUsername()))
                .collect(Collectors.toList());
    }

    public BoardingReservation checkIn(long id) {
        BoardingReservation r = getReservation(id);
        if (r.getStatus() != Status.CONFIRMED) {
            throw new BadRequestException("Only confirmed reservations can be checked in");
        }
        r.setStatus(Status.CHECKED_IN);
        return r;
    }

    public BoardingReservation checkOut(long id) {
        BoardingReservation r = getReservation(id);
        if (r.getStatus() != Status.CHECKED_IN) {
            throw new BadRequestException("Only checked-in reservations can be checked out");
        }
        r.setStatus(Status.CHECKED_OUT);
        return r;
    }

    public BoardingReservation cancelReservation(long id) {
        BoardingReservation r = getReservation(id);
        if (r.getStatus() == Status.CHECKED_OUT || r.getStatus() == Status.CANCELLED) {
            throw new BadRequestException("Reservation cannot be cancelled in its current state");
        }
        r.setStatus(Status.CANCELLED);
        return r;
    }

    // --- Helpers ---

    private int availableSlots(long kennelId, LocalDate checkIn, LocalDate checkOut) {
        Kennel kennel = kennels.get(kennelId);
        if (kennel == null) throw new NotFoundException("Kennel not found");

        long occupied = reservations.values().stream()
                .filter(r -> r.getKennelId() == kennelId)
                .filter(r -> r.getStatus() != Status.CANCELLED && r.getStatus() != Status.CHECKED_OUT)
                .filter(r -> r.getCheckInDate().isBefore(checkOut) && r.getCheckOutDate().isAfter(checkIn))
                .count();

        return kennel.getCapacity() - (int) occupied;
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Check-in and check-out dates are required");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }

    private void seedKennels() {
        Kennel small = new Kennel();
        small.setId(kennelIdSeq.incrementAndGet());
        small.setName("Small Suites");
        small.setSize(Kennel.Size.SMALL);
        small.setCapacity(5);
        small.setDescription("Cozy suites for small dogs and cats");
        kennels.put(small.getId(), small);

        Kennel large = new Kennel();
        large.setId(kennelIdSeq.incrementAndGet());
        large.setName("Large Lodges");
        large.setSize(Kennel.Size.LARGE);
        large.setCapacity(3);
        large.setDescription("Spacious lodges for large breeds");
        kennels.put(large.getId(), large);
    }
}
