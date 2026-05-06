package io.swagger.petstore.controller;

import io.swagger.petstore.exception.BadRequestException;
import io.swagger.petstore.model.ApiResponse;
import io.swagger.petstore.model.Pet;
import io.swagger.petstore.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Pet addPet(@Valid @RequestBody Pet pet) {
        return petService.add(pet);
    }

    @PutMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Pet updatePet(@Valid @RequestBody Pet pet) {
        return petService.update(pet);
    }

    @GetMapping(value = "/findByStatus",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Pet> findByStatus(@RequestParam(value = "status", defaultValue = "available") String status) {
        List<Pet.Status> statuses = new ArrayList<>();
        try {
            for (String s : status.split(",")) {
                statuses.add(Pet.Status.fromValue(s.trim()));
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid status value");
        }
        return petService.findByStatus(statuses);
    }

    @GetMapping(value = "/findByTags",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Pet> findByTags(@RequestParam("tags") List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new BadRequestException("Invalid tag value");
        }
        // Support both ?tags=a&tags=b and ?tags=a,b
        List<String> flat = new ArrayList<>();
        for (String t : tags) {
            if (t == null) continue;
            flat.addAll(Arrays.asList(t.split(",")));
        }
        return petService.findByTags(flat);
    }

    @GetMapping(value = "/{petId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Pet getPetById(@PathVariable("petId") Long petId) {
        return petService.getById(petId);
    }

    @PostMapping(value = "/{petId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Pet updatePetWithForm(@PathVariable("petId") Long petId,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "status", required = false) String status) {
        Pet pet = petService.getById(petId);
        if (name != null) pet.setName(name);
        if (status != null) {
            try {
                pet.setStatus(Pet.Status.fromValue(status));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid status value");
            }
        }
        return petService.update(pet);
    }

    @DeleteMapping("/{petId}")
    public void deletePet(@PathVariable("petId") Long petId,
                          @RequestHeader(value = "api_key", required = false) String apiKey) {
        petService.delete(petId);
    }

    @PostMapping(value = "/{petId}/uploadImage",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse uploadImage(@PathVariable("petId") Long petId,
                                   @RequestParam(value = "additionalMetadata", required = false) String additionalMetadata,
                                   @RequestBody(required = false) byte[] body) {
        petService.getById(petId); // 404 if missing
        int size = body == null ? 0 : body.length;
        String msg = "additionalMetadata: " + additionalMetadata + "\nFile uploaded, " + size + " bytes";
        return new ApiResponse(200, "unknown", msg);
    }
}
