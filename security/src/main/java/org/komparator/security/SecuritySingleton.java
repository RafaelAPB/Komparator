package org.komparator.security;



import java.util.Date;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

public class SecuritySingleton {
	private static SecuritySingleton instance = new SecuritySingleton();
	private String name;
	private int wsI;
	private Date date;
	private SecuritySingleton() {
	}

	public static SecuritySingleton getInstance() {
		return instance;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getUDDI(String url) throws UDDINamingException {
		String otherName = "";
		UDDINaming uddiNaming = new UDDINaming("http://a45:BvDJb0AN@uddi.sd.rnl.tecnico.ulisboa.pt:9090");
		try {
			for (UDDIRecord uddiRecord : uddiNaming.listRecords("A45_Mediator" + "%")) {
				if (uddiRecord.getUrl().equals(url)) {
					otherName = uddiRecord.getOrgName();
				}
			}
		} catch (UDDINamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return otherName;
	}

	public int getWsI() {
		return wsI;
	}

	public void setWsI(int wsI) {
		this.wsI = wsI;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
