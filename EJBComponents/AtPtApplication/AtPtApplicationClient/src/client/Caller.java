package client;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class Caller {
	
	private Connector connector = null;
	
	public Caller() {
		connector = new Connector();
	}
	
	@SuppressWarnings("unchecked")
	public <T> Future<T>[] callCustom(Class<?> clas, Method method, T argOne, T argTwo, String beanName, String remoteInterfaceName) {
		//This check should not be performed!!!
//		if(!Serializable.class.isAssignableFrom(clas)) {
//			System.err.println("Class " + clas.toString() + " is not serializable.\nThis operation cannot be performed.");
//			return;
//		}
		
		Future<T>[] results = new Future[2];
		if(!(argOne instanceof Serializable)) {
			System.err.println(argOne.toString() + " is not a serializable argument.\nThis operation cannot be performed.");
			return null;
		}
		
		if(!(argTwo instanceof Serializable)) {
			System.err.println(argTwo.toString() + " is not a serializable argument.\nThis operation cannot be performed.");
			return null;
		}
		
		try {
			Object obj = connector.lookup(beanName, remoteInterfaceName);
			obj = clas.cast(obj);
			
			Future<T> resultOne = (Future<T>) method.invoke(obj, argOne, argTwo);
			
			Future<T> resultTwo = (Future<T>) method.invoke(obj, argOne, argTwo); 
					//method.invoke(clas.cast(connector.lookup(beanName, remoteInterfaceName)), argOne, argTwo);
			
			results[0] = resultOne; results[1] = resultTwo;
			
			return results;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
