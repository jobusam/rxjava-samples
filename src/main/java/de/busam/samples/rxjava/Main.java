package de.busam.samples.rxjava;

import de.busam.samples.rxjava.endlesslooping.IntervalFlowable;
import de.busam.samples.rxjava.endlesslooping.RepeatedFlowableWithThreadPool;
import de.busam.samples.rxjava.resourcehandling.Sample1;
import de.busam.samples.rxjava.resourcehandling.Sample2;
import de.busam.samples.rxjava.resourcehandling.Sample3;

public class Main {

    public static void main(String[] args){
        //executeResourceHandling();
        //executeIntervalFlowable();
        executeIntervalFlowableExtended();
        //executeRepeatedFlowableWithThreadPool();
    }

    public static void executeIntervalFlowable(){
        new IntervalFlowable().execute();
    }

    public static void executeIntervalFlowableExtended(){
        new IntervalFlowable().executeWithExtendedWorkload();
    }

    public static void executeRepeatedFlowableWithThreadPool(){
        new RepeatedFlowableWithThreadPool().execute();
    }


    private static void executeResourceHandling(){
        System.out.println("------Resource Handling with RxJava----");
        new Sample1().execute();
        System.out.println("--------------");
        new Sample2().execute();
        System.out.println("--------------");
        new Sample3().execute();
        System.out.println("--------------");

    }


}
