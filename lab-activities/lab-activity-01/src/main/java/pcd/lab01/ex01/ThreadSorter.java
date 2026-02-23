package pcd.lab01.ex01;

import java.util.Arrays;

public class ThreadSorter extends Thread{
    private int _firstIndex;
    private int _lastIndex;
    private int[] _v;

    public ThreadSorter(String name, int firstIndex, int lastIndex, int[] v){
        super(name);
        this._firstIndex = firstIndex;
        this._lastIndex = lastIndex;
        this._v = v;
    }

    public void run(){
        log("Sorting v from " + this._firstIndex + " to " + _lastIndex);
        Arrays.sort(this._v, this._firstIndex, this._lastIndex);
        log("Done");
    }

    private void log(String msg) {
        System.out.println("[ " + System.currentTimeMillis() +   " ][ " + getName()+ " ] " + msg);
    }
}
