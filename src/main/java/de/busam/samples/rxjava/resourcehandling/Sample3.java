package de.busam.samples.rxjava.resourcehandling;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Supplier;


/**
 * de.busam.samples.rxjava3.Sample3: the Flowable itself is responsible for creating and
 * closing resource with the {@link Flowable#using(Supplier, Function, Consumer)} method!
 *
 * According to several blogs this is the preferred solution!
 */
public class Sample3 {

    public void execute(){
        System.out.println("Sample 3: Flowable handles resource lifecycle with Flowable#using method:");
        create().subscribe(
                onNext -> System.out.println("nextItem: "+onNext),
                onError -> System.err.println("error: "+onError.getCause()),
                () -> System.out.println("Flowable completed")
        );
    }

    private static Flowable<String> create() {
        Supplier<Resource> resourceSupplier = Resource::new;
        Function< Resource, Flowable<String>> sourceSupplier = Sample3::createFlowable;
        Consumer<Resource> resourceDisposer = Resource::close;
        return Flowable.using(resourceSupplier, sourceSupplier, resourceDisposer);
    }

    private static Flowable<String> createFlowable(Resource resource){
        return Flowable.create( emitter ->{
            System.out.println("Flowable starting to emit items...");
            //always check if the emitter is already disposed
            //becuase if the emitter is disposed the resource is also closed!
            if(!emitter.isCancelled()) {
                emitter.onNext(resource.getContent());
            }
            emitter.onComplete();
        },BackpressureStrategy.BUFFER);
    }
}
