package com.fuzis.techtask.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entity для хранения абонентов текущей сети в базе данных. Содержит в себе пока только поле
 * {@code phoneNumber} и индекс для БД. <br>
 * В данной классе представлены getтеры и setтеры для обращения ко всем указаным полям.
 */
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String phoneNumber;

    public Client(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Client() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
