package uk.co.negura.workshop_users_api.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "roles_authorities",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id")
    )
    private List<AuthorityEntity> authorities = new ArrayList<>();

    public RoleEntity(Long id, String name, List<AuthorityEntity> authorities) {
        Id = id;
        this.name = name;
        this.authorities = authorities;
    }

    public RoleEntity() {
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

    public List<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<AuthorityEntity> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "RoleEntity{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
