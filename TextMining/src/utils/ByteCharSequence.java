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
public class ByteCharSequence implements CharSequence, Serializable, Comparable<ByteCharSequence> {
	static final long serialVersionUID = 1L;

	/**
	 * The encoding
	 */
	private static final String ENCODING = "ISO-8859-1";
	/**
	 * The data
	 */
	private byte[] data;

	/**
	 * Create a new ByteCharSequence
	 * @param str the word
	 */
	public ByteCharSequence(String str) {
		try {
			data = str.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected: " + ENCODING + " not supported!");
		}
	}
	
	/**
	 * Create a new ByteCharSequence
	 * @param data the data of the word
	 */
	public ByteCharSequence(byte[] data)
	{
		this.data = data;
	}
	
	/**
	 * Create a new ByteCharSequence
	 * @param data the data
	 * @param offset the offset
	 * @param end the end
	 */
	public ByteCharSequence(byte[] data, int offset, int end) {
		this.data = new byte[end - offset];
		System.arraycopy(data, offset, this.data, 0, end - offset);
	}

	/**
	 * Retrieve data
	 * @return the data
	 */
	public byte[] getBytes() {
		return data;
	}
	
	/**
	 * Return the character at index
	 */
	public char charAt(int index) {
		if (index >= data.length) {
			throw new StringIndexOutOfBoundsException("Invalid index " +
					index + " length " + length());
		}
		return (char) (data[index] & 0xff);
	}

	/**
	 * Get length of data
	 */
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
	
	/**
	 * Get a subsequence of data
	 * @param start start of subsequence
	 * @param end end of subsequence
	 * @return the subsequence
	 */
	public ByteCharSequence byteSubSequence(int start, int end) {
		if (start < 0 || end > (data.length)) {
			throw new IllegalArgumentException("Illegal range " +
					start + "-" + end + " for sequence of length " + length());
		}
		return new ByteCharSequence(data, start, end);
	}

	/**
	 * Retrieve the String
	 */
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
		int n = data.length;
		for (int i = 0; i < n; i++) {
			h = (int) (h + charAt(i)* Math.pow(31, n-1-i));
		}
		return h;
	}

	@Override
	public int compareTo(ByteCharSequence s) {
		int cmp = length() - s.length();
		if (cmp != 0) return cmp;
		
		for (int i = 0; i < data.length; i++) {
			int cmp2 = charAt(i) - s.charAt(i);
			if (cmp2 != 0) return cmp2;
		}
		return 0;
	}
}
