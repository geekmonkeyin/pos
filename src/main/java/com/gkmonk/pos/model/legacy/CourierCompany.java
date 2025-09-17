package com.gkmonk.pos.model.legacy;

public enum CourierCompany {

	SHIPMOZO("shipmozo"),MARUTI("maruti"),EKART("Ekart Logistics"),DTDC("DTDC"),BLUEDART("Bluedart"),DELHIVERY("Delhivery"),DEFAULT("DEFAULT"),@Deprecated SHIPCLUES("Shipclues");
	
	private String value;
	CourierCompany(String value) {
		this.value = value;
	}
	
	public static String getCourierCompanyName(String courierName) {
		
			for(CourierCompany company : CourierCompany.values()) {
					if(company.value.equalsIgnoreCase(courierName) || company.name().equalsIgnoreCase(courierName)) {
						return company.value;
					}
			}
			return BLUEDART.value;
	}

	public static CourierCompany getCourierName(String courierName){
		for(CourierCompany company : CourierCompany.values()) {
					if(company.value.equalsIgnoreCase(courierName)) {
						return company;
					}
			}
			return BLUEDART;
	}
}
