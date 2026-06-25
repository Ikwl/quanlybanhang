package com.example.quanlybanhang.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Manager")
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "managerId")
    private Long managerId;

    @OneToOne(optional = false)
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    @Column(name = "role", length = 50)
    private String role;

    protected Manager() {
    }

    public Manager(User user, String role) {
        this.user = user;
        this.role = role;
    }

    public Long getManagerId() {
        return managerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
