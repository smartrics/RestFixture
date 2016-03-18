# Release the RestFixture ###### 

To release RestFixture follow detailed instructions here http://central.sonatype.org/pages/ossrh-guide.html.

In a nutshell

* Configure Maven settings.xml with the details of the jira account on Sonatype. The server `<id>` must match the `<id>` in the pom.xml `distributionManagement/repository`
>        <server>
>            <id>ossrh</id>
>            <username>{username}</username>
>            <password>{password}</password>
>        </server>
* PGP must be configured correctly as described here http://central.sonatype.org/pages/working-with-pgp-signatures.html
* Kick off the build. 
All things going OK, the PGP passphrase will be required to complete and deploy to Sonatype.
Because the `nexus-staging-maven-plugin` is configured with `autoReleaseAfterClose=true` the successful build will deploy to Maven Central
> mvn clean deploy -P bundle,release


