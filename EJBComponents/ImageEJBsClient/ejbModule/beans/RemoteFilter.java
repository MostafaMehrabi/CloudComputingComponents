package beans;

import java.util.concurrent.Future;
import javax.ejb.Remote;
import classes.Image;

@Remote
public interface RemoteFilter {
	public Future<Image> filter (Image image); 
}
