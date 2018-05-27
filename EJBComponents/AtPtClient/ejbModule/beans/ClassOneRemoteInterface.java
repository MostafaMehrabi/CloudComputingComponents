package beans;

import java.util.concurrent.Future;

import javax.ejb.Remote;

import classes.ClassOne;

@Remote
public interface ClassOneRemoteInterface {
	public Future<ClassOne> add (ClassOne a, ClassOne b);
	public Future<ClassOne> sub (ClassOne a, ClassOne b);
}
