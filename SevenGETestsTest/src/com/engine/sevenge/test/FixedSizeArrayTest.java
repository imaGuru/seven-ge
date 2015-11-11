package com.engine.sevenge.test;

import java.util.Comparator;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.sevenge.utils.FixedSizeArray;

public class FixedSizeArrayTest extends AndroidTestCase {

	private FixedSizeArray<Integer> testArray;
	private int[] testData = new int[] { 2, 3, 1, 4, 2 };

	public FixedSizeArrayTest() {
	}

	@Override
	public void setUp() {
		testArray = new FixedSizeArray<Integer>(10, new Comparator<Integer>() {
			@Override
			public int compare(Integer l, Integer r) {
				return l.intValue() - r.intValue();
			}
		});
		for (int i = 0; i < testData.length; i++) {
			testArray.add(testData[i]);
		}
	}

	@Override
	public void tearDown() {
		testArray.clear();
	}

	public void testAdd() {
		int[] test = new int[] { 2, 3, 1, 4, 2 };
		for (int i = 0; i < test.length; i++) {
			testArray.add(test[i]);
			if (test[i] != testArray.get(i))
				Assert.fail("Contents of fixed array don't match the data input");
		}
	}

	public void testRemoveByIndex() {
		int[] expectedResult = new int[] { 2, 3, 4, 2 };
		testArray.remove(2);
		assertEquals(expectedResult.length, testArray.getCount());
		for (int i = 0; i < expectedResult.length; i++) {
			if (expectedResult[i] != testArray.get(i))
				Assert.fail("Contents of fixed array don't match the data input after removal");
		}
	}

	public void testRemoveByObject() {
		int[] expectedResult = new int[] { 2, 1, 4, 2 };
		testArray.remove(new Integer(3));
		for (int i = 0; i < expectedResult.length; i++) {
			if (expectedResult[i] != testArray.get(i))
				Assert.fail("Contents of fixed array don't match the data input after removal");
		}
	}

	public void testSort() {
		int[] expectedResult = new int[] { 1, 2, 2, 3, 4 };
		testArray.sort(true);
		for (int i = 0; i < expectedResult.length; i++) {
			if (expectedResult[i] != testArray.get(i))
				Assert.fail("Contents of fixed array are not sorted");
		}
	}

	public void testSwap() {
		int[] expectedResult = new int[] { 6, 3, 1, 4, 2, 2 };
		testArray.add(new Integer(6));
		testArray.swapWithLast(0);
		for (int i = 0; i < expectedResult.length; i++) {
			if (expectedResult[i] != testArray.get(i))
				Assert.fail("Contents of fixed array don't match the data input after swap");
		}
	}

	public void testSet() {
		int[] expectedResult = new int[] { 2, 3, 1, 6, 2 };
		testArray.set(3, new Integer(6));
		for (int i = 0; i < expectedResult.length; i++) {
			if (expectedResult[i] != testArray.get(i))
				Assert.fail("Contents of fixed array don't match the data input after set");
		}
	}
}
