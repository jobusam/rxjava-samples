package de.busam.samples.rxjava;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Sample 1: The caller of the flowable is responsible
 * for creating and closing the resource!
 */
class Sample1 {

    void execute(){
        System.out.println("Sample 1: Closing the resource externally:");

        //Create resource externally
        Resource res = new Resource();

        createWithResource(res).subscribe(
                onNext -> System.out.println("nextItem: " + onNext),
                onError -> System.err.println("error: " + onError.getCause()),
                () -> System.out.println("Flowable completed")
        );

        //Close resource externally
        res.close();
    }

    private static Flowable<String> createWithResource(Resource resource) {
        FlowableOnSubscribe<String> source = subscribe ->{
            System.out.println("Flowable starting to emit items...");
            subscribe.onNext(resource.getContent());
            subscribe.onComplete();
        };
        return Flowable.create(source, BackpressureStrategy.BUFFER);
    }
}
