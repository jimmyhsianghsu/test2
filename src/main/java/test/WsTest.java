package test;
import gov.mvdis.m3.vil.service.pub.VilTrafficRuleEntity;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
@SuppressWarnings("serial")
public class WsTest extends HttpServlet {
	private void setWsdlUrl(String url) {
		ResourceBundle rb = ResourceBundle.getBundle("/META-INF/bundle/wsdl");
		URL urlPol = null;
		try{urlPol = new URL(url);}catch(MalformedURLException e){e.printStackTrace();}//rb.getString("pol." + url)
		gov.mvdis.m3.vil.service.PolViolationService.WSDL_LOCATION = urlPol;
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		setWsdlUrl(req.getParameter("url"));
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=utf8");
		VilTrafficRuleEntity vilEntity = genBean(VilTrafficRuleEntity.class,req);
		res.getWriter().print(printBean(vilEntity));
		gov.mvdis.m3.vil.service.pub.PolViolationService port = new gov.mvdis.m3.vil.service.PolViolationService().getPolViolationServiceImplPort();
		String b = null;
		try{b = String.valueOf(port.saveViolation(vilEntity));}catch(Exception e){b=e.getMessage();e.printStackTrace();}
		res.getWriter().print(b);
	}
	private <T> T genBean(Class<T> clz,HttpServletRequest req){
		T bean=null;
		try {
			bean = clz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		Enumeration<String> names = req.getParameterNames();
		while(names.hasMoreElements()){
			String name=names.nextElement();
			String value=req.getParameter(name);
			boolean flag=false;
			try {
				for (PropertyDescriptor propdesc : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
					if (propdesc.getWriteMethod() != null && propdesc.getReadMethod()!=null && name.equals(propdesc.getName())){
						Class<? extends Object> type = propdesc.getReadMethod().getReturnType();
						if(type.equals(String.class))
							propdesc.getWriteMethod().invoke(bean, value);
						else if(type.equals(XMLGregorianCalendar.class))
							if(!name.equals("vilDate"))
								propdesc.getWriteMethod().invoke(bean, rocDate(value));
							else
								propdesc.getWriteMethod().invoke(bean, rocDate(value, req.getParameter("vilTime")));
						else if(type.equals(int.class))
							if(!name.equals("accuseType") && !name.equals("vilKindNo") && !name.equals("respPeopleType") && !name.equals("payWayNo") && !name.equals("closeNo") && !name.equals("isAdjudged"))
								propdesc.getWriteMethod().invoke(bean, toInt(value));
							else
								propdesc.getWriteMethod().invoke(bean, toChar(value));
						else if(type.equals(boolean.class))
							propdesc.getWriteMethod().invoke(bean, Boolean.valueOf(value));
						flag=true;
						break;
					}
				}
			} catch (Exception e) {
				System.out.println(name+"="+value+":"+e.getMessage());
				e.printStackTrace();
			}
			if(!flag)
				System.out.println(name+"="+value);
		}
		return bean;
	}
	private XMLGregorianCalendar rocDate(String date) throws NumberFormatException, DatatypeConfigurationException {
		if (date != null && date.trim().length() == 7) {
			date = date.trim();
			String year = date.substring(0, 3);
			String month = date.substring(3, 5);
			String day = date.substring(5, 7);
			year = String.valueOf(Integer.valueOf(year) + 1911);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day)));
		}
		return null;
	}
	private XMLGregorianCalendar rocDate(String date, String time) throws NumberFormatException, DatatypeConfigurationException {
		if (date != null && time!= null && date.trim().length() == 7 && time.trim().length()==4) {
			date = date.trim();
			time = time.trim();
			String year = date.substring(0, 3);
			String month = date.substring(3, 5);
			String day = date.substring(5, 7);
			String min = time.substring(0,2);
			String sec = time.substring(2, 4);
			year = String.valueOf(Integer.valueOf(year) + 1911);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day), Integer.valueOf(min), Integer.valueOf(sec)));
		}
		return null;
	}
	private int toInt(String str) {
		int n=0;
		if (str != null && !str.trim().isEmpty())
			try{
				n=Integer.valueOf(str.trim());
			}catch(NumberFormatException nu){
				n=str.charAt(0);
			}
		return n;
	}
	private int toChar(String str){return str!=null&&!str.trim().isEmpty()?str.charAt(0):0;}
	public String printBean(Object bean){
		StringBuffer beanSb = new StringBuffer('\n');
		if (bean != null){
			try {
				for (PropertyDescriptor propdesc : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors())
					if (propdesc.getReadMethod() != null && !"class".equals(propdesc.getName()))
						beanSb.append(bean + "." + propdesc.getName() + "=" + propdesc.getReadMethod().invoke(bean) + '\n');
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IntrospectionException e) {
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