
package com.smartexam.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invigilators")
public class Invigilator {

    @Id
    @Column(name = "invigilator_id", length = 20)
    private String invigilatorId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private boolean available = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "invigilator_unavailable_dates",
        joinColumns = @JoinColumn(name = "invigilator_id")
    )
    @Column(name = "unavailable_date")
    private Set<java.time.LocalDate> unavailableDates = new HashSet<>();

    public Invigilator() {
    }

    public String getInvigilatorId() {
        return invigilatorId;
    }

    public void setInvigilatorId(String invigilatorId) {
        this.invigilatorId = invigilatorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Set<java.time.LocalDate> getUnavailableDates() {
        return unavailableDates;
    }

    public void setUnavailableDates(Set<java.time.LocalDate> unavailableDates) {
        this.unavailableDates = unavailableDates;
    }
}
