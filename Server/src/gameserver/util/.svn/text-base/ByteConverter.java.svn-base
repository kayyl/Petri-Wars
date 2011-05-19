package gameserver.util;


public class ByteConverter {
	
	public static byte[] convertToByte(boolean value){
		return new byte[]{(byte)((value)?1:0)};
	}
	public static boolean backToBoolean(byte[] value, int offset){
		return (value[offset]>0)?true:false;
	}
	
	public static byte[] convertToByte(int value){
		byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
	}
	public static int backToInt(byte[] value, int offset){
		int b = 0;
		for (int i = 0; i < 4; i++){
			int off = (3 - i) * 8;
			b |= value[offset+i] << off;
		}
		return b;
	}
	
	public static byte[] convertToByte(long value){
		byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
	}
	public static long backToLong(byte[] value, int offset){
		long b = 0;
		for (int i = 0; i < 8; i++){
			int off = (7 - i) * 8;
			b |= value[offset+i] << off;
		}
		return b;
	}
	
	public static byte[] convertToByte(char value){
		byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
	}
	public static char backToChar(byte[] value, int offset){
		char b = 0;
		for (int i = 0; i < 2; i++){
			int off = (1 - i) * 8;
			b |= (char) (value[offset+i] << off);
		}
		return b;
	}
	
	public static byte[] convertToByte(byte value){
		return new byte[]{value};
	}
	public static byte[] convertToByte(byte[] value){
		return value;
	}
}
