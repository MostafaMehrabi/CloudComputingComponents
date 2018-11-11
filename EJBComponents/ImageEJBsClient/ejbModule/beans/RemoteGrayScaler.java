package beans;

import java.util.concurrent.Future;

import javax.ejb.Remote;

import classes.Image;

@Remote
public interface RemoteGrayScaler {
	public Future<Image> convert(Image image);
}
