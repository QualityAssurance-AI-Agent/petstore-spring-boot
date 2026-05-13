package io.swagger.petstore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JacksonXmlRootElement(localName = "kennel")
public class Kennel {

    public enum Size {
        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large");

        private final String value;

        Size(String value) { this.value = value; }

        @JsonValue
        public String getValue() { return value; }

        @JsonCreator
        public static Size fromValue(String value) {
            if (value == null) return null;
            for (Size s : values()) {
                if (s.value.equalsIgnoreCase(value)) return s;
            }
            throw new IllegalArgumentException("Unknown kennel size: " + value);
        }
    }

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Size size;

    @NotNull
    @Min(1)
    private Integer capacity;

    private String description;

    public Kennel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Size getSize() { return size; }
    public void setSize(Size size) { this.size = size; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
