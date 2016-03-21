package test;

import gov.mvdis.m3.batch.service.CoreBatchServiceClient;
import gov.mvdis.m3.batch.service.pub.ServiceMapEntry;
import gov.mvdis.m3.batch.service.pub.ServiceMapEntryArray;
import gov.mvdis.m3.batch.service.pub.StartJob;
import gov.mvdis.m3.vil.service.AdjViolationService;
import gov.mvdis.m3.vil.service.EmvViolationService;
import gov.mvdis.m3.vil.service.PhoneViolationService;
import gov.mvdis.m3.vil.service.PolViolationService;
import gov.mvdis.m3.vil.service.TpeAdjViolationService;
import gov.mvdis.m3.vil.service.pub.EmvViolationOuterObject;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceTest extends HttpServlet {
    private void setWsdlUrl(String url) {
	ResourceBundle rb = ResourceBundle.getBundle("/META-INF/bundle/wsdl");
	URL urlPol = null;
	URL urlEmv = null;
	URL urlBat = null;
	URL urlPne = null;
	URL urlAdj = null;
	URL urlTpe = null;
	try {
	    urlPol = new URL(rb.getString("pol." + url));
	    urlEmv = new URL(rb.getString("emv." + url));
	    urlBat = new URL(rb.getString("bat." + url));
	    urlPne = new URL("http://localhost:8081/m3-service/vil/vil001/s007?wsdl");
	    urlAdj = new URL("http://localhost:8081/m3-service/vil/vil001/s006?wsdl");
	    urlTpe = new URL("http://localhost:8081/m3-service/vil/vil001/s008?wsdl");
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
	PolViolationService.WSDL_LOCATION = urlPol;
	EmvViolationService.WSDL_LOCATION = urlEmv;
	CoreBatchServiceClient.WSDL_LOCATION = urlBat;
	PhoneViolationService.WSDL_LOCATION = urlPne;
	AdjViolationService.WSDL_LOCATION = urlAdj;
	TpeAdjViolationService.WSDL_LOCATION = urlTpe;

	java.net.Authenticator.setDefault(new java.net.Authenticator(){
		@Override
		protected java.net.PasswordAuthentication getPasswordAuthentication() {
			return new java.net.PasswordAuthentication("sys.ws.emv", "mer@pass".toCharArray());
		}
	});
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	res.setContentType("text/html; charset=utf8");
	setWsdlUrl(req.getParameter("url"));
	String service = req.getParameter("service");

	JSONObject jobj = new JSONObject();
	List list = null;
	try {
	    if ("PolViolationService".equals(service))
		list = new PolViolationServiceTest().resList(req);
	    else if ("EmvViolationService".equals(service))
		list = new EmvViolationServiceTest().resList(req);
	    else if ("PhoneViolationService".equals(service))
		list = new PhoneViolationServiceTest().resList(req);
	    else if ("AdjViolationService".equals(service))
	    res.getWriter().println(new AdjViolationServiceTest().resList(req));
	    else if ("TpeAdjViolationService".equals(service))
	    res.getWriter().println(new AdjViolationServiceTest().resList(req));
	    getResList(jobj, list);
	} catch (Exception e) {
	    e.printStackTrace();
	    try {
		jobj.put("errMsg", e.getMessage());
	    } catch (JSONException e1) {
		e1.printStackTrace();
	    }
	}
	res.getWriter().println(jobj.toString());
	System.out.println(testCoreBatchService());
    }

    private void getResList(JSONObject jobj, List list) throws JSONException, IllegalArgumentException,
	    IntrospectionException, IllegalAccessException, InvocationTargetException {
	JSONArray jArry = new JSONArray();
	if (list != null)
	    for (Object o : list)
		if (o instanceof EmvViolationOuterObject) {
		    JSONObject jobj1 = new JSONObject();
		    for (PropertyDescriptor pd : Introspector.getBeanInfo(EmvViolationOuterObject.class)
			    .getPropertyDescriptors())
			if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
			    Object o1 = pd.getReadMethod().invoke(o);
			    if (o1 instanceof List) {
				JSONArray jArry1 = new JSONArray();
				for (Object o2 : (List) o1)
				    jArry1.put(new JSONObject(o2));
				jobj1.put(pd.getName(), jArry1.toString());
			    } else
				jobj1.put(pd.getName(), o1);
			}
		    jArry.put(jobj1);
		} else
		    jArry.put(new JSONObject(o));
	jobj.put("rows", jArry);
    }

    private boolean testCoreBatchService() {
	ServiceMapEntryArray ar = new ServiceMapEntryArray();
	ServiceMapEntry en = new ServiceMapEntry();
	en.setKey("vilInDate");
	en.setValue(new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date()));
	List<ServiceMapEntry> li = new ArrayList<ServiceMapEntry>();
	li.add(en);
	ar.item = li;
	StartJob.Arg2 arg2 = new StartJob.Arg2();
	List<StartJob.Arg2.Entry> list = new ArrayList<StartJob.Arg2 .Entry>();
	StartJob.Arg2.Entry sjen = new StartJob.Arg2.Entry();
	sjen.setKey("mei.file.list");
	sjen.setValue("D:/POLTestData/EA10210140.A.Q");
	StartJob.Arg2.Entry sjent = new StartJob.Arg2.Entry();
	sjent.setKey("timestamp");
	sjent.setValue(System.currentTimeMillis());
	StartJob.Arg2.Entry sjenc = new StartJob.Arg2.Entry();
	sjenc.setKey("mei.case.no");
	sjenc.setValue("TSTCASENO001");
	list.add(sjen);
	list.add(sjent);
	list.add(sjenc);
	arg2.entry=list;
	try{return new CoreBatchServiceClient().getCoreBatchServiceImplPort().startJob("jobPOL003", "", arg2);}catch(Exception e){e.printStackTrace();return false;}
    }
}