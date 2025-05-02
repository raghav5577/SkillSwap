package com.skillswap.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String tutor;
    private String category;
    private double rating;
    private String sessionDuration;
    private String classSize;
    
    @ElementCollection
    @CollectionTable(name = "skill_availability", 
                    joinColumns = @JoinColumn(name = "skill_id"))
    @Column(name = "day")
    private List<String> availability;
    
    private double hourlyRate;

    public Skill() {}

    private static Long nextId = 1L;

    public Skill(String name, String description, String tutor) {
        this.id = generateId();
        this.name = name;
        this.description = description;
        this.tutor = tutor;
        this.rating = 4.5 + Math.random() * 0.5; // Random rating between 4.5 and 5.0
        this.category = determineCategory(name);
        this.sessionDuration = "1"; // Default duration
        this.classSize = "Small"; // Default class size
        this.hourlyRate = 1000.0; // Default hourly rate
        this.availability = List.of("Monday", "Wednesday", "Friday"); // Default availability
    }

    public Skill(String name, String description, String tutor, String category, 
                String sessionDuration, String classSize, List<String> availability, double hourlyRate) {
        this.id = generateId();
        this.name = name;
        this.description = description;
        this.tutor = tutor;
        this.category = category;
        this.sessionDuration = sessionDuration;
        this.classSize = classSize;
        this.availability = availability;
        this.hourlyRate = hourlyRate;
        this.rating = 4.5 + Math.random() * 0.5; // Random rating between 4.5 and 5.0
    }

    private synchronized Long generateId() {
        return nextId++;
    }

    private String determineCategory(String name) {
        name = name.toLowerCase();
        if (name.contains("programming") || name.contains("development") || name.contains("coding") || 
            name.contains("web") || name.contains("frontend") || name.contains("backend") || 
            name.contains("fullstack") || name.contains("software")) {
            return "Programming";
        } else if (name.contains("design") || name.contains("art") || name.contains("drawing")) {
            return "Design";
        } else if (name.contains("marketing") || name.contains("seo") || name.contains("social media")) {
            return "Marketing";
        } else if (name.contains("music") || name.contains("guitar") || name.contains("piano")) {
            return "Music";
        } else if (name.contains("language") || name.contains("english") || name.contains("spanish")) {
            return "Language";
        }
        return "Other";
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTutor() { return tutor; }
    public void setTutor(String tutor) { this.tutor = tutor; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getFormattedRating() { return String.format("%.1f", rating); }
    public String getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(String sessionDuration) { this.sessionDuration = sessionDuration; }
    public String getClassSize() { return classSize; }
    public void setClassSize(String classSize) { this.classSize = classSize; }
    public List<String> getAvailability() { return availability; }
    public void setAvailability(List<String> availability) { this.availability = availability; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public String getFormattedHourlyRate() { return String.format("₹%.2f", hourlyRate); }
}