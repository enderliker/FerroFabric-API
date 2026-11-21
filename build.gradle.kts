plugins { `java-library` }

group = "dev.ferroloom"
version = "26.3-ferro.1"

// FerroLoom provides the shim (`net.minecraft.*`) and the runtime bridge this API compiles
// against. Point -PferroRepo or FERROLOOM_REPO at its checkout when it is not a sibling folder.
val ferroRepo = file(
    providers.gradleProperty("ferroRepo")
        .orElse(providers.environmentVariable("FERROLOOM_REPO"))
        .orElse("../FerroLoom")
        .get()
)
val runtimeJar = ferroRepo.resolve("java/ferroloom-runtime/build/libs/ferroloom-runtime.jar")
val shimJar = ferroRepo.resolve("java/ferroloom-shim/build/libs/ferroloom-shim.jar")

repositories { mavenCentral() }

dependencies {
    compileOnly(files(runtimeJar, shimJar))
    testImplementation(files(runtimeJar, shimJar))
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java { withSourcesJar() }

tasks.withType<JavaCompile> { options.release.set(25) }

tasks.jar {
    archiveBaseName.set("ferrofabric-api")
    manifest {
        attributes(
            "Implementation-Title" to "FerroFabric API",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "FerroLoom contributors",
        )
    }
}

tasks.test { useJUnitPlatform() }