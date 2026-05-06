package io.swagger.petstore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.OffsetDateTime;

@JacksonXmlRootElement(localName = "order")
public class Order {

    public enum Status {
        PLACED("placed"),
        APPROVED("approved"),
        DELIVERED("delivered");

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

    private Long id;
    private Long petId;
    private Integer quantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime shipDate;

    private Status status;
    private Boolean complete;

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public OffsetDateTime getShipDate() { return shipDate; }
    public void setShipDate(OffsetDateTime shipDate) { this.shipDate = shipDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Boolean getComplete() { return complete; }
    public void setComplete(Boolean complete) { this.complete = complete; }
}
