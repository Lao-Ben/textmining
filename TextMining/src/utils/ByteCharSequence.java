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

	private static final String ENCODING = "ISO-8859-1";
	private byte[] data;

	public ByteCharSequence(String str) {
		try {
			data = str.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected: " + ENCODING + " not supported!");
		}
	}
	
	public ByteCharSequence(byte[] data)
	{
		this.data = data;
	}
	
	public ByteCharSequence(byte[] data, int offset, int end) {
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
	
	public int indexOf(ByteCharSequence seq)
	{
		for (int i = 0; i < (data.length-seq.length());i++)
		{
			int j = 0;
			for (j=0; j < seq.length(); j++)
			{
				if (charAt(i+j) != seq.charAt(j))
					break;
			}
			if (j == seq.length())
				return i;
		}
		return -1;
	}
	
	public ByteCharSequence get(ByteCharSequence seq)
	{
		for (int i = 0; i < (data.length-seq.length());i++)
		{
			int j = 0;
			byte[] tab = new byte[seq.length()];
			for (j=0; j < seq.length(); j++)
			{
				if (charAt(i+j) != seq.charAt(j))
					break;
				else
					tab[j] = seq.data[j];
			}
			if (j == seq.length())
				return new ByteCharSequence(tab);
		}
		return null;
	}
	
	public void append(ByteCharSequence seq)
	{
		byte[] tab = new byte[seq.length()+data.length];
		System.arraycopy(data, 0, tab, 0, data.length);
		System.arraycopy(seq.data, 0, tab, data.length, seq.length());
		data = null;
		data = tab;
	}
}
