# DotEnv-Java-IBMi
A `.env` implementation with convenience functions for IBM i

This is a thin wrapper around the well-designed [dotenv-java](https://github.com/cdimascio/dotenv-java)
project. 

The objectives of this library are:
- Make it easier to do cross-platform activities with a single codebase
- Unify best practices for acquiring connections to the IBM i system
- Avoid storage of authentication data in source code

## Installation

**Maven Users**

add the following to your `pom.xml` file (substitute version number to the latest available):
```xml
<dependency>
    <groupId>io.github.theprez</groupId>
    <artifactId>dotenv-java-ibmi</artifactId>
    <version>0.0.1</version>
</dependency>
```

**Non-Maven Users**

Download the `-with-dependencies.jar` file from [Maven Central](https://mvnrepository.com) and
add to your classpath.

## Usage

Access of `.env` data is done through use of the `Dotenv` object. The `Dotenv`
class is provided by [the underlying implementation](https://github.com/cdimascio/dotenv-java),
but, but instances should be acquired through the `IBMiDotEnv.getDotEnv()` helper function for
consistent behavior with the IBM i helper functions in this library. 

```java
IBMiDotEnv.getDotEnv();
```

This assumes a `.env` file exists in your current working directory. The `.env` file is in the
common `KEY=VALUE` format. 
Note: values can also be specified in environment variables. 

### Getting new IBM i connections through this library

Currently, this library provides two convenience functions:
- `getNewSystemConnection()`, which will return a new `AS400` object. The object will not be cached
- `getCachedSystemConnection()`, which will return a cached `AS400` object if one is available. If
a cached object is not available, a new one will be constructed and cached. 

The above convenience functions are implemented assuming the following values in your `.env` file:
- `IBMI_HOSTNAME`
- `IBMI_USERNAME`
- `IBMI_PASSWORD`

These functions take a single parameter (`_starCurrentIfPossible`). When this parameter is set to
`true`, the API will return an `AS400` object with the username and password set to `*CURRENT` if:
- running on IBM i
- a username and password are not explicitly provided

### Example

```java
// Function to get a JDBC Connection object
private Connection getConnection() throws SQLException, IOException, AS400SecurityException {
    final AS400 as400 = IBMiDotEnv.getCachedSystemConnection(true);
    AS400JDBCDataSource ds = new AS400JDBCDataSource(as400);
    return ds.getConnection();
}
```
