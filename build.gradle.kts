plugins {
    java
    application
}

group = "de.busam.samples"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("io.reactivex.rxjava3","rxjava","3.0.0-RC4")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}

application{
    mainClassName = "de.busam.samples.rxjava.Main"
}