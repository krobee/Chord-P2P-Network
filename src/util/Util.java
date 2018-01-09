package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
	
	public static int generateID() {
		int max = (int) Math.pow(2, Config.ID_BIT_RANGE);
		return getRandInt(0,max);
	}

	public static String generateFileKey(File file) {
		String md5Str = null;
		try {
			FileInputStream	fis = new FileInputStream(file);

			byte[] byteArray = new byte[1024];
			int bytesCount = 0;

			MessageDigest md = MessageDigest.getInstance("MD5");
			while ((bytesCount = fis.read(byteArray)) != -1) {
				md.update(byteArray, 0, bytesCount);
			}
			fis.close();

			md5Str = TypeConverter.bytesToHex(md.digest()).substring(0, Config.FILE_KEY_RANGE);
//			fileKey = Integer.parseInt(md5Str.substring(0,Config.FILE_KEY_RANGE), 16);
		} catch (FileNotFoundException e) {
			Logger.error(Util.class, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			Logger.error(Util.class, e.getMessage());
		} catch (IOException e) {
			Logger.error(Util.class, e.getMessage());
		}
		return md5Str;
	}

	public static InetAddress getHostInetAddress() throws UnknownHostException {
		InetAddress inetAddr = InetAddress.getLocalHost();
		return inetAddr;
	}

	public static String getHostInetName() throws UnknownHostException {
		String inetName = InetAddress.getLocalHost().getHostName();
		return inetName;
	}

	public static int getRandInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}
	
	public static int convertToCircleID(int id){
		if(id < 0){
			return (int) (id + Math.pow(2, Config.ID_BIT_RANGE) );
		}
		else{
			return (int) (id%Math.pow(2, Config.ID_BIT_RANGE));
		}
		
	}
}











