package test;

import gov.mvdis.m3.vil.service.EmvViolationService;
import gov.mvdis.m3.vil.service.pub.EmvViolationOuterObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class EmvViolationServiceTest {
    private gov.mvdis.m3.vil.service.pub.EmvViolationService port = new EmvViolationService()
	    .getEmvViolationServiceImplPort();

    public List<EmvViolationOuterObject> resList(HttpServletRequest req) {
	String action = req.getParameter("action");
	List<EmvViolationOuterObject> list = new ArrayList<EmvViolationOuterObject>();

	XMLGregorianCalendar gcBirthday = null;
	if (!req.getParameter("birthday").isEmpty()) {
	    int[] intBirthday = toIntArry(req.getParameter("birthday"));
	    try {
		gcBirthday = DatatypeFactory.newInstance().newXMLGregorianCalendar(
		        new GregorianCalendar(intBirthday[0], intBirthday[1] - 1, intBirthday[2]));
	    } catch (DatatypeConfigurationException e) {
		e.printStackTrace();
	    }
	}

	if ("searchPenaltyByNaturalPerson".equals(action))
	    list = port.searchPenaltyByNaturalPerson(req.getParameter("driverId"), gcBirthday,Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchPenaltyByLegalPerson".equals(action))
	    list = port.searchPenaltyByLegalPerson(req.getParameter("driverId"),Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchPenaltyByNaturalPersonRestrict".equals(action))
	    list = port.searchPenaltyByNaturalPersonRestrict(req.getParameter("driverId"), gcBirthday,Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchPenaltyByLegalPersonRestrict".equals(action))
		list = port.searchPenaltyByLegalPersonRestrict(req.getParameter("driverId"),Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchSuspDriverLicence".equals(action))
	    list = port.searchSuspDriverLicence(req.getParameter("driverId"), gcBirthday);
	else if ("searchSuspPlate".equals(action))
	    list = port.searchSuspPlate(req.getParameter("plateNo"), req.getParameter("carKind"));
	else if ("searchMarkByDriver".equals(action))
	    list = port.searchMarkByDriver(req.getParameter("driverId"), gcBirthday);
	else if ("searchMarkByNaturalPersonPage".equals(action))
	    list = port.searchMarkByNaturalPersonPage(req.getParameter("driverId"), gcBirthday,Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchMarkByLegalPersonPage".equals(action))
	    list = port.searchMarkByLegalPersonPage(req.getParameter("driverId"),Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchPaymentByNaturalPerson".equals(action))
	    list = port.searchPaymentByNaturalPerson(req.getParameter("driverId"), null,Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchPaymentByLegalPerson".equals(action))
	    list = port.searchPaymentByLegalPerson(req.getParameter("driverId"),Integer.valueOf(req.getParameter("rowStart")), Integer.valueOf(req.getParameter("rowEnd")));
	else if ("searchPenaltyByNaturalPersonCount".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchPenaltyByNaturalPersonCount(req.getParameter("driverId"),gcBirthday)));
	    list.add(o);
	} else if ("searchPenaltyByLegalPersonCount".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchPenaltyByLegalPersonCount(req.getParameter("driverId"))));
	    list.add(o);
	} else if ("searchPenaltyByNaturalPersonCountRestrict".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchPenaltyByNaturalPersonCountRestrict(req.getParameter("driverId"),gcBirthday)));
	    list.add(o);
	} else if ("searchPenaltyByLegalPersonCountRestrict".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchPenaltyByLegalPersonCountRestrict(req.getParameter("driverId"))));
	    list.add(o);
	} else if ("searchMarkByNaturalPersonCount".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchMarkByNaturalPersonCount(req.getParameter("driverId"), gcBirthday)));
	    list.add(o);
	} else if ("searchMarkByLegalPersonCount".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchMarkByLegalPersonCount(req.getParameter("driverId"))));
	    list.add(o);
	} else if ("searchPaymentByNaturalPersonCount".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchPaymentByNaturalPersonCount(req.getParameter("driverId"),gcBirthday)));
	    list.add(o);
	} else if ("searchPaymentByLegalPersonCount".equals(action)) {
	    EmvViolationOuterObject o = new EmvViolationOuterObject();
	    o.setVilTicket(String.valueOf(port.searchPaymentByLegalPersonCount(req.getParameter("driverId"))));
	    list.add(o);
	}else if("updateViolation".equals(action)){
		EmvViolationOuterObject o = new EmvViolationOuterObject();
		o.setVilTicket(req.getParameter("ticket"));
		o.setPlateNo(req.getParameter("plateNo"));
		o.setVehKind(req.getParameter("carKind"));
		o.setPenalty(Integer.valueOf(req.getParameter("rule1")));
		o.setPayment(Integer.valueOf(req.getParameter("rule2")));
		o.setDriverId(req.getParameter("driverId"));
		o.setPayWay(req.getParameter("pay_way"));
		o.setDriverId(String.valueOf(port.updateViolation(o)));
		list.add(o);
	}else if("searchViolationByTicketPerson".equals(action))
	    list.add(port.searchViolationByTicketPerson(req.getParameter("ticket"),req.getParameter("driverId"),null));
    else if("searchVehViolationCount".equals(action)){
    	EmvViolationOuterObject o = new EmvViolationOuterObject();
    	o.setVilTicket(String.valueOf(port.searchVehViolationCount(req.getParameter("plateNo"),req.getParameter("carKind"))));
	    list.add(o);
    }
	else if("searchDriverMarkPoint".equals(action))System.out.println("searchDriverMarkPoint=>"+port.searchDriverMarkPoint("B100307748", null, "1"));
	return list;
    }

    private int[] toIntArry(String s) {
	String[] ss = s.split("\\.");
	int[] si = new int[ss.length];
	for (int i = 0; i < si.length; i++)
	    si[i] = Integer.valueOf(ss[i]);
	return si;
    }
}
