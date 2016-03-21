package test;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import gov.mvdis.m3.vil.service.PhoneViolationService;
import gov.mvdis.m3.vil.service.pub.EmvViolationOuterObject;
public class PhoneViolationServiceTest {
	private gov.mvdis.m3.vil.service.pub.PhoneViolationService port = new PhoneViolationService().getPhoneViolationServiceImplPort();
	public List<EmvViolationOuterObject> resList(HttpServletRequest req) {
		String action = req.getParameter("action");
		List<EmvViolationOuterObject> list = new ArrayList<EmvViolationOuterObject>();
		EmvViolationOuterObject obj = new EmvViolationOuterObject();
		list.add(obj);
		XMLGregorianCalendar gcBirthday = null;
		if (!req.getParameter("birthday").isEmpty()) {
			int[] intBirthday = toIntArry(req.getParameter("birthday"));
			try {
				gcBirthday = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(intBirthday[0], intBirthday[1] - 1, intBirthday[2]));
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		}
		if ("searchTotalPenaltyByNaturalPerson".equals(action))
			obj.setVilTicket(String.valueOf(port.searchTotalPenaltyByNaturalPerson(req.getParameter("driverId"), gcBirthday)));
		else if ("searchTotalPenaltyByNaturalPersonRestrict".equals(action))
			obj.setVilTicket(String.valueOf(port.searchTotalPenaltyByNaturalPersonRestrict(req.getParameter("driverId"), gcBirthday)));
		else if ("searchTotalPenaltyByLegalPerson".equals(action))
			obj.setVilTicket(String.valueOf(port.searchTotalPenaltyByLegalPerson(req.getParameter("driverId"))));
		else if ("searchTotalPenaltyByLegalPersonRestrict".equals(action))
			obj.setVilTicket(String.valueOf(port.searchTotalPenaltyByLegalPersonRestrict(req.getParameter("driverId"))));
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