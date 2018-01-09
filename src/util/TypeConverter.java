package util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TypeConverter {
	/**
	 * This method converts a set of bytes into a Hexadecimal representation.
	 *
	 * @param buf
	 * @return
	 */
	public static String bytesToHex(byte[] buf) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			int byteValue = (int) buf[i] & 0xff;
			if (byteValue <= 15) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toString(byteValue, 16));
		}
		return strBuf.toString();
	}

	/**
	 * This method converts a specified hexadecimal String into a set of bytes.
	 *
	 * @param hexString
	 * @return
	 */
	public static byte[] hexToBytes(String hexString) {
		int size = hexString.length();
		byte[] buf = new byte[size / 2];
		int j = 0;
		for (int i = 0; i < size; i++) {
			String a = hexString.substring(i, i + 2);
			int valA = Integer.parseInt(a, 16);
			i++;
			buf[j] = (byte) valA;
			j++;
		}
		return buf;
	}
	
	/*
	 * This method converts a long to a set of bytes
	 */
	public static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}
	
//	public static byte[] inputStreamToBytes(FileInputStream fis) throws IOException{
//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		int nRead;
//		byte[] data = new byte[16384];
//		while ((nRead = fis.read(data, 0, data.length)) != -1) {
//		  buffer.write(data, 0, nRead);
//		}
//		buffer.flush();
//		return buffer.toByteArray();
//	}
}
