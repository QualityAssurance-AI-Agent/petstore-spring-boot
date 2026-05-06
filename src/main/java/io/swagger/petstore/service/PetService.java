package io.swagger.petstore.service;

import io.swagger.petstore.exception.NotFoundException;
import io.swagger.petstore.model.Category;
import io.swagger.petstore.model.Pet;
import io.swagger.petstore.model.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PetService {

    private final ConcurrentMap<Long, Pet> pets = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1000);

    @PostConstruct
    void seed() {
        Pet p1 = new Pet();
        p1.setId(1L);
        p1.setName("Dog 1");
        p1.setCategory(new Category(1L, "Dogs"));
        p1.setPhotoUrls(new ArrayList<>(List.of("url1", "url2")));
        p1.setTags(new ArrayList<>(List.of(new Tag(1L, "tag1"), new Tag(2L, "tag2"))));
        p1.setStatus(Pet.Status.AVAILABLE);
        pets.put(p1.getId(), p1);

        Pet p2 = new Pet();
        p2.setId(2L);
        p2.setName("Cat 1");
        p2.setCategory(new Category(2L, "Cats"));
        p2.setPhotoUrls(new ArrayList<>(List.of("url1", "url2")));
        p2.setTags(new ArrayList<>(List.of(new Tag(1L, "tag1"), new Tag(2L, "tag2"))));
        p2.setStatus(Pet.Status.AVAILABLE);
        pets.put(p2.getId(), p2);
    }

    public Pet add(Pet pet) {
        if (pet.getId() == null || pet.getId() == 0L) {
            pet.setId(idSeq.incrementAndGet());
        }
        pets.put(pet.getId(), pet);
        return pet;
    }

    public Pet update(Pet pet) {
        if (pet.getId() == null || !pets.containsKey(pet.getId())) {
            throw new NotFoundException("Pet not found");
        }
        pets.put(pet.getId(), pet);
        return pet;
    }

    public Pet getById(long id) {
        Pet pet = pets.get(id);
        if (pet == null) throw new NotFoundException("Pet not found");
        return pet;
    }

    public void delete(long id) {
        if (pets.remove(id) == null) {
            throw new NotFoundException("Pet not found");
        }
    }

    public List<Pet> findByStatus(List<Pet.Status> statuses) {
        List<Pet> out = new ArrayList<>();
        for (Pet p : pets.values()) {
            if (statuses.contains(p.getStatus())) out.add(p);
        }
        return out;
    }

    public List<Pet> findByTags(List<String> tagNames) {
        List<Pet> out = new ArrayList<>();
        for (Pet p : pets.values()) {
            if (p.getTags() == null) continue;
            for (Tag t : p.getTags()) {
                if (t != null && tagNames.contains(t.getName())) {
                    out.add(p);
                    break;
                }
            }
        }
        return out;
    }

    public Collection<Pet> all() {
        return pets.values();
    }
}
