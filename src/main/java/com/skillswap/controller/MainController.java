package com.skillswap.controller;

import com.skillswap.model.Meeting;
import com.skillswap.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private List<Skill> skills = new ArrayList<Skill>() {{
        add(new Skill("Java Programming", "Learn Java from basics to advanced", "Alice"));
        add(new Skill("Web Development", "HTML, CSS, JavaScript and modern frameworks", "Bob", "Programming", "2", "Small", Arrays.asList("Monday", "Wednesday", "Friday"), 1500.0));
        add(new Skill("Public Speaking", "Improve your communication skills", "Charlie"));
        add(new Skill("Photography", "Basics of digital photography", "Diana"));
        add(new Skill("Python Programming", "Introduction to Python", "Ethan"));
        add(new Skill("Frontend Development", "Modern frontend frameworks and tools", "Frank", "Programming", "2", "Small", Arrays.asList("Tuesday", "Thursday", "Saturday"), 1200.0));
        add(new Skill("Backend Development", "Server-side programming and databases", "Grace", "Programming", "2.5", "Small", Arrays.asList("Monday", "Wednesday", "Friday"), 1800.0));
        add(new Skill("Full Stack Development", "End-to-end web application development", "Henry", "Programming", "3", "Small", Arrays.asList("Tuesday", "Thursday", "Saturday"), 2000.0));
        add(new Skill("UI/UX Design", "Create beautiful and functional interfaces", "Isabella", "Design", "2", "Small", Arrays.asList("Monday", "Wednesday"), 1500.0));
        add(new Skill("Digital Marketing", "SEO, SEM, and social media marketing", "Nina"));
        add(new Skill("Mobile App Development", "Build Android and iOS apps", "Oscar"));
        add(new Skill("Cooking", "Master the art of cooking delicious meals", "Fiona"));
        add(new Skill("Dancing", "Learn various dance forms", "George"));
        add(new Skill("Guitar Playing", "Play acoustic and electric guitar", "Hannah"));
        add(new Skill("Beat Boxing", "Learn the basics of beat boxing", "Ivan"));
        add(new Skill("Painting", "Acrylic and watercolor painting techniques", "Julia"));
        add(new Skill("Yoga", "Yoga for beginners and advanced", "Kevin"));
        add(new Skill("Chess", "Improve your chess strategies", "Laura"));
        add(new Skill("French Language", "Learn to speak French fluently", "Mike"));
        add(new Skill("Photography Editing", "Edit photos like a pro", "Quentin"));
        add(new Skill("Swimming", "Learn swimming from basics", "Rachel"));
        add(new Skill("Mathematics", "Algebra, calculus, and more", "Steve"));
        add(new Skill("Public Relations", "Master PR skills", "Tina"));
    }};
    private List<Meeting> meetings = new ArrayList<>();
    private Map<String, List<String>> userMessages = new HashMap<>();

    @GetMapping("/login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Login page accessed. Current authentication: {}", authentication);
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            logger.debug("User is already authenticated, redirecting to dashboard");
            return "redirect:/dashboard";
        }
        logger.debug("Showing login page");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        logger.debug("Dashboard accessed by user: {}", principal.getName());
        model.addAttribute("username", principal.getName());
        model.addAttribute("skills", skills);
        model.addAttribute("meetings", meetings);
        // Add messages for the logged-in user
        List<String> messages = userMessages.getOrDefault(principal.getName(), new ArrayList<>());
        model.addAttribute("messages", messages);
        return "dashboard";
    }

    @GetMapping("/skills")
    public String skillsPage(@RequestParam(value = "query", required = false) String query,
                             @RequestParam(value = "tutor", required = false) String tutor,
                             Model model) {
        List<Skill> filteredSkills = new ArrayList<>(skills);

        if (query != null && !query.isEmpty()) {
            filteredSkills.removeIf(skill -> !skill.getName().toLowerCase().contains(query.toLowerCase()));
        }
        if (tutor != null && !tutor.isEmpty()) {
            filteredSkills.removeIf(skill -> !skill.getTutor().toLowerCase().contains(tutor.toLowerCase()));
        }

        model.addAttribute("skills", filteredSkills);
        model.addAttribute("query", query);
        model.addAttribute("tutor", tutor);
        return "skills";
    }
    
    @PostMapping("/skills")
    public String addSkill(@RequestParam String name,
                          @RequestParam String description,
                          @RequestParam String category,
                          @RequestParam String sessionDuration,
                          @RequestParam String classSize,
                          @RequestParam String availability,
                          @RequestParam double hourlyRate,
                          Principal principal) {
        List<String> availabilityList = Arrays.asList(availability.split(","));
        Skill newSkill = new Skill(name, description, principal.getName(), 
                                 category, sessionDuration, classSize, 
                                 availabilityList, hourlyRate);
        skills.add(newSkill);
        return "redirect:/dashboard";
    }

    @GetMapping("/meetings")
    public String meetingsPage(Model model) {
        model.addAttribute("meetings", meetings);
        return "meetings";
    }

    @RequestMapping(value = "/schedule/{id}", method = RequestMethod.GET)
    public String scheduleSession(@PathVariable("id") Long id, Model model) {
        logger.info("Accessing schedule page for skill ID: {}", id);
        
        // Find the skill
        Skill skillToSchedule = null;
        for (Skill s : skills) {
            if (s.getId().equals(id)) {
                skillToSchedule = s;
                break;
            }
        }
        
        // If skill not found, redirect to dashboard
        if (skillToSchedule == null) {
            logger.warn("Skill not found with ID: {}", id);
            return "redirect:/dashboard";
        }
        
        // Add skill to model and render template
        logger.info("Found skill: {}, rendering schedule page", skillToSchedule.getName());
        model.addAttribute("skill", skillToSchedule);
        return "schedule-session";
    }

    @PostMapping("/meetings")
    public String scheduleMeeting(@RequestParam String skillName, 
                                @RequestParam String tutor,
                                @RequestParam String date,
                                @RequestParam String time,
                                @RequestParam(required = false) String notes,
                                Principal principal) {
        // Generate a mock Google Meet link
        String meetLink = "https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 8);
    
        // Combine date and time
        String dateTime = date + " " + time;
    
        // Create meeting
        Meeting meeting = new Meeting(skillName, tutor, principal.getName(), dateTime);
        meeting.setMeetLink(meetLink);  // Set the Google Meet link
        meetings.add(meeting);
    
        // Prepare notification message with clickable link
        String message = String.format(
            "Meeting scheduled for skill: %s on %s at %s. %s Google Meet link: %s",
            skillName, date, time,
            notes != null ? "Notes: " + notes + "." : "",
            meetLink
        );
    
        // Send message to learner
        userMessages.computeIfAbsent(principal.getName(), k -> new ArrayList<>())
                   .add("To Learner: " + message);
    
        // Send message to tutor
        userMessages.computeIfAbsent(tutor, k -> new ArrayList<>())
                   .add("To Tutor: " + message);
    
        return "redirect:/dashboard";
    }

    @Autowired
    private InMemoryUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerPage() {
        logger.debug("Accessing registration page");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, 
                             @RequestParam String password,
                             @RequestParam String fullName,
                             @RequestParam(required = false) String confirmPassword,
                             @RequestParam String role, 
                             Model model) {
        try {
            logger.debug("Attempting to register user: {} with role: {}", username, role);
            
            // Validate password confirmation
            if (confirmPassword != null && !password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match!");
                return "register";
            }
            
        if (userDetailsManager.userExists(username)) {
                logger.debug("Username already exists: {}", username);
                model.addAttribute("error", "Email address already exists!");
                return "register";
            }
            
            String encodedPassword = passwordEncoder.encode(password);
            logger.debug("Password encoded successfully for user: {}", username);
            
            org.springframework.security.core.userdetails.User.UserBuilder userBuilder = 
                org.springframework.security.core.userdetails.User.withUsername(username)
                    .password(encodedPassword)
                    .roles(role.toUpperCase());
            
            userDetailsManager.createUser(userBuilder.build());
            logger.debug("User created successfully: {} with role: {}", username, role);
            
            // Redirect to login page with success message
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            model.addAttribute("error", "Registration failed. Please try again.");
            return "register";
        }
    }

    @GetMapping("/api/skill-suggestions")
    @ResponseBody
    public List<String> skillSuggestions(@RequestParam("q") String query) {
        List<String> suggestions = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill.getName().toLowerCase().contains(query.toLowerCase())) {
                suggestions.add(skill.getName());
            }
        }
        return suggestions;
    }

    @GetMapping("/api/tutor-suggestions")
    @ResponseBody
    public List<String> tutorSuggestions(@RequestParam("q") String query) {
        List<String> suggestions = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill.getTutor().toLowerCase().contains(query.toLowerCase())) {
                if (!suggestions.contains(skill.getTutor())) {
                    suggestions.add(skill.getTutor());
                }
            }
        }
        return suggestions;
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        // Show only skills added by this user
        List<Skill> userSkills = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill.getTutor().equalsIgnoreCase(principal.getName())) {
                userSkills.add(skill);
            }
        }
        model.addAttribute("skills", userSkills);
        // Show meetings where user is tutor or learner
        List<Meeting> userMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (meeting.getTutor().equalsIgnoreCase(principal.getName()) ||
                (meeting.getLearner() != null && meeting.getLearner().equalsIgnoreCase(principal.getName()))) {
                userMeetings.add(meeting);
            }
        }
        model.addAttribute("meetings", userMeetings);
        return "profile";
    }

    @GetMapping("/your-meetings")
    public String yourMeetingsPage(Model model, Principal principal) {
        List<Meeting> userMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if ((meeting.getLearner() != null && meeting.getLearner().equalsIgnoreCase(principal.getName())) ||
                meeting.getTutor().equalsIgnoreCase(principal.getName())) {
                userMeetings.add(meeting);
            }
        }
        model.addAttribute("meetings", userMeetings);
        return "your-meetings";
    }

    @GetMapping("/messages")
    public String messagesPage(Model model, Principal principal) {
        List<String> messages = userMessages.getOrDefault(principal.getName(), new ArrayList<>());
        model.addAttribute("messages", messages);
        return "messages";
    }

    @GetMapping("/skills/{id}")
    public String viewSkill(@PathVariable Long id, Model model) {
        // Find the skill with the given ID
        Skill skill = null;
        for (Skill s : skills) {
            if (s.getId() != null && s.getId().equals(id)) {
                skill = s;
                break;
            }
        }

        if (skill == null) {
            // If skill not found, redirect to dashboard
            return "redirect:/dashboard";
        }

        // Add the skill to the model
        model.addAttribute("skill", skill);
        return "skill-details";
    }

    @GetMapping("/upcoming-sessions")
    public String upcomingSessions(Model model, Principal principal) {
        logger.info("Accessing upcoming sessions page for user: {}", principal.getName());
        List<Meeting> upcomingMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (meeting.getLearner() != null && 
                (meeting.getLearner().equalsIgnoreCase(principal.getName()) ||
                meeting.getTutor().equalsIgnoreCase(principal.getName()))) {
                upcomingMeetings.add(meeting);
            }
        }
        logger.info("Found {} upcoming meetings", upcomingMeetings.size());
        model.addAttribute("meetings", upcomingMeetings);
        model.addAttribute("username", principal.getName());
        model.addAttribute("messages", userMessages.getOrDefault(principal.getName(), new ArrayList<>()));
        return "/upcoming-sessions";
    }
}
