package test;

import gov.mvdis.m3.vil.service.PolViolationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import gov.mvdis.m3.vil.service.pub.VilTrafficRuleEntity;
public class PolViolationServiceTest {
    private gov.mvdis.m3.vil.service.pub.PolViolationService port = new PolViolationService()
	    .getPolViolationServiceImplPort();

    public List<VilTrafficRuleEntity> resList(HttpServletRequest req) {
	String action = req.getParameter("action");
	List<VilTrafficRuleEntity> list = new ArrayList<VilTrafficRuleEntity>();

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

	if ("saveViolation".equals(action)) {
		VilTrafficRuleEntity obj = newObj(req.getParameter("driverId"), gcBirthday, req.getParameter("plateNo"),
		    req.getParameter("carKind"), req.getParameter("ticket"), req.getParameter("resp_tp"),
		    req.getParameter("pay_way"), req.getParameter("vilInDate"), req.getParameter("rule1"),
		    req.getParameter("rule2"), req.getParameter("rule3"), req.getParameter("rule4"));
		list.add(port.savePolViolation(obj));
	    boolean b = false;//port.saveViolation(obj);
	    if (!b) {
		obj = new VilTrafficRuleEntity();
		obj.setVilTicket("false");
	    }
	    //list.add(obj);
	} else if ("searchViolationByPerson".equals(action))
	    list = port.searchViolationByPerson(req.getParameter("driverId"), gcBirthday);
	else if ("searchViolationByCar".equals(action))
	    list = port.searchViolationByCar(req.getParameter("plateNo"), req.getParameter("carKind"));
	else if ("searchCarViolation".equals(action))
	    list.add(port.searchCarViolation(req.getParameter("ticket")));
	return list;
    }

    private VilTrafficRuleEntity newObj(String driverId, XMLGregorianCalendar gcBirthday, String plateNo,
	    String carKind, String ticket, String respTp, String payWay, String vilInDate, String rule1, String rule2,
	    String rule3, String rule4) {

    VilTrafficRuleEntity obj = new VilTrafficRuleEntity();
	int[] intVilInDate = toIntArry(vilInDate);
	XMLGregorianCalendar gcVilInDate = null;
	XMLGregorianCalendar gcVilDate = null;
	XMLGregorianCalendar gcArrivedDate = null;
	XMLGregorianCalendar gcSuspBackDate = null;
	try {
	    gcVilInDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
		    new GregorianCalendar(intVilInDate[0], intVilInDate[1] - 1, intVilInDate[2]));
	    GregorianCalendar gcNow = new GregorianCalendar();
	    gcNow.setTime(new Date());
	    gcVilDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcNow);
	    gcArrivedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2014, 10, 1));
	    gcSuspBackDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(2002, 2, 2));
	} catch (DatatypeConfigurationException e) {
	    e.printStackTrace();
	}
	int rule1No = 0;
	int rule2No = 0;
	int rule3No = 0;
	int rule4No = 0;
	if (rule1 != null && !rule1.isEmpty())
	    rule1No = Integer.valueOf(rule1);
	if (rule2 != null && !rule2.isEmpty())
	    rule2No = Integer.valueOf(rule2);
	if (rule3 != null && !rule3.isEmpty())
	    rule3No = Integer.valueOf(rule3);
	if (rule4 != null && !rule4.isEmpty())
	    rule4No = Integer.valueOf(rule4);

	obj.setDriverId(driverId);
	obj.setDriverBirthday(gcBirthday);
	obj.setPlateNo(plateNo);
	obj.setCarKindNo(carKind);
	obj.setVilTicket(ticket);
	obj.setRespPeopleType(String.valueOf(respTp.toCharArray()[0]));
	obj.setPayWayNo(String.valueOf(payWay.toCharArray()[0]));
	obj.setInDate(gcVilInDate);
	obj.setVilDate(gcVilDate);
	obj.setArriveDate(gcArrivedDate);
//	obj.setSuspBackDate(gcSuspBackDate);
	obj.setPayment(20000000);
	obj.setPenalty(40000000);
	obj.setRule1No(rule1No);
	obj.setRule2No(rule2No);
	obj.setRule3No(rule3No);
	obj.setRule4No(rule4No);
	obj.setCloseNo(String.valueOf("closeNo".toCharArray()[0]));
	obj.setDmv("dmv");
	obj.setVilLocation("location");
	obj.setAccuseType(String.valueOf("type".toCharArray()[0]));
	obj.setVilKindNo(String.valueOf("vilType".toCharArray()[0]));
	obj.setInUserID("inuserId");
	obj.setAccuseType("1");
	obj.setVehKindNo(carKind);
	return obj;
    }

    private int[] toIntArry(String s) {
	String[] ss = s.split("\\.");
	int[] si = new int[ss.length];
	for (int i = 0; i < si.length; i++)
	    si[i] = Integer.valueOf(ss[i]);
	return si;
    }
}
