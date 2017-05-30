# CoRE Things
Implementation of CoRE Resource Directory (Internet-Draft).

## Table of contents
1. Installation
    1. Eclipse Kura™
    2. Resource Directory
    3. Endpoint
    4. Admin
2. About

## 1. Installation
### i. Eclipse Kura™
Eclipse Kura™ is a Java/OSGi-based container for IoT systems. It is recommended to use Eclipse Kura™ as a container for CoRE Things bundles as it enables easy deployment and configuration. CoRE Things software was developed and tested with the Eclipse Kura™ version 2.1.0. More information can be found in the official documentation: http://eclipse.github.io/kura

Following page provides Eclipse Kura™ quick installation procedures for the Raspberry Pi: http://eclipse.github.io/kura/intro/raspberry-pi-quick-start.html

Eclipse plugin **mToolkit** enables to manage bundles remotely from Eclipse. Installation guide for this plugin can also be found in the official documentation: http://eclipse.github.io/kura/dev/kura-setup.html#installing-mtoolkit

### ii. Resource Directory
Resource Directory can be started either as a conventional Java application (module **ct-rd**) or as an OSGi bundle inside a container (module **ct-rd-osgi**). CoAP properties can be configured in the **Californium.properties** file which is automatically created and filled with default values by the Californium framework if it doesn't exists.

The **ct-rd-osgi** bundle requires the **californium-osgi** bundle which is downloaded automatically after the artefact is build. It can be found into the **/target/dependency/** directory of the project after maven build process is finished.

### iii. Endpoint
Following three modules need to be deployed to an OSGi container:

- **ct-endpoint-device-osgi**
- **ct-endpoint-server-osgi** (requires **ct-endpoint-device-osgi** and **californium-osgi**)
- **ct-endpoint-pi4j-osgi** (requiers **ct-endpoint-device-osgi** and **pi4j-core**)

Required bundles are automatically downloaded by maven into the **/target/dependency/** directory of the corresponding project after build process is finished. The module **pi4j-core** also requires WiringPi which needs to be installed on the Raspberry Pi.

All configuration files will be automatically created if they do not exist and can be found in following directory:

- **/opt/eclipse/kura_2.1.0_raspberry-pi-2/**

CoAP properties can be configured in the **Californium.properties** and the endpoint properties can be configured in the **Endpoint.properties** file.

Connected sensors can be configured in the **EndpointSensors.csv** file. Following example shows the configuration of all supported sensors:

```
# name, type, WiringPi GPIO pin number
thermostat,corethings.sen.mercurySwitch,1
doorbell,corethings.sen.tactileSwitch,2
motionSensor,corethings.sen.pirSensor,3

# name, type, address, temperature threshold
indoorTemp,corethings.sen.ds18b20,28-0316b39ab5ff,0.1
```

Connected actuators can be configured in the **EndpointActuators.csv** file. Following example shows the configuration of all supported actuators:

```
# name, type, WiringPi GPIO pin number
tableLed,corethings.act.led,0

# name, type, address
lcd,corethings.act.lcd16x2,0x27
```

Following page contains lot of useful information about sensors and actuators on a Raspberry Pi: http://osoyoo.com/2016/04/12/raspberry-pi-3-s

### iv. Admin
Admin GUI can be started on a console as a conventional Java application.

## 2. About
This Software was developed by Martin Vantroba as part of bachelor thesis at the Technical University of Cologne in spring 2017.

Supervisors: Prof. Dr. Hans W. Nissen, Prof. Dr. Gregor Büchel

Authors of the icons used in the GUI:
* Freepik (http://www.freepik.com)
* Google (https://material.io/icons)
* Gregor Cresnar (http://www.flaticon.com/authors/gregor-cresnar)
* Icons8 (http://icons8.com)
* Yannick (http://www.flaticon.com/authors/yannick)
