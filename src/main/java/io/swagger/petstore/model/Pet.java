package io.swagger.petstore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "pet")
public class Pet {

    public enum Status {
        AVAILABLE("available"),
        PENDING("pending"),
        SOLD("sold");

        private final String value;

        Status(String value) { this.value = value; }

        @JsonValue
        public String getValue() { return value; }

        @JsonCreator
        public static Status fromValue(String value) {
            if (value == null) return null;
            for (Status s : values()) {
                if (s.value.equalsIgnoreCase(value)) return s;
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private Category category;

    @NotNull
    @JacksonXmlElementWrapper(localName = "photoUrls")
    @JacksonXmlProperty(localName = "photoUrl")
    private List<String> photoUrls = new ArrayList<>();

    @JacksonXmlElementWrapper(localName = "tags")
    @JacksonXmlProperty(localName = "tag")
    private List<Tag> tags = new ArrayList<>();

    private Status status;

    public Pet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls == null ? new ArrayList<>() : photoUrls;
    }
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) {
        this.tags = tags == null ? new ArrayList<>() : tags;
    }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
