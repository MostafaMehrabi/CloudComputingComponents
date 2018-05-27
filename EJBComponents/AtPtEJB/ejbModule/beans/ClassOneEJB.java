package beans;

import classes.ClassOne;

import java.util.Calendar;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ClassOneEJB
 */
@Stateless
@Remote(ClassOneRemoteInterface.class)
@Asynchronous
public class ClassOneEJB implements ClassOneRemoteInterface {

    /**
     * Default constructor. 
     */
    public ClassOneEJB() {

    }

	/**
     * @see ClassOneRemoteInterface#sub(ClassOne, ClassOne)
     */
    public Future<ClassOne> sub(ClassOne a, ClassOne b) {
    	String message = "";
    	
    	Calendar now = Calendar.getInstance();
    	message += "Method sub for ClassOneEJB strated from " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread "
    			+ Thread.currentThread().getId() + "\n";
    	
    	try {
    		Thread.sleep(10000);
    	}catch(Exception e) {
    		
    	}
    	
    	int sum = a.getNum() + b.getNum();
    	now = Calendar.getInstance();
    	message += "Method sub for ClassOneEJB finished at " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread " 
    			+ Thread.currentThread().getId() + "\n";
    	
    	ClassOne value = new ClassOne(sum);
    	value.setMessage(message);
    	return new AsyncResult<ClassOne>(value);
    }

	/**
     * @see ClassOneRemoteInterface#add(ClassOne, ClassOne)
     */
    public Future<ClassOne> add(ClassOne a, ClassOne b) {
    	String message = "";
    	
    	Calendar now = Calendar.getInstance();
    	message += "Method add for ClassOneEJB started from " +  now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread "
    			+ Thread.currentThread().getId() + "\n";
    	
    	try {
    		Thread.sleep(10000);
    	}catch(Exception e) {
    		
    	}
    	
    	now = Calendar.getInstance();
    	message += "Method add for ClassOneEJB finished at " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread " 
    			+ Thread.currentThread().getId() + "\n";
    	int sub = a.getNum() - b.getNum();
    	ClassOne value = new ClassOne(sub);
    	value.setMessage(message);
    	return new AsyncResult<ClassOne>(value);
    }
}
