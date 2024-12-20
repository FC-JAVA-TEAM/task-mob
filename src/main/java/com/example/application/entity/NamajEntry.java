package com.example.application.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "namaj_entries", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"namazDate", "namazType", "user_id"}) // Add user_id to the unique constraint
	})
public class NamajEntry {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private LocalDate namazDate;

	    @Column(nullable = false, length = 20)
	    private String namazType;

	    @Column(nullable = false, length = 20)
	    private String jamatType;

	    @Column(nullable = false, length = 10) // Assuming the length for the string
	    private String missed; // Change to String to hold "Yes" or "No"

	    private String kaza;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)  // Foreign key to UserEntity
	    @JsonIgnore
	    private MobUser user;
	    public MobUser getUser() {
	        return user;
	    }

	    public void setUser(MobUser user) {
	        this.user = user;
	    }

    // Getters and Setters

    public String  isKaza() {
		return kaza;
	}

	public void setKaza(String  kaza) {
		this.kaza = kaza;
	}

	public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getNamazDate() {
        return namazDate;
    }

    public void setNamazDate(LocalDate namazDate) {
        this.namazDate = namazDate;
    }

    public String getNamazType() {
        return namazType;
    }

    public void setNamazType(String namazType) {
        this.namazType = namazType;
    }

    public String getJamatType() {
        return jamatType;
    }
 
    public void setJamatType(String jamatType) {
        this.jamatType = jamatType;
    }

	public String getMissed() {
		return missed;
	}

	public void setMissed(String missed) {
		this.missed = missed;
	}

	public String getKaza() {
		return kaza;
	}

   
}
