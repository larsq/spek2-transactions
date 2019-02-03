# spek2-transactions

## Why

This is a simple proof-of-concept to extend Spek2
(https://spekframework.org/) with support with spring transactions

Spek2 is a DSL based test framework for Kotlin and I lacked support for testing parts that required a
living transaction without creating TransactionTemplate explicitly.

## How

See the the test class for example. The magic is added by the extension scope tx {} which will create an explicit
transaction for each before/beforeEach/afterEach/after methods as well for the 'it' block. In addition I created
variables for the applicationContext and entityManager which are often recurring in these kind of tests

## Limitation

By some reason, the entityManger and applicationContext is not implicitly available in the 'it' block which was
a pity since the variable must be re-assigned in the describe block. Suggestions are welcome how to improve the code

Keywords: Kotlin, Micronaut, JPA, Spek2

## Running
Run with ```gradle test``` to run. The application is not relevant although it can be started as an application.
