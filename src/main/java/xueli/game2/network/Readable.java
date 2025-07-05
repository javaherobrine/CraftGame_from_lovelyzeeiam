package xueli.game2.network;

import java.io.IOException;
import java.io.InputStream;

import xueli.utils.Bytes;

public interface Readable {

	default public int readInteger() throws IOException {
		return Bytes.getInt(readBytes(4));
	}

	default public short readShort() throws IOException {
		return Bytes.getShort(readBytes(2));
	}

	default public long readLong() throws IOException {
		return Bytes.getLong(readBytes(8));
	}
	default public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInteger());
	}

	default public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	default public boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	default public String readString(int length) throws IOException {
		return new String(readBytes(length), CodecConstants.STRING_CHARSET);
	}

	default public byte[] readBytes(int length) throws IOException {
		byte[] bs = new byte[length];
		for (int i = 0; i < length; i++) {
			bs[i] = readByte();
		}
		return bs;
	}

	public byte readByte() throws IOException;

	default InputStream toInputStream() {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				return readByte();
			}
		};
	}

}
