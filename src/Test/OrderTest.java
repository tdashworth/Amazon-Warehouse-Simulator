package Test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import model.Order;

public class OrderTest {

	@Test
	public void orderCreationTest() {
		//Set up and create a simple order to perform tests on
		List<String> SSUids = new ArrayList<String>();
		SSUids.add("1");
		SSUids.add("2");
		SSUids.add("3");
		SSUids.add("4");
		int i = 10;
		Order o = new Order(SSUids, i);
	
		//Ensure that the order was created successfully
		assertEquals(10, o.getNumberOfTicksToPack());
		assertEquals(SSUids, o.getStorageShelfUIDs());
		assertEquals("Order("+o.hashCode()+"): Storage Shelfs: [1, 2, 3, 4], Ticks to pack: 10", o.toString());
	}
	
	@Test
	public void orderSetTicksToPackTest() {
		//Set up and create a simple order to perform tests on
		List<String> SSUids = new ArrayList<String>();
		SSUids.add("1");
		SSUids.add("2");
		SSUids.add("3");
		SSUids.add("4");
		int i = 10;
		Order o = new Order(SSUids, i);
	
		//Ensure that the order was created successfully
		assertEquals(10, o.getNumberOfTicksToPack());
		assertEquals(SSUids, o.getStorageShelfUIDs());
		assertEquals(0, o.getTotalNumberOfTicksToPack());
		assertEquals("Order("+o.hashCode()+"): Storage Shelfs: [1, 2, 3, 4], Ticks to pack: 10", o.toString());
		
		//Update the number of ticks to pack and ensure everything is still correct
		o.setTotalNumberOfTicksToPack(5);
		assertEquals(10, o.getNumberOfTicksToPack());
		assertEquals(SSUids, o.getStorageShelfUIDs());
		assertEquals(5, o.getTotalNumberOfTicksToPack());
		assertEquals("Order("+o.hashCode()+"): Storage Shelfs: [1, 2, 3, 4], Ticks to pack: 10, Total ticks to pack: 5", o.toString());
		
		//Update the number of ticks to pack again and ensure everything is still correct
		o.setTotalNumberOfTicksToPack(7);
		assertEquals(10, o.getNumberOfTicksToPack());
		assertEquals(SSUids, o.getStorageShelfUIDs());
		assertEquals(7, o.getTotalNumberOfTicksToPack());
		assertEquals("Order("+o.hashCode()+"): Storage Shelfs: [1, 2, 3, 4], Ticks to pack: 10, Total ticks to pack: 7", o.toString());
	}

}
