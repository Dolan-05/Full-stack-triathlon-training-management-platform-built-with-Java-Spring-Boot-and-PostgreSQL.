package com.tritrack.backend.service;

import com.tritrack.backend.athlete.Athlete;
import com.tritrack.backend.dto.AthleteResponse;
import com.tritrack.backend.dto.LoginRequest;
import com.tritrack.backend.dto.RegisterAthleteRequest;
import com.tritrack.backend.exception.InvalidCredentialsException;
import com.tritrack.backend.repository.AthleteRepository;
import com.tritrack.backend.exception.AthleteNotFoundException;
import com.tritrack.backend.exception.EmailAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AthleteService(AthleteRepository athleteRepository, PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.athleteRepository = athleteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public AthleteResponse saveAthlete(RegisterAthleteRequest request){
        Optional<Athlete> existingAthlete =
                athleteRepository.findByEmail(request.getEmail());

        if(existingAthlete.isPresent()){
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        Athlete athlete = new Athlete();

        athlete.setFirstName(request.getFirstName());
        athlete.setLastName(request.getLastName());
        athlete.setEmail(request.getEmail());
        athlete.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        Athlete savedAthlete = athleteRepository.save(athlete);

        AthleteResponse response = new AthleteResponse();

        response.setId(savedAthlete.getId());
        response.setFirstName(savedAthlete.getFirstName());
        response.setLastName(savedAthlete.getLastName());
        response.setEmail(savedAthlete.getEmail());
        response.setHeightCm(savedAthlete.getHeightCm());
        response.setWeightKg(savedAthlete.getWeightKg());

        return response;
    }

    public AthleteResponse getAthleteById(Long id){
        Optional<Athlete> existingAthlete=
                athleteRepository.findById(id);

        if(existingAthlete.isEmpty()) {
            throw new AthleteNotFoundException("Athlete not found");
        }
        Athlete athlete = existingAthlete.get();

        AthleteResponse response = new AthleteResponse();

        response.setId(athlete.getId());
        response.setFirstName(athlete.getFirstName());
        response.setLastName(athlete.getLastName());
        response.setEmail(athlete.getEmail());
        response.setHeightCm(athlete.getHeightCm());
        response.setWeightKg(athlete.getWeightKg());

        return response;

    }

    public String login(LoginRequest request){
        Optional<Athlete> existingAthlete =
                athleteRepository.findByEmail(request.getEmail());

        if(existingAthlete.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        Athlete athlete = existingAthlete.get();
        if(!passwordEncoder.matches(request.getPassword(), athlete.getPasswordHash())){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return jwtService.generateToken(athlete.getEmail());

    }

    public List<AthleteResponse> getAllAthletes(){
        List<Athlete> athletes = athleteRepository.findAll();
        List<AthleteResponse> responses = new ArrayList<>();

        for(Athlete athlete : athletes){
            AthleteResponse response = new AthleteResponse();

            response.setId(athlete.getId());
            response.setFirstName(athlete.getFirstName());
            response.setLastName(athlete.getLastName());
            response.setEmail(athlete.getEmail());
            response.setHeightCm(athlete.getHeightCm());
            response.setWeightKg(athlete.getWeightKg());

            responses.add(response);
        }
        return responses;
    }
}
