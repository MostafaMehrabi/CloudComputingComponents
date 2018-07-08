package client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Connector {
	
	public Connector() {
		
	}
	
	public Context initiateContext() throws NamingException {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		props.put(Context.PROVIDER_URL, "t3://127.0.0.1:7001");
		return new InitialContext(props);
	}
	
	public Object lookup(String beanName, String remoteInterfaceName) {
		String scope = "java:global";
		String appName = "AtPtEAR";
		String moduleName = "AtPtEJB";
		String qualifiedInterfaceName = "beans." + remoteInterfaceName;
		String lookupString = scope + "/" + appName + "/" + moduleName + "/" + beanName + "!" + qualifiedInterfaceName;
		System.out.println("Lookup string is: " + lookupString);
		try {
			Context context = initiateContext();
			return context.lookup(lookupString);
		}catch(NamingException e) {
			System.err.println("ERROR OCCURRED DURING LOOKING UP");
			e.printStackTrace();
		}
		return null;
	}

}
