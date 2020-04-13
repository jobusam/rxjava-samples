package de.busam.samples.rxjava.resourcehandling;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.functions.Action;

/**
 * de.busam.samples.rxjava3.Sample2 the Flowable itself is responsible for
 * closing resource with the {@link Flowable#doFinally(Action)} method!
 */
public class Sample2 {

    public void execute(){
        System.out.println("Sample 2: Closing the resource internally:");

        // creating the resource is not necessary for the callee
        // Resource resource = new Resource();
        createFlowable().subscribe(
                onNext -> System.out.println("nextItem: "+onNext),
                onError -> System.err.println("error: "+onError.getCause()),
                () -> System.out.println("Flowable completed")
        );
        // closing the resource is not necessary for the callee
        // res.close();
    }

    private static Flowable<String> createFlowable() {
        Resource resource = new Resource();
        FlowableOnSubscribe<String> source = subscribe ->{
            System.out.println("Flowable starting to emit items...");
            subscribe.onNext(resource.getContent());
            subscribe.onComplete();
        };
        return Flowable.create(source, BackpressureStrategy.BUFFER)
                //Close the resource within the Flowable!
                .doFinally(resource::close);
    }
}
