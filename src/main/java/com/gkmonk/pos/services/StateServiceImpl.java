package com.gkmonk.pos.services;

import com.gkmonk.pos.model.ReportDetails;
import com.gkmonk.pos.model.State;
import com.gkmonk.pos.repo.StateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateServiceImpl {

    @Autowired
    private StateRepo stateRepo;

    public void updateStateCodes(List<ReportDetails> reportDetails){

            List<State> states = stateRepo.findAll();
            for (ReportDetails reportDetail : reportDetails) {
                for (State state : states) {
                    if (reportDetail.getBillingRegion().equalsIgnoreCase(state.getState())) {
                        reportDetail.setStateCode(state.getCode());
                    }
                }
            }
    }
}
