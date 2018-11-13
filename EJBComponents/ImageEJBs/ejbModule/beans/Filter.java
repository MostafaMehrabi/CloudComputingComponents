package beans;

import classes.Image;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class Filter
 */
@Stateless
@Remote(RemoteFilter.class)
public class Filter implements RemoteFilter {

    /**
     * Default constructor. 
     */
    public Filter() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see RemoteFilter#filter(Image)
     */
    @Asynchronous
    public Future<Image> filter(Image image) {
    	long start = System.currentTimeMillis();
    	String description = image.getMessage();
    	
    	processingUnits.GrayScaler grayScaler = new processingUnits.GrayScaler();
    	processingUnits.Erroder erroder = new processingUnits.Erroder();
    	Image result = grayScaler.grayScale(image);
    	result = erroder.errode(result);
    	
    	long end = System.currentTimeMillis();
    	long duration = end - start;
    	String newMessage = Long.toString(duration);
    	
    	description += newMessage;
    	result.setMessage(description);
    	return new AsyncResult<Image>(result);    	
    }

}
