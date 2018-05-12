package jam;

import jam.core.Server;
import jam.core.game.TableManager;
import jam.core.user.User;
import jam.core.user.UserManager;
import jam.io.IIoSystem;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main
{
    public static void main( String[] args ) throws IOException
    {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("jamspring.xml");
		
		UserManager um = ctx.getBean("userManager", UserManager.class);
		List<User> list = um.getUsers();
		for (User user : list) {
			System.out.println(user.toString());
		}
    	
		TableManager gm = ctx.getBean("tableManager", TableManager.class);
		
		Server server = new Server(um, gm);
		
    	IIoSystem iosystem = ctx.getBean("ioSystem", IIoSystem.class);
    	iosystem.start(server);
    	/*	
    	 *  id 	username 	password 	avatar 	gender 	date_register
			1 	lili 	asd123 	0 	0 	2013
			2 	lili2 	asd 	0 	0 	1970
			3 	lilito 	asd
    	 */
    	while (true)
    	{
    		try
			{
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		System.gc();
    	}
    }
}
