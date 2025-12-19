package io.github.javaherobrine;
import org.lwjgl.system.NativeType;
/**
 * Code from Java_Herobrine, but only memory-related functions are provided
 */
public final class GameUtils {
	static {
		System.load("/home/javaherobrine/libJNI.so");
	}
	public static native long address(byte[] b);
	/**
	 * Tell JVM that the memory can be freed
	 */
	public static native void allowGC(@NativeType("void*") long addr,byte[] b);
}
