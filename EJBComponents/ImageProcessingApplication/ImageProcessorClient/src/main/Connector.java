package main;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Connector {
	public Connector() {
		
	}
	
	private Context getInitialContext() throws NamingException{
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		props.put(Context.PROVIDER_URL, "t3://127.0.0.1:7001");
		return new InitialContext(props);
	}
	
	public Object lookup(String beanName, String remoteInterface) {
		String scope = "java:global";
		String appName = "ImageProcessors";
		String moduleName = "ImageEJBs";
		String qualifiedRemoteInterface = "beans." + remoteInterface;
		String  lookupString = scope + "/" + appName + "/" + moduleName + "/" + beanName + "!" + qualifiedRemoteInterface;
		try {
			Context context = getInitialContext();
			return context.lookup(lookupString);
		}catch(Exception e) {
			System.err.println("ERROR OCCURED WHILE LOOKING UP THIS STRING");
			e.printStackTrace();
		}
		return null;
	}
}
