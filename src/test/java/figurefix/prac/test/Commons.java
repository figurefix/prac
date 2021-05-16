package figurefix.prac.test;

import java.io.File;

public class Commons {
	
	public static String getTestHome() {
		String path = System.getProperty("user.home")+File.separator
				+"DEV"+File.separator
				+"LOG"+File.separator;
		File home = new File(path);
		if(!home.exists()) {
			home.mkdirs();
		}
		return path;
	}
}
