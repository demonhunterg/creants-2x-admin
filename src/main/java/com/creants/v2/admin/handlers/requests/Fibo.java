package com.creants.v2.admin.handlers.requests;

/**
 * @author LamHM
 *
 */
public class Fibo implements Runnable {
	private int iter;


	public Fibo(int iter) {
		this.iter = iter;
	}


	public void run() {
		for (int i = 0; i < iter; i++) {
			fib(i);
		}
	}


	public long fib(int n) {
		if (n <= 1) {
			return n;
		}

		return fib(n - 1) + fib(n - 2);
	}
}
