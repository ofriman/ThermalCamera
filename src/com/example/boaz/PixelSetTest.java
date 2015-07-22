package com.example.boaz;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;


public class PixelSetTest {

	@Test
	public void testPixelSet() {
		PixelSet ps = new PixelSet();
		int size=ps.INIT_SIZE*3;
		for(int i=0;i<size;i++) {
			ps.add(i, -i);
		}
		for(int i=0;i<size;i++) {
			ps.removeFirst();
		}
		if(!ps.isEmpty())
		fail("ERR: something is very wrong with add / removeFirst");
	}
	/*@Test
	public void testPixelSet2() {
		PixelSet ps = new PixelSet();
		int size=ps.INIT_SIZE;
		for(int i=0;i<10;i++) {
			ps.add(i, -i);
		}
		for(int i=0;i<10;i++) {
			int[] tt = ps.removeFirst();
			System.out.println(tt[0]+" y "+tt[1]);
		}
		if(!ps.isEmpty())
		fail("ERR: something is very wrong with add / removeFirst");
	}*/

	@Test
	public void testRemoveFirstUnsafe() {
			PixelSet ps = new PixelSet();
			int size=ps.INIT_SIZE*3;
			for(int i=0;i<size;i++) {
				ps.add(i, -i);
			}
			for(int i=0;i<size;i++) {
				ps.removeFirstUnsafe();
			}
			if(!ps.isEmpty())
			fail("ERR: something is very wrong with add / removeFirst");
		}
	@Test
	public void testRemoveFirstUnsafe2() {
			PixelSet ps = new PixelSet();
			int size1=ps.INIT_SIZE*300;
			for(int i=0;i<30;i++) {
				ps.add(i, -i);
			}
			for(int i=0;i<size1;i++) {
				ps.add(i, -i);
				ps.removeFirst();
			}
		}
	
	/**
	 * public static ArrayList<Image_light> Image2Lights(
	 */
}
