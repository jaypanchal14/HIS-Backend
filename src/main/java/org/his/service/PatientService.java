package org.his.service;

import lombok.extern.slf4j.Slf4j;
import org.his.repo.user.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PatientService {

    @Autowired
    private PatientRepo patientRepo;


}