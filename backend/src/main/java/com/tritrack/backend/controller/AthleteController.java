package com.tritrack.backend.controller;

import com.tritrack.backend.dto.AthleteResponse;
import com.tritrack.backend.dto.LoginRequest;
import jakarta.validation.Valid;
import com.tritrack.backend.athlete.Athlete;
import com.tritrack.backend.dto.RegisterAthleteRequest;
import com.tritrack.backend.service.AthleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/athletes")
public class AthleteController {
    private final AthleteService athleteService;

    public AthleteController(AthleteService athleteService){
        this.athleteService = athleteService;
    }

    @PostMapping
    public AthleteResponse createAthlete(@Valid @RequestBody RegisterAthleteRequest request){
        return athleteService.saveAthlete(request);
    }
    @GetMapping
    public List<AthleteResponse> getAthletes(){
        return athleteService.getAllAthletes();
    }
    @GetMapping("/{id}")
    public AthleteResponse getAthleteById(@PathVariable Long id){
        return athleteService.getAthleteById(id);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request) {
        String token = athleteService.login(request);

        return ResponseEntity.ok(token);
    }

}
