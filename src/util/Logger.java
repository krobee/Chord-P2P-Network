package util;

public class Logger {
	public static void info(Class logClass, String message){
		if (message != null) {
			System.out.println("\n[INFO]" + logClass.getName() + ": " + message);
		}
	}

	public static void error(Class logClass, String message){
		if (message != null) {
			System.err.println("\n[ERROR]" + logClass.getName() + ": " + message);
		}
	}
}
