package com.placementtracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user") // Prevents infinite loop in toString
@EqualsAndHashCode(exclude = "user") // Prevents infinite loop in hashCode
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

    @ElementCollection
    private List<String> preferredTags;
}