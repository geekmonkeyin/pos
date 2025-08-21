package com.gkmonk.pos.model;

import com.gkmonk.pos.utils.InboundStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document("inbound")
public class InboundData {

    private String vendorName;
    @Id
    private long id;
    private InboundStatus status;
    private String receivedDate;
    private Integer numberOfBoxes;
    private double totalPurchaseAmount;
    private Map<Integer,List<CartonDetails>> cartonDetails;

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    private String closedBy;

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Integer getNumberOfBoxes() {
        return numberOfBoxes;
    }

    public void setNumberOfBoxes(Integer numberOfBoxes) {
        this.numberOfBoxes = numberOfBoxes;
    }

    public Map<Integer, List<CartonDetails>> getCartonDetails() {
        return cartonDetails;
    }

    public void setCartonDetails(Map<Integer, List<CartonDetails>> cartonDetails) {
        this.cartonDetails = cartonDetails;
    }

    public InboundStatus getStatus() {
        return status;
    }

    public void setStatus(InboundStatus status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }
    public void setTotalPurchaseAmount(double totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }
}
