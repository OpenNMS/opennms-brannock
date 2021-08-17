# opennms-brannock
A quick and dirty tool for gathering info about the sizing and performance of an OpenNMS instance

**A Brannock device is the gadget used to measure a person's foot to determine shoe size.**

# Things we aim to measure

* Events per second (eps)
* Metrics persisted per second (mps)
* Polls per second (pps)
* Flows persisted per second (fps)
* Count of monitoring locations (loc)
* Count of Minions (mnc)

Going to do all these things via JMX through the local MBeanServer.

It's not Mavenized yet, so:

 `javac -cp $HOME/.m2/repository/org/json/json/20200518/json-20200518.jar org/opennms/netmgt/brannock/Brannock.java`
 `java -cp .:$HOME/.m2/repository/org/json/json/20200518/json-20200518.jar  org.opennms.netmgt.brannock.Brannock | jq .`
