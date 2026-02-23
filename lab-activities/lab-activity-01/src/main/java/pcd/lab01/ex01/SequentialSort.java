package pcd.lab01.ex01;

import java.util.*;
import pcd.lab01.ex01.ThreadSorter;

public class SequentialSort {

	static final int VECTOR_SIZE = 400_000_000;
	static final int THREAD_NUMBER = 1;
	
	public static void main(String[] args) {
	
		log("Num elements to sort: " + VECTOR_SIZE);
		log("Generating array.");
		var v = genArray(VECTOR_SIZE);
		
		log("Array generated.");
		log("Sorting (" + VECTOR_SIZE + " elements)...");

		List<ThreadSorter> threads = new ArrayList<>();

		int last_start = 0;
		int range = VECTOR_SIZE / THREAD_NUMBER;
		int i;

		for(i = 0; i < THREAD_NUMBER-1; i++) {
			ThreadSorter t = new ThreadSorter(i+"", last_start, last_start+range, v);
			last_start = last_start + range;
			threads.add(t);
		}
		ThreadSorter t = new ThreadSorter(i+"", last_start, VECTOR_SIZE, v);
		threads.add(t);


		long t0 = System.nanoTime();		
		Arrays.sort(v, 0, v.length);
		long t1 = System.nanoTime();
		log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");
		
		// dumpArray(v);
	}


	private static int[] genArray(int n) {
		Random gen = new Random(System.currentTimeMillis());
		var v = new int[n];
		for (int i = 0; i < v.length; i++) {
			v[i] = gen.nextInt();
		}
		return v;
	}

	private static void dumpArray(int[] v) {
		for (var l:  v) {
			System.out.print(l + " ");
		}
		System.out.println();
	}

	private static void log(String msg) {
		System.out.println(msg);
	}
}
