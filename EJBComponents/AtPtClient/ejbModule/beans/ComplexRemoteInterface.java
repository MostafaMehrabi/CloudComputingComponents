package beans;

import java.util.concurrent.Future;

import javax.ejb.Remote;

import classes.ComplexClass;

@Remote
public interface ComplexRemoteInterface {
	public Future<ComplexClass> add (ComplexClass a, ComplexClass b);
	public Future<ComplexClass> sub (ComplexClass a, ComplexClass b);
}
