package com.example.theatrondemo;

import java.util.ArrayList;
import java.util.List;

public class AppUser {
    private String id;
    private String display;
    private String firstName;
    private String lastName;
    private String profilePic;
    private String currentCity;
    private String homeTown;
    private String profession;
    private String qualification;
    private List<String> liked;
    private List<String> commented;
    private List<String> shared;
    private List<String> posted;

    public AppUser() {
        liked = new ArrayList<>();
        commented = new ArrayList<>();
        shared = new ArrayList<>();
        posted = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public List<String> getLiked() {
        return liked;
    }

    public void setLiked(List<String> liked) {
        this.liked = liked;
    }

    public List<String> getCommented() {
        return commented;
    }

    public void setCommented(List<String> commented) {
        this.commented = commented;
    }

    public List<String> getShared() {
        return shared;
    }

    public void setShared(List<String> shared) {
        this.shared = shared;
    }

    public List<String> getPosted() {
        return posted;
    }

    public void setPosted(List<String> posted) {
        this.posted = posted;
    }
}
