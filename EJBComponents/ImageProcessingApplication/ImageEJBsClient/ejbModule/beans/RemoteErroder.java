package beans;

import java.util.concurrent.Future;

import javax.ejb.Remote;

import classes.Image;

@Remote
public interface RemoteErroder {
	public Future<Image> errode (Image image);
}
