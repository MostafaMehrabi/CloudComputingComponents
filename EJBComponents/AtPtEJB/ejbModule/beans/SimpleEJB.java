package beans;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class SimpleEJB
 */
@Stateless
@Remote(SimpleRemoteInterface.class)
@Asynchronous
public class SimpleEJB implements SimpleRemoteInterface {

    /**
     * Default constructor. 
     */
    public SimpleEJB() {

    }

	/**
     * @see SimpleRemoteInterface#add(int, int)
     */
    public Future<Integer> add(int a, int b) {
    	try {
    		Thread.sleep(10000);
    	}catch(Exception e) {
    		
    	}    	
		int result =  a + b;
		return new AsyncResult<Integer>(result);
    }

	/**
     * @see SimpleRemoteInterface#sub(int, int)
     */
    public Future<Integer> sub(int a, int b) {
    	try {
    		Thread.sleep(10000);
    	}catch(Exception e) {
    		
    	}
		int result = a - b;
		return new AsyncResult<Integer>(result);
    }

}
