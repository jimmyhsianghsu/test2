package test;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import gov.mvdis.m3.vil.service.TpeAdjViolationService;
public class AdjViolationServiceTest {
	private gov.mvdis.m3.vil.service.pub.TpeAdjViolationService port = new TpeAdjViolationService().getTpeAdjViolationServiceImplPort();
	public String resList(HttpServletRequest req) {
		String action = req.getParameter("action");
		if ("searchAdjViolationByPerson".equals(action))
			return action+"="+(printBean(port.searchAdjViolationByPerson(null,null,null)));
		else if ("adjustViolation".equals(action))
			return action+"="+(printBean(port.adjustViolation(null)));
		else if ("receiveAdjViolation".equals(action))
			return action+"="+(printBean(port.receiveAdjViolation(null)));
		else if ("searchAdjViolationByCar".equals(action))
			return action+"="+(printBean(port.searchAdjViolationByCar(null,null,null)));
		else if ("searchAdjRemedyByTicket".equals(action))
			return action+"="+(printBean(port.searchAdjRemedyByTicket(null,null,null)));
		else if ("searchAdjPermByPerson".equals(action))
			return action+"="+(printBean(port.searchAdjPermByPerson(null,null)));
		else if ("searchAdjPayment".equals(action))
			return action+"="+(printBean(port.searchAdjPayment(null,null,null)));
		else if ("searchAdjViolationByTicket".equals(action))
			return action+"="+(printBean(port.searchAdjViolationByTicket(null,null,null)));
		return action+"="+"error";
	}
	private String printBean(@SuppressWarnings("rawtypes") List list){
		StringBuffer beanSb = new StringBuffer('\n');
		if (list != null){
			int number = 0;
			for (Object bean : list){
				number++;
				try {
					for (PropertyDescriptor propdesc : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()){
						if (propdesc.getReadMethod() != null && !"class".equals(propdesc.getName())){
							beanSb.append(number + "." + propdesc.getName() + "=" + propdesc.getReadMethod().invoke(bean) + '\n');
						}
					}
				} catch (IntrospectionException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
				}
			}
		}
		return beanSb.toString();
	}
	private String printBean(Object bean){
		StringBuffer beanSb = new StringBuffer('\n');
		if (bean != null){
			try {
				for (PropertyDescriptor propdesc : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()){
					if (propdesc.getReadMethod() != null && !"class".equals(propdesc.getName())){
						beanSb.append(bean + "." + propdesc.getName() + "=" + propdesc.getReadMethod().invoke(bean) + '\n');
					}
				}
			} catch (IntrospectionException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return beanSb.toString();
	}
}