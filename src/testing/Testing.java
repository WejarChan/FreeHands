package testing;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.micheal.freeHands.exception.ConfigurationFormatException;
import org.micheal.freeHands.exception.FileFormatException;
import org.micheal.freeHands.exception.FileNotFoundException;
import org.micheal.freeHands.main.FreeHands;

public class Testing {

	@Test
	public void test(){
		try {
			String path = ClassLoader.getSystemResource("").getPath()+"QCSConfig.xml";
			FreeHands free = new FreeHands();
			free.free(path, FreeHands.MYBATIS);
			
		} catch( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
