RestFixture: A FitNesse fixture for testing REST services
=========================================================

The RestFixture is a FitNesse (http://fitnesse.org)  fixture that allows
developers and/or product owners to write test fixtures for REST services
with simplicity in mind. The idea is to write tests that are self
documenting and easy to write and read, without the need to write Java code.

The fixture allows test writers to express tests as actions (any of the
allowed HTTP methods) to operate on resource URIs and express expectations on
the content of the return code, headers and body. All without writing one
single line of Java code.

And it also works as a living/executable documentation of the API.

Overview:

    * Get Fitnesse With Some Rest
      (http://smartrics.blogspot.com/2008/08/get-fitnesse-with-some-rest.html)
    * Rest Fixture, Latest Additions
      (http://smartrics.blogspot.com/2008/11/restfixture-latest-additions.html)
    * Rest Fixure with namespaces support
      (http://smartrics.blogspot.com/2011/01/restfixture-with-namespaces-support.html)

Documentation:

    * http://rest-fixture.googlecode.com/files/RestFixture-docs-<ver>.zip
    * http://rest-fixture.googlecode.com/files/RestFixture-live-<ver>.html

Build
-----
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

Note on dependencies
--------------------
RestFixture distribution contains the file etc/restfixture/sequence.pic
part of UMLGraph (http://www.umlgraph.org), distributed under BSD license (see
etc/restfixture/BSDLICENSE)

Install
-------

To use the RestFixture, simply add RestFixture-<ver>.jar to your FitNesse tests classpath,
alongside its dependencies. Dependencies are bundled in the RestFixture-<ver>.zip, or
in the lib/ directory.

For example,

1) Unzip the RestFixture-<ver>.zip
    let's assume that you have unzipped it in C:\lib\RestFixture
1) Start FitNesse 
    let's assume that fitnesse is now running on port 8090)
2) Go to http://localhost/RestFixtureInstallTest 
    this will create a new test page
3) Type the following:

!define TEST_SYSTEM {slim}

!path C:/lib/RestFixture/lib/*
!path C:/lib/RestFixture/RestFixture.jar

|Table:smartrics.rest.fitnesse.fixture.RestFixture | http://localhost:8090 |
| GET | /RestFixtureInstallTest?rss | | | //title[text()='RestFixtureInstallTest']|
 
4) Execute the test
    If it passes, you have succesfully installed the RestFixture. You'll also know how to reference it's jar and dependencies.
