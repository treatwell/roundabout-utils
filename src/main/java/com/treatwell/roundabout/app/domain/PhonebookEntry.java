package com.treatwell.roundabout.app.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PhonebookEntry {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private String phoneNumber;

    private PhonebookEntry() {
    }

    public PhonebookEntry(Long id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
