dependencies {
    implementation(project(":api"))
}

tasks.register<Copy>("installScript") {
    dependsOn(tasks.jar)
    from(tasks.jar.get().archiveFile)
    into(rootProject.layout.projectDirectory.dir("scripts"))
}

tasks.named("build") {
    finalizedBy("installScript")
}
