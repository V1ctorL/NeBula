# NeBula Backend

NeBula is a distributed online shopping system, which is a coursework of the course *Distributed Systems*, developed by a group of students of LUC@BJTU.

The name is given due to the name of the IDE designated by the professor, NetBeans, which could be simply noted as NB. We wanted to achieve a similar effect, and NeBula seems to be a good name.

This repository contains the backend services of NeBula.

There are also a [web client](https://github.com/V1ctorL/NeBulaWebClient) and a [starter](https://github.com/V1ctorL/NeBulaStarter).

## Install

You can get each part of the code via the related commends.

**The Backend Services**

```shell
git clone git@github.com:V1ctorL/NeBula.git
```

**The Web Client**

```shell
git clone git@github.com:V1ctorL/NeBulaWebClient.git
```

**The Starter**

```shell
git clone git@github.com:V1ctorL/NeBulaStarter.git
```

For this backend project, the database should also be imported. The `.sql` file is provided, you can run the following commends in the MySQL Commend Line Client.

```mysql
CREATE DATABASE NeBula DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
USE NeBula;
SOURCE path/to/nebula.sql
```

If error occurs, you can re-run the last line of the commends, and then it should be ok.

## Usage

You can run the projects which your preferred IDE. Since they are developed with NetBeans, it is a recommended one.

A setting file is provided, whose location is `/src/java/team/v1ctorl/nebula/Settings.java`. You can easily customize the project by modifying the properties in it.

### Developer Mode

The backend interfaces are designed in the RESTful way, and thus some operations are NOT recommended, such as sending user information as parameters in the URL, although it is surely convenient for programmers to do so while developing and testing.

```
https://localhost:8181/NeBula/login?username=example_name&password=example_password
```

If you really want to do so, you can enable it by switching the field `DEVELOPER_MODE` from `false` (the default one) to `true` (again, it is not recommended, especially in production environments).

### Database

The default setting is using MySQL with version above 8.0, hence the driver is set to be

```java
com.mysql.cj.jdbc.Driver
```

If you are using MySQL with version below 8.0, you should change it to be

```java
com.mysql.jdbc.Driver
```

Or if you are using other databases, then edit the driver to be the one you are using.

The `NAME` field refers to the name of the database to connect.

Plus, remember to alter the database username, set in the field `USER`, and password, set in the field `PASSWORD`, to be yours.

### Kafka

Kafka is enabled by default in aim of replicating data among servers, because this is a distributed system.

If you just want to run the program on a single machine, you can disable Kafka by setting `ENABLE_KAFKA` to be `false`.

If you have your own Kafka servers, remember to fill your server IPs with ports in `BOOTSTRAP_SERVERS`.

If you are deploying this project on multiple machines, remember to number them and set it in the `Consumer.GROUP_ID` field. Each instance of the backend server is designed to be both producer and consumer, and this field is used to identify whether a Kafka event is produced by a consumer itself.

You can also customize and edit other Kafka setting as you wish.

### HTTPS

HTTPS is enabled. However, in this case, you may have to deal with extra settings in the server, such as GlassFish (designated for this coursework) or Tomcat. If you just want to work with HTTP only, you can remove the label `<security-constraint>` together with its children labels and their contents in the file `web.xml`. All the codes that enables HTTPS and can be removed are shown as follows.

```xml
    <security-constraint>
        <web-resource-collection>
            <web-resource-name>*</web-resource-name>
            <url-pattern>/*</url-pattern>
        </web-resource-collection>
        <user-data-constraint>
            <transport-guarantee>CONFIDENTIAL</transport-guarantee>
        </user-data-constraint>
    </security-constraint>
```



## Dependencies

There are some external libraries on which this project relies, whose packages are in the directory `/lib`.

The dependencies and related packages are

- MySQL (Connector/J driver)
  - mysql-connector-java-8.0.26.jar
- Jackson
  - jackson-annotations-2.11.4.jar
  - jackson-core-2.11.4.jar
  - jackson-databind-2.11.4.jar
- Kafka
  - kafka-clients-3.0.0.jar
  - slf4j-api-1.7.30.jar