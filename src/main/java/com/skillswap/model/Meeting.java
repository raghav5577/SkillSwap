package com.skillswap.model;

import javax.persistence.*;

@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;
    private String tutor;
    private String learner;
    private String dateTime;
    private String meetLink;

    public Meeting() {}

    public Meeting(String skillName, String tutor, String learner, String dateTime) {
        this.skillName = skillName;
        this.tutor = tutor;
        this.learner = learner;
        this.dateTime = dateTime;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getTutor() { return tutor; }
    public void setTutor(String tutor) { this.tutor = tutor; }
    public String getLearner() { return learner; }
    public void setLearner(String learner) { this.learner = learner; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
}