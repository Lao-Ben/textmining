/**
 * 
 */
package utils;

import java.util.Arrays;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;


/**
 * @author peyotll
 *
 */
public class ByteCharSequence implements CharSequence, Serializable, Comparable {
	static final long serialVersionUID = 1L;

	private static final String ENCODING = "ISO-8859-1";
	private final byte[] data;

	public ByteCharSequence(String str) {
		try {
			data = str.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected: " + ENCODING + " not supported!");
		}
	}
	
	ByteCharSequence(byte[] data, int offset, int end) {
		this.data = new byte[end - offset];
		System.arraycopy(data, offset, this.data, 0, end - offset);
	}

	public byte[] getBytes() {
		return data;
	}
	
	public char charAt(int index) {
		if (index >= data.length) {
			throw new StringIndexOutOfBoundsException("Invalid index " +
					index + " length " + length());
		}
		return (char) (data[index] & 0xff);
	}

	public int length() {
		return data.length;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if (start < 0 || end > (data.length)) {
			throw new IllegalArgumentException("Illegal range " +
					start + "-" + end + " for sequence of length " + length());
		}
		return new ByteCharSequence(data, start, end);
	}
	
	public ByteCharSequence byteSubSequence(int start, int end) {
		if (start < 0 || end > (data.length)) {
			throw new IllegalArgumentException("Illegal range " +
					start + "-" + end + " for sequence of length " + length());
		}
		return new ByteCharSequence(data, start, end);
	}

	public String toString() {
		try {
			return new String(data, ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected: " + ENCODING + " not supported");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ByteCharSequence)
			return Arrays.equals(data, ((ByteCharSequence)o).getBytes());
		return false;
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		for (int i = 0; i < data.length; i++) {
			h = 31*h + charAt(i);
		}
		return h;
	}

	@Override
	public int compareTo(Object o) {
		ByteCharSequence s = (ByteCharSequence)o;

		int cmp = length() - s.length();
		if (cmp != 0) return cmp;
		
		for (int i = 0; i < data.length; i++) {
			int cmp2 = charAt(i) - s.charAt(i);
			if (cmp2 != 0) return cmp2;
		}
		return 0;
	}
}
