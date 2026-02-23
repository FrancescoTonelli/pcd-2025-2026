package pcd.lab01.hello;

import java.util.*;

public class SimpleClock extends Thread {

	private volatile boolean stopped;
	private int interval;
	
	public SimpleClock(int interval){
		super("clock");
		stopped = false;
		this.interval = interval;
	}
	
	public void run(){
		log("started.");
		while (!stopped) {
			log(new Date().toString());
			try{
				waitAbit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log("stopped.");
	}
	
	public void notifyStop() {
		stopped = true;
		interrupt();
	}
	
	private void waitAbit() {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException ex) {
			log(ex.getMessage());
		}
	}
	
	private void log(String msg) {
		System.out.println("[ " + System.currentTimeMillis() +   " ][ " + getName()+ " ] " + msg); 
	}
}
