package gameserver.util;

import java.util.Arrays;

public class ByteBreaker {
	private byte[] array;
	private int ptr;
	
	public ByteBreaker(byte[] array){
		this.array = array;
		this.ptr = 0;
	}
	
	public byte[] toByteArray(){
		return array.clone();
	}
	
	private void checkOffsetPointer(int size){
		if (ptr+size > array.length) 
			throw new ArrayIndexOutOfBoundsException("Pointer past end of array.");
	}
	
	//////Parse commands///////
	
	public byte parseByte(){
		checkOffsetPointer(1);
		return array[ptr++];
	}
	
	public byte[] parseByteArray(int length){
		checkOffsetPointer(length);
		byte[] bb = Arrays.copyOfRange(array, ptr, ptr+length);
		ptr += length;
		return bb;
	}
	
	public char parseChar(){
		checkOffsetPointer(1);
		char value = (char) array[ptr++];
		return value;
	}
	
	public boolean parseBoolean(){
		checkOffsetPointer(1);
		return (array[ptr++] > 0);
	}
	
	public short parseShort(){
		final int LEN = 2; //long are 8 bytes long
		checkOffsetPointer(LEN);
		
		byte[] b = Arrays.copyOfRange(array, ptr, ptr+LEN);
		ptr += LEN;
		short value = 0;
		
		for (int i = 0; i < LEN; i++) {
			int offset = (b.length - 1 - i) * 8;
			long v = 0xFF & b[i]; //see note above
			value |= v << offset;
		}
		return value;
	}
	
	public int parseInt(){
		final int LEN = 4; //int are 4 bytes long
		checkOffsetPointer(LEN);
		
		byte[] b = Arrays.copyOfRange(array, ptr, ptr+LEN);
		ptr += LEN;
		int value = 0;
		
		for (int i = 0; i < LEN; i++) {
			int offset = (b.length - 1 - i) * 8;
			
			int v = 0xFF & b[i]; //The ANDing with 0xFF is necessary to keep
			// from the auto-conversion to int from repeating the negative bit, 
			// to keep negative values. We don't want to keep negative values here,
			// as doing so messes up the ORing below
			
			value |= (v << offset);
//			System.out.println("off:"+offset+" v:"+Integer.toBinaryString(v)+" voff:"+Integer.toBinaryString(v << offset)+" value:"+Integer.toBinaryString(value));
		}
		return value;
	}
	
	public long parseLong(){
		final int LEN = 8; //long are 8 bytes long
		checkOffsetPointer(LEN);
		
		byte[] b = Arrays.copyOfRange(array, ptr, ptr+LEN);
		ptr += LEN;
		long value = 0;
		
		for (int i = 0; i < LEN; i++) {
			int offset = (b.length - 1 - i) * 8;
			long v = 0xFF & b[i]; //see note above
			value |= v << offset;
		}
		return value;
	}
	
	public float parseFloat(){
		final int LEN = 4; //int are 4 bytes long
		checkOffsetPointer(LEN);
		
		byte[] b = Arrays.copyOfRange(array, ptr, ptr+LEN);
		ptr += LEN;
		int value = 0;
		
		for (int i = 0; i < LEN; i++) {
			int offset = (b.length - 1 - i) * 8;
			
			int v = 0xFF & b[i]; //The ANDing with 0xFF is necessary to keep
			// from the auto-conversion to int from repeating the negative bit, 
			// to keep negative values. We don't want to keep negative values here,
			// as doing so messes up the ORing below
			
			value |= (v << offset);
//			System.out.println("off:"+offset+" v:"+Integer.toBinaryString(v)+" voff:"+Integer.toBinaryString(v << offset)+" value:"+Integer.toBinaryString(value));
		}
		return Float.intBitsToFloat(value);
	}
	
	public double parseDouble(){
		final int LEN = 8; //long are 8 bytes long
		checkOffsetPointer(LEN);
		
		byte[] b = Arrays.copyOfRange(array, ptr, ptr+LEN);
		ptr += LEN;
		long value = 0;
		
		for (int i = 0; i < LEN; i++) {
			int offset = (b.length - 1 - i) * 8;
			long v = 0xFF & b[i]; //see note above
			value |= v << offset;
		}
		return Double.longBitsToDouble(value);
	}
	
	public String parseString(){
		int length = this.parseInt();
		try {
			String str = new String(Arrays.copyOfRange(array, ptr, ptr+length));
			return str;
		} catch (OutOfMemoryError e){
			throw new StringByteFormatException("The string being parsed is in a bad format.\n" +
					"The format is [length: 4 bytes][string: length bytes]. Length read: "
					+length+", or 0x"+Integer.toHexString(length), e);
		}
		
	}
	
	public long parseSpecial(){
		return 0;
	}
	
	///////////////////////////////////////////////
	
	public static class StringByteFormatException extends RuntimeException {
		private static final long serialVersionUID = -1169977566539214706L;
		public StringByteFormatException() {super();}
		public StringByteFormatException(String arg0, Throwable arg1) {super(arg0, arg1);}
		public StringByteFormatException(String arg0) {super(arg0);}
		public StringByteFormatException(Throwable arg0) {super(arg0);}
	}
}
