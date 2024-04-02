package org.his.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.his.bean.PharmaHistoryResp;
import org.his.bean.PrescriptionItem;
import org.his.config.Roles;
import org.his.entity.Login;
import org.his.entity.Prescription;
import org.his.exception.AuthenticationException;
import org.his.repo.LoginRepo;
import org.his.repo.PrescriptionRepo;
import org.his.repo.user.PharmaRepo;
import org.his.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PharmaService {

    @Autowired
    private LoginRepo loginRepo;

    @Autowired
    private PharmaRepo pharmaRepo;

    @Autowired
    private PrescriptionRepo prescriptionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    public PharmaHistoryResp getPharmaHistory(String pharmaId, String startDate, String endDate) {
        PharmaHistoryResp resp = new PharmaHistoryResp();
        List<PrescriptionItem> items = new ArrayList<>();
        boolean isAnyEmpty = false;
        try{
            //Validate the request-fields
            if(pharmaId == null || pharmaId.isEmpty()){
                throw new AuthenticationException("Empty pharmaId passed in the request.");
            }

            if(validate(startDate, endDate)){
                isAnyEmpty = true;
            }

            //Check if pharma is active
            Optional<Login> optAcc = loginRepo.checkIfUserIsActive(pharmaId, String.valueOf(Roles.PHARMACIST));
            if(optAcc.isEmpty()){
                throw new AuthenticationException("PharmaId is either inActive or doesn't exist.");
            }

            if(isAnyEmpty){
                List<Prescription> ls = prescriptionRepo.findByDateAfter(Utility.getOffSetDateOf30Days());
                for(Prescription item: ls ){
                    items.add(getDtoFromBean(item));
                }
            }else{
                //startDate and endDate contains valid date or not, if not -> use last 30 days as default
                OffsetDateTime startD = Utility.getOffSetStartDateFromString(startDate);
                OffsetDateTime endD = Utility.getOffSetEndDateFromString(endDate);

                List<Prescription> ls = prescriptionRepo.findByDateBetween(startD, endD);
                for(Prescription item: ls ){
                    items.add(getDtoFromBean(item));
                }
            }
            resp.setResponse(items);

        } catch (AuthenticationException e){
            log.error("AuthenticationException while fetching pharmaHistory : "+e.getMessage());
            resp.setError(e.getMessage());
        } catch (Exception e){
            log.error("Exception while fetching pharmaHistory : "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }

    private PrescriptionItem getDtoFromBean(Prescription obj) throws JsonProcessingException {
        PrescriptionItem item = new PrescriptionItem();
        item.setName(obj.getPatientId());
        item.setDiagnosisId(obj.getDiagnosisId());
        item.setMedicine(objectMapper.readValue(obj.getMedicine(), Map.class));
        item.setDate(Utility.getFormattedOffsetTime(obj.getDate()));
        return item;
    }

    private boolean validate(String startDate, String endDate) {
        if(startDate == null || startDate.isEmpty()){
            return true;
        }
        return endDate == null || endDate.isEmpty();
    }

    public PharmaHistoryResp getOneFromDiagnosisId(String pharmaId, String diagnosisId) {
        PharmaHistoryResp resp = new PharmaHistoryResp();
        List<PrescriptionItem> items = new ArrayList<>();
        try{
            //Validate the request-fields
            if(pharmaId == null || pharmaId.isEmpty()){
                throw new AuthenticationException("Empty pharmaId passed in the request.");
            }
            if(diagnosisId == null || diagnosisId.isEmpty()){
                throw new Exception("Empty diagnosisId passed in the request");
            }

            //Check if pharma is active
            Optional<Login> optAcc = loginRepo.checkIfUserIsActive(pharmaId, String.valueOf(Roles.PHARMACIST));
            if(optAcc.isEmpty()){
                throw new AuthenticationException("PharmaId is either inActive or doesn't exist.");
            }

            Optional<Prescription> optPres = prescriptionRepo.findByDiagnosisId(diagnosisId);
            if(optPres.isEmpty()){
                throw new Exception("No such diagnosis found in the database");
            }
            items.add(getDtoFromBean(optPres.get()));
            resp.setResponse(items);

        } catch (AuthenticationException e){
            log.error("AuthenticationException while fetching pharmaHistory : "+e.getMessage());
            resp.setError(e.getMessage());
        } catch (Exception e){
            log.error("Exception while fetching pharmaHistory : "+e.getMessage());
            resp.setError(e.getMessage());
        }
        return resp;
    }
}