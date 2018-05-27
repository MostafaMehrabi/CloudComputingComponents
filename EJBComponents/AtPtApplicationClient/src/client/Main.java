package client;

import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import beans.ClassOneRemoteInterface;
import beans.ComplexRemoteInterface;
import beans.SimpleRemoteInterface;
import classes.ClassOne;
import classes.ComplexClass;

public class Main {

	static ConcurrentLinkedQueue<Future<ClassOne>> classOnes = new ConcurrentLinkedQueue<>();
	static ConcurrentLinkedQueue<Future<ComplexClass>> complexes = new ConcurrentLinkedQueue<>();
	
	public static void main(String[] args) {
		Caller caller = new Caller();
//		caller.callSimpleEJB();
//		caller.callClassOneEJB();
//		caller.callComplexEJB();		
		
		try {
			caller.callCustom(SimpleRemoteInterface.class, SimpleRemoteInterface.class.getMethod("add", int.class, int.class), 5, 3, "SimpleEJB", "SimpleRemoteInterface");
			caller.callCustom(SimpleRemoteInterface.class, SimpleRemoteInterface.class.getMethod("sub", int.class, int.class), 5, 3, "SimpleEJB", "SimpleRemoteInterface");
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//calling ClassOneEJB using reflection
		try {
		
			ClassOne argOne = new ClassOne(5);
			ClassOne argTwo = new ClassOne(3);
			
			Future<ClassOne>[] onesAdd = caller.callCustom(ClassOneRemoteInterface.class, ClassOneRemoteInterface.class.getMethod("add", ClassOne.class, ClassOne.class)
					, argOne, argTwo, "ClassOneEJB", "ClassOneRemoteInterface");
			
			Future<ClassOne>[] onesSub = caller.callCustom(ClassOneRemoteInterface.class, ClassOneRemoteInterface.class.getMethod("sub", ClassOne.class, ClassOne.class)
					, argOne, argTwo, "ClassOneEJB", "ClassOneRemoteInterface");
		
			classOnes.add(onesAdd[0]); classOnes.add(onesAdd[1]); classOnes.add(onesSub[0]); classOnes.add(onesSub[1]);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//calling ComplexEJB using reflection
		try {
			
			ComplexClass argOne = new ComplexClass(new ClassOne(5));
			ComplexClass argTwo = new ComplexClass(new ClassOne(3));
			
			Future<ComplexClass>[] complexAdd = caller.callCustom(ComplexRemoteInterface.class, ComplexRemoteInterface.class.getMethod("add", ComplexClass.class, ComplexClass.class)
					, argOne, argTwo, "ComplexEJB", "ComplexRemoteInterface");
			
			Future<ComplexClass>[] complexSub = caller.callCustom(ComplexRemoteInterface.class, ComplexRemoteInterface.class.getMethod("sub", ComplexClass.class, ComplexClass.class)
					, argOne, argTwo, "ComplexEJB", "ComplexRemoteInterface");
			
			complexes.add(complexAdd[0]); complexes.add(complexAdd[1]); complexes.add(complexSub[0]); complexes.add(complexSub[1]);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		checkForResults();
	}
	
	private static void checkForResults() {
		int count = 0;
		Calendar now = Calendar.getInstance();
		System.out.println("Checking results started at " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) 
											+ ":" + now.get(Calendar.SECOND) + " by thred " + Thread.currentThread().getId());
		try {
			while(count != 8) {
				for(Future<ClassOne> result : classOnes) {
					if(result.isDone()) {
						count ++;
						classOnes.remove(result);
						ClassOne cls = result.get();
						System.out.println(cls.getMessage());
						System.out.println("And the classOne result is: " + cls.getNum() + "\n");
					}
				}
				
				for(Future<ComplexClass> result : complexes) {
					if(result.isDone()) {
						count ++;
						complexes.remove(result);
						ComplexClass clx = result.get();
						System.out.println(clx.getClassOne().getMessage());
						System.out.println("And the complex result is: " + clx.getClassOne().getNum() + "\n");
						
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		now = Calendar.getInstance();
		System.out.println("Checking results finished at " + now.get(Calendar.HOUR) + ":" + now.get(Calendar.MINUTE) 
											+ ":" + now.get(Calendar.SECOND) + " by thred " + Thread.currentThread().getId());
	}	
}
