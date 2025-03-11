package com.example.HomeService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user in the system.
 * Stores essential user information such as name, email, role, and contact details.
 */
@Data
@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    /**
     * Primary key for the users table.
     * Auto-incremented unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the user.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Email address of the user. Must be unique.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Role of the user in the system (e.g., ADMIN, CUSTOMER, SERVICE_PROVIDER).
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Contact phone number of the user. Must be unique.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    /**
     * Encrypted password of the user.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Retrieves the role of the user.
     *
     * @return The role assigned to the user.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role The role to assign.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Retrieves the unique ID of the user.
     *
     * @return User ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the user.
     *
     * @param id The user ID to assign.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the full name of the user.
     *
     * @return User's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name of the user.
     *
     * @param name The name to assign.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the email address of the user.
     *
     * @return User's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email to assign.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the phone number of the user.
     *
     * @return User's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber The phone number to assign.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retrieves the encrypted password of the user.
     *
     * @return User's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the encrypted password of the user.
     *
     * @param password The password to assign.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of the Users object.
     */
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
