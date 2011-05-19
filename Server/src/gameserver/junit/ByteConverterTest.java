package gameserver.junit;

import static org.junit.Assert.*;
import gameserver.util.ByteBreaker;
import gameserver.util.ByteBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ByteConverterTest {

	@Before public void setUp() throws Exception {
		
	}

	@After public void tearDown() throws Exception {
		
	}

	
	@Test public void testByte() {
		final byte testVal = (byte) 187;
		final byte[] testArr = new byte[]{(byte) 187};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		byte act = bd.parseByte();
		
		assertTrue("ByteBreaker failed to convert: ("+act+", "+testVal+")", act == testVal);
	}

	@Test public void testByteArray() {
		final byte[] testVal = new byte[]{(byte) 192, (byte) 168, (byte) 12, (byte) 255};
		final byte[] testArr = new byte[]{(byte) 192, (byte) 168, (byte) 12, (byte) 255};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		byte[] act = bd.parseByteArray(4);
		
		assertArrayEquals("ByteBreaker failed to convert", testArr, act);
	}

//	@Test public void testByteArrayIntInt() {
//		fail("Not yet implemented");
//	}

	@Test public void testChar() {
		final char testVal = 'A';
		final byte[] testArr = new byte[]{(byte) 65};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		char act = bd.parseChar();
		
		assertTrue("ByteBreaker failed to convert: ("+act+", "+testVal+")", act == testVal);
	}

//	@Test public void testCharArray() {
//		fail("Not yet implemented");
//	}

//	@Test public void testCharArrayIntInt() {
//		fail("Not yet implemented");
//	}

	@Test public void testBoolean() {
		final boolean testVal = true;
		final byte[] testArr = new byte[]{(byte) 1};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		boolean act = bd.parseBoolean();
		
		assertTrue("ByteBreaker failed to convert: ("+act+", "+testVal+")", act == testVal);
	}

	@Test public void testInt() {
		final int testVal = 0xF2439B8A;
		final byte[] testArr = new byte[]{(byte) 0xF2, (byte)0x43, (byte)0x9B, (byte)0x8A};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		int act = bd.parseInt();
		
		assertTrue("ByteBreaker failed to convert: ("+act+", "+testVal+")", act == testVal);
	}

	@Test public void testLong() {
		final long testVal = 0xF2439B8ABDE762C9L;
		final byte[] testArr = new byte[]{
			(byte)0xF2, (byte)0x43, (byte)0x9B, (byte)0x8A,
			(byte)0xBD, (byte)0xE7, (byte)0x62, (byte)0xC9
		};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		long act = bd.parseLong();
		
		assertTrue("ByteBreaker failed to convert: ("+act+", "+testVal+")", act == testVal);
	}

	@Test public void testFloat() {
		final float testVal = 5676.1230984f;
//		System.out.println(Integer.toHexString(Float.floatToRawIntBits(testVal)));
		final byte[] testArr = new byte[]{
				(byte)0x45, (byte)0xB1, (byte)0x60, (byte)0xFC
		};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		float act = bd.parseFloat();
		
		assertTrue("ByteBreaker failed to convert: ("+act+", "+testVal+")", act == testVal);
	}

	@Test public void testDouble() {
		final double testVal = 93821.6593610373;
//		System.out.println(Long.toHexString(Double.doubleToRawLongBits(testVal)));
		final byte[] testArr = new byte[]{
				(byte)0x40, (byte)0xf6, (byte)0xe7, (byte)0xda,
				(byte)0x8c, (byte)0xbe, (byte)0x28, (byte)0xb7
		};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		double act = bd.parseDouble();
		
		assertTrue("ByteBreaker failed to convert: ("+act+", "+testVal+")", act == testVal);
	}

//	@Test public void testObject() {
//		gameserver.ids.ClientID ci = new gameserver.ids.ClientID("Hello World", 123);
//		
//	}

	@Test public void testString() {
		final String testVal = "Hello World";
		final byte[] testArr = new byte[]{
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)11,
				(byte)0x48, (byte)0x65, (byte)0x6C, (byte)0x6C, (byte)0x6F,
				(byte)0x20,
				(byte)0x57, (byte)0x6F, (byte)0x72, (byte)0x6C, (byte)0x64,
		};
		
		ByteBuilder bb = new ByteBuilder();
		bb.append(testVal);
		
		assertArrayEquals("ByteBuilder failed to convert",
				testArr, 
				bb.toByteArray());
		
		ByteBreaker bd = new ByteBreaker(bb.toByteArray());
		String act = bd.parseString();
		
		assertEquals("ByteBreaker failed to convert", act, testVal);
	}

//	@Test public void testSpecial() {
//		fail("Not yet implemented");
//	}

}
