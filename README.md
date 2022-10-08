# Cali-Lang Mule Extension

This Mule extension provides the cali-lang interpreter support to the
Mule 4 environment. It's easy to use and provides a rich coding experience.

Click [here to view the cali-lang documentation](https://github.com/cali-lang/cali.lang.base/wiki)
for more information about the language and how to use it.

## Add it to a Mule 4 project

Add the following dependency to your pom.xml and you should then see
a "Cali-Lang" module within the Mule Palette. You can then drag and drop the
'Cali Lang Transform' connector into a flow to configure it.

```
<groupId>io.github.rsv-code</groupId>
  <artifactId>cali-lang-mule-connector</artifactId>
  <version>1.0.2</version>
<classifier>mule-plugin</classifier>
```

## Configuring the Transform

Once you drop in the transform object you will need to specify a cali-lang
script file to use in the 'Script File Name' field. The script is a file which
should exist in the src/main/resources path of your project. For instance if you
create a script file in src/main/resources/ca called hello-world.ca then you
would put ca/hello-world.ca in this field to tell the interpreter that's the
script file you want to run.

Optionally you can also set the Logger name. If you setup a logger in
log4j2.xml you can provide the name in this field and any log statements you
make in the cali-lang scripts will use this logger.

## Hello World

Here's some basic code to get you started.

```
class helloWorld {
  public main() {
    return "hello world";
  }
}
```

## Demo Application

If you'd like to test out an existing demo application download and import the [calilangmuletest](https://github.com/rsv-code/calilangmuletest). This minimal app will provide a basic getting started experience.


## Available Objects

Along with the default language objects that can be found in [the standard
documentation](https://github.com/cali-lang/cali.lang.base/wiki) the following
objects are available in the mule connector.

#### env

The env object is a mule specific static global object that can be used anywhere.
It contains the flow variables, attributes, and payload. It also contains a number
of useful functions.

**Objects available in env:**
```
env.payload - A map which contains the payload information.
env.variables - A map which contains the flow variables.
env.attributes - A map which contains the flow attributes.
```

**Functions available in env:**
```
env.p("propName") - Gets the property value with the provided property name.
env.loadResource("resource.json") - Loads a resource from the application
src/main/resources directory and returns it as a string.
env.toString() - Returns a string representation of attributes, variables,
and payload.
```

### http

The http object provides basic HTTP request support.

**Functions available in http:**
```
// Include the http library.
include http;

// Create a new object.
con = new http();

// Use the object.
con.get(string Url) - Makes a HTTP GET request with the provided URL.
con.post(string Url, string Content, string MediaType = "application/json; charset=utf-8") - Makes a HTTP POST request with the provided arguments. The MediaType argument is
optional and if not set defaults to application/json; charset=utf-8.
```

### jdbc

The jdbc object provides Java JDBC relational database support.

**Functions available in jdbc:**
```
// Include the jdbc library.
include jdbc;

// Create a new object.
con = new jdbc();

// Use the object.
con.setDriver(string Driver) - Sets the driver string.
con.setUrl(string Url) - Sets the connection URL.
con.setUserName(string UserName) - Sets the connection user name.
con.setPassword(string Password) - Sets the connection password.
con.setConnectionInfo(string Driver, string Url, string UserName, string Password) - Sets all of the connection information.
con.select(string Query, list Params = []) - Executes a select query.
con.update(string Query, list Params = []) - Executes a update query.
con.connect() - Establishes a DB connection.
con.disconnect() - Terminates a DB connection.
```

## Installing the dependency locally

Ensure Java 8 is set at JAVA_HOME.

```
> JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
> mvn clean install -DskipTests
```

## License

Copyright 2022 Austin Lehman (austin@rosevillecode.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
