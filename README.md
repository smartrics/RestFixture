RestFixture: A FitNesse fixture for testing REST services
=========================================================

Master branch build status: [![Master Branch Build Status](https://travis-ci.org/smartrics/RestFixture.svg?branch=master)](https://travis-ci.org/smartrics/RestFixture)

The RestFixture is a FitNesse (http://fitnesse.org)  fixture that allows
developers and/or product owners to write test fixtures for REST services
with simplicity in mind. The idea is to write tests that are self
documenting and easy to write and read, without the need to write Java code.

The fixture allows test writers to express tests as actions (any of the
allowed HTTP methods) to operate on resource URIs and express expectations on
the content of the return code, headers and body. All without writing one
single line of Java code.

And it also works as a living/executable documentation of the API.

The fixture can be used with both Fit (FitRestFixture) and Slim (Table:Rest Fixture) runners.

Overview:

* Get Fitnesse With Some Rest (http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html)
* Rest Fixture, Latest Additions (http://smartrics.blogspot.com/2008/11/restfixture-latest-additions.html)
* Rest Fixure with namespaces support (http://smartrics.blogspot.com/2011/01/restfixture-with-namespaces-support.html)

Documentation:

http://github.com/smartrics/RestFixtureLiveDoc


Versions and compatibility with FitNesse
----------------------------------------

RestFixture v3.1 and below are compatible with any release of FitNesse to date
([v20150424](http://mvnrepository.com/artifact/org.fitnesse/fitnesse/20150424)) and no further support for newer versions
of FitNesse is guraranteed for these versions of RestFixture.

RestFixture v4.0 is compatible with [FitNesse v20150424](http://mvnrepository.com/artifact/org.fitnesse/fitnesse/20150424)
and above having implemented proper support of Slim Symbols as described in [Issue #52](https://github.com/smartrics/RestFixture/issues/52).

Test with the latest FitNesse version
-------------------------------------

The profile `latest-fitnesse` configures the Versions plugin to fetch the latest FitNesse version. Tests can be executed to verify regression with:

> mvn clean versions:use-latest-releases test -P latest-fitnesse

Build: Released from 4.2 onwards
--------------------------------

Two new profiles are available: 'bundle' and 'release' 

### Build the RestFixture jar ###### 

> mvn clean package

Once the build completes, the directory target contains:

* <code>smartrics-RestFixture-&lt;version>.jar</code> : the RestFixture jar

### Build the RestFixture bundle ###### 

> mvn clean package -P bundle 

Once the build completes, the directory target contains:

* <code>smartrics-RestFixture-&lt;version>.jar</code> : the RestFixture jar
* <code>smartrics-RestFixture-&lt;version>-sources.jar</code> : the RestFixture sources jar
* <code>smartrics-RestFixture-&lt;version>-javadoc.jar</code> : the RestFixture javadoc jar
* <code>smartrics-RestFixture-&lt;version>-bin.zip</code>: a bundle the RestFixture and all dependencies.
* <code>dependencies/</code> : all the RestFixture dependencies (excluding a Logging framework implementation. See below for details)


Build: Releases from 2.1 to 4.1
-------------------------------

To build RestFixture install Maven and execute

> mvn clean package

Once the build completes, the directory target contains:

* <code>smartrics-RestFixture-&lt;version>.jar</code> : the RestFixture jar
* <code>dependencies/</code> : all the RestFixture dependencies (excluding a Logging framework implementation. See below for details)
* <code>smartrics-RestFixture-&lt;version>-bin.zip</code>: a bundle the RestFixture and all dependencies.

If the build fails because smartrics-RestClient can't be found, check the pom.xml file and make sure the repository
sonatype-releases-restclient is enabled.

Build: Releases up to 2.0
-------------------------

To build RestFixture add a property file in the properties directory named <your.os.username>.properties 
by copying and customising build.properties if necessary.

Use

> ant

or

> ant full

to run the default target (this will build the Rest Fixture, start a local instance
of FitNesse on port 7070 and run the fitnesse tests in src/test/cat. 

You can also pass a build properties file to ant with 

> ant -Dproperties=<my.bespoke.file>.properties

If it all succeeds a distribution of the RestFixture is available in dist/ alongside with the latest 
documentation. Reports of tests and metrics are available in build/reports

The (missing!) logger framework dependency
------------------------------------------

The RestFixture uses slf4j-api; if no logger implementation is provided slf4j defaults to nop binding. 
Please download and add to the classpath your binding of choice (and the respective configuration file).

The current version is slf4j-api-1.6.6, hence download the matching version of the binding implementation.

Check http://www.slf4j.org/ for more details.

Install
-------

To use the RestFixture, simply add RestFixture-<ver>.jar to your FitNesse tests classpath,
alongside its dependencies. Dependencies are available in the directory target/dependencies.

For example, let's assume that you have succesfully built the RestFixture in C:/RestFixture

0. Download a binding logger implementation for slf4j and copy it into C:/slf4j-simple/slf4j-simple-1.6.6.jar. Download it from http://repo2.maven.org/maven2/org/slf4j/slf4j-simple/1.6.6/slf4j-simple-1.6.6.jar
1. Start FitNesse; let's assume that fitnesse is now running on port 8090)
2. Go to http://localhost:8090/RestFixtureInstallTest to create a new test page
3. Type the following:

<pre>
!define TEST_SYSTEM {slim}

!path C:/RestFixture/target/dependencies/*
!path C:/RestFixture/target/smartrics-RestFixture-&ltver>.jar
!path C:/slf4j-simple/slf4j-simple-1.6.6.jar

|Table:smartrics.rest.fitnesse.fixture.RestFixture | http://localhost:8090 |
| GET | /RestFixtureInstallTest?rss | | | //title[text()='RestFixtureInstallTest']|
</pre>
 
4. Execute the test. If it passes, you have succesfully installed the RestFixture. You'll also know how to reference it's jar and dependencies.
 
