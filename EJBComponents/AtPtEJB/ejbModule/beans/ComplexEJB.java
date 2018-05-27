package beans;

import classes.ClassOne;
import classes.ComplexClass;

import java.util.Calendar;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ComplexEJB
 */
@Stateless
@Remote(ComplexRemoteInterface.class)
@Asynchronous
public class ComplexEJB implements ComplexRemoteInterface {

    /**
     * Default constructor. 
     */
    public ComplexEJB() {

    }

	/**
     * @see ComplexRemoteInterface#add(ComplexClass, ComplexClass)
     */
    public Future<ComplexClass> add(ComplexClass a, ComplexClass b) {
    	String message = "";
    	Calendar now = Calendar.getInstance();
    	message += "Method add for ComplexEJB started from " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread "
    			+ Thread.currentThread().getId() + "\n";
    	
    	try {
    		Thread.sleep(10000);
    	}catch(Exception e) {
    		
    	}
    	
    	now = Calendar.getInstance();
    	message += "Method add for ComplexEJB finished at " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread "
    			+ Thread.currentThread().getId() + "\n";
    	
    	int sum = a.getClassOne().getNum() + b.getClassOne().getNum();
    	ClassOne classOne = new ClassOne(sum);
    	classOne.setMessage(message);
		ComplexClass value = new ComplexClass(classOne);
    	return new AsyncResult<ComplexClass>(value);
    }

	/**
     * @see ComplexRemoteInterface#sub(ComplexClass, ComplexClass)
     */
    public Future<ComplexClass> sub(ComplexClass a, ComplexClass b) {
    	String message = "";
    	Calendar now = Calendar.getInstance();
    	message += "Method sub for ComplexEJB started from " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread " 
    			+ Thread.currentThread().getId() + "\n";
    	
    	try {
    		Thread.sleep(10000);
    	}catch(Exception e) {
    		
    	}
    	
    	now = Calendar.getInstance();
    	message += "Method sub for ComplexEJB finished at " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND) + " by thread "
    			+ Thread.currentThread().getId() + "\n";
    	int sub = a.getClassOne().getNum() - b.getClassOne().getNum();
    	ClassOne classOne = new ClassOne(sub);
    	classOne.setMessage(message);
    	ComplexClass value = new ComplexClass(classOne);
    	return new AsyncResult<ComplexClass>(value);
    }

}
