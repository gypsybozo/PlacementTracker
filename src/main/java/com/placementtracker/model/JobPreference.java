package com.placementtracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_preferences")
public class JobPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "preferred_companies", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "company")
    private Set<String> preferredCompanies = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "preferred_roles", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "role")
    private Set<String> preferredRoles = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "preferred_locations", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "location")
    private Set<String> preferredLocations = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "preferred_skills", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "skill")
    private Set<String> preferredSkills = new HashSet<>();
    
    @Column(name = "min_salary")
    private Integer minSalary;
    
    @Column(name = "notify_enabled")
    private boolean notifyEnabled = true;
}