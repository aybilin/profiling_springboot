package com.profiling.profiling_project.service;

import com.profiling.profiling_project.model.UserProfile;
import com.profiling.profiling_project.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Sauvegarder les profils dans la base de données.
     * @param profiles Carte des profils à sauvegarder.
     */
    public void saveProfilesToDatabase(Map<String, UserProfile> profiles) {
        userProfileRepository.saveAll(profiles.values());
    }
}
