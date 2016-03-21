package interceptor;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
public class OutSoapInterceptor extends AbstractSoapInterceptor{
	public OutSoapInterceptor() {
		super(Phase.PRE_PROTOCOL);
		System.out.println("OutSoapInterceptor.construct");
	}
	public void handleMessage(SoapMessage msg) throws Fault {
		System.out.println("OutSoapInterceptor.handleMessage");
	}
}