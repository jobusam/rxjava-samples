package de.busam.samples.rxjava.resourcehandling;

class Resource implements AutoCloseable {

    Resource() {
        System.out.println("Create closable resource");
    }

    String getContent() {
        return "This is the content";
    }

    @Override
    public void close() {
        System.out.println("Close resource");
    }
}

