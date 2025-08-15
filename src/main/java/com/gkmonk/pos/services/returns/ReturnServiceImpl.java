package com.gkmonk.pos.services.returns;

import com.gkmonk.pos.model.returns.ReturnOrder;
import com.gkmonk.pos.repo.returns.ReturnRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnServiceImpl {

    @Autowired
    private ReturnRepo returnServiceRepo;

    public void saveReturnOrder(ReturnOrder returnOrder){
        returnServiceRepo.save(returnOrder);
    }
}
