package com.placementtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPreferenceDto {
    private Long id;
    private List<String> preferredCompanies = new ArrayList<>();
    private List<String> preferredRoles = new ArrayList<>();
    private List<String> preferredLocations = new ArrayList<>();
    private List<String> preferredSkills = new ArrayList<>();
    private Integer minSalary;
    private boolean notifyEnabled = true;
}
