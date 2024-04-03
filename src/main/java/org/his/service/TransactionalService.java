package org.his.service;

import org.his.entity.Login;
import org.his.entity.user.Doctor;
import org.his.entity.user.Nurse;
import org.his.entity.user.Pharma;
import org.his.entity.user.Receptionist;
import org.his.repo.LoginRepo;
import org.his.repo.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionalService {

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private NurseRepo nurseRepo;

    @Autowired
    private PharmaRepo pharmaRepo;

    @Autowired
    private ReceptionistRepo receptionRepo;

    @Transactional
    void insertNewDoctor(Login account, Doctor doc) {
        loginRepo.save(account);
        doctorRepo.save(doc);
    }

    @Transactional
    void insertNewNurse(Login account, Nurse nur) {
        loginRepo.save(account);
        nurseRepo.save(nur);
    }

    @Transactional
    void insertNewReceptionist(Login account, Receptionist recep) {
        loginRepo.save(account);
        receptionRepo.save(recep);
    }

    @Transactional
    void insertNewPharma(Login account, Pharma pharma) {
        loginRepo.save(account);
        pharmaRepo.save(pharma);
    }

}