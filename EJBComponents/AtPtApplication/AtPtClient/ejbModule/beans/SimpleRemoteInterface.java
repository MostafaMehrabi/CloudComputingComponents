package beans;

import java.util.concurrent.Future;

import javax.ejb.Remote;

@Remote
public interface SimpleRemoteInterface {
	public Future<Integer> add (int a, int b);
	public Future<Integer> sub (int a, int b);
}
