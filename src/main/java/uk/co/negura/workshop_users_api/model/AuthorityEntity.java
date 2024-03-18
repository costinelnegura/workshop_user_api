package uk.co.negura.workshop_users_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "authorities")
public class AuthorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    public AuthorityEntity(Long id, String name) {
        Id = id;
        this.name = name;
    }

    public AuthorityEntity() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
