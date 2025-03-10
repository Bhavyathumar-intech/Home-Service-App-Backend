package com.example.HomeService.model;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Users {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "name", nullable = false)
    private String name;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
	 @Enumerated(EnumType.STRING)
	 private Role role;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "password", nullable = false)
    private String password;

	
    public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

   
	@Override
	public String toString() {
	    return "Users{" +
	            "id=" + id +
	            ", name='" + name + '\'' +
	            ", email='" + email + '\'' +
	            ", role=" + role +
	            ", phoneNumber='" + phoneNumber + '\'' +
	            '}';
	}

	

	

    
}
