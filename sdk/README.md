# sdk

## Publishing

### Maven Local

To deploy to Maven Local, run the following command:
```shell
./gradlew publishToMavenLocal
```

### Maven Central

Hopefully you will usually deploy through Github Actions - but to do so manually follow these steps:

Create a GPG key and publish it, you can follow the instructions [here](https://central.sonatype.org/pages/working-with-pgp-signatures.html).

Create a `gradle.properties` file in `~/.gradle/` with the following content:
```properties
mavenCentralUsername=<User token username>
mavenCentralPassword=<User token password>

signing.keyId=........ (last 8 characters of the GPG key ID)
signing.password=...... (passphrase)
signing.secretKeyRingFile=/Users/<your_username>/.gnupg/secring.gpg
```

Then run the following command:
```shell
./gradlew publishAllPublicationsToMavenCentralRepository
```

More details can be found in [this guide](https://vanniktech.github.io/gradle-maven-publish-plugin/central).