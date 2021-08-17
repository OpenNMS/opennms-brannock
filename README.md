# opennms-brannock
A minimalist tool for gathering info about the sizing and performance of an OpenNMS instance

**A Brannock device is the gadget used to measure a person's foot to determine shoe size.**

# Usage

Once you have built a JAR, simply copy it into the `OPENNMS_HOME/deploy` directory.
Karaf will install and start the bundle, and barring any unforeseen trouble, the tool will write its output file as `OPENNMS_HOME/logs/brannock_stats.json`.
A sample output follows.

```json
{
  "brannockVersion": "v1",
  "jmxData": {
    "OpenNMS:Name=Queued": {
      "CreatesCompleted": "0",
      "StartTime": "0",
      "DequeuedItems": "0",
      "ElapsedTime": "1629243514632",
      "SignificantOpsEnqueued": "0",
      "SignificantOpsDequeued": "0",
      "DequeuedOperations": "0",
      "Errors": "0",
      "TotalOperationsPending": "0",
      "EnqueuedOperations": "0",
      "SignificantOpsCompleted": "0",
      "PromotionCount": "0",
      "UpdatesCompleted": "0"
    },
    "OpenNMS:Name=Pollerd": {
      "ActiveThreads": "1",
      "TaskCompletionRatio": "0.9999995432571973",
      "CorePoolThreads": "30",
      "PeakPoolThreads": "30",
      "NumPoolThreads": "30",
      "TaskQueuePendingCount": "0",
      "TasksTotal": "2189416",
      "MaxPoolThreads": "30",
      "TasksCompleted": "2189415",
      "NumPolls": "2189416",
      "TaskQueueRemainingCapacity": "2147483647"
    },
    "org.opennms.netmgt.eventd:name=eventlogs.process": {
      "75thPercentile": "17.829575",
      "Mean": "19.82412585742063",
      "StdDev": "5.3137949086351215",
      "98thPercentile": "24.773691",
      "RateUnit": "events/second",
      "95thPercentile": "24.773691",
      "99thPercentile": "48.929044999999995",
      "Max": "339.6596",
      "Count": "140964",
      "FiveMinuteRate": "0.011125178727385715",
      "50thPercentile": "17.829575",
      "MeanRate": "0.03845734540163717",
      "Min": "4.1572379999999995",
      "OneMinuteRate": "0.007144365613377293",
      "DurationUnit": "milliseconds",
      "999thPercentile": "67.752331",
      "FifteenMinuteRate": "0.01071415077980683"
    },
    "org.opennms.netmgt.flows:name=flowsPersisted": {
      "RateUnit": "events/second",
      "OneMinuteRate": "2.964393875E-314",
      "Count": "1077946460",
      "FifteenMinuteRate": "4.44659081257E-313",
      "FiveMinuteRate": "1.4821969375E-313",
      "MeanRate": "294.08404133231045"
    },
    "org.opennms.newts:name=repository.samples-inserted": {
      "RateUnit": "events/second",
      "OneMinuteRate": "238.5283472098449",
      "Count": "813309259",
      "FifteenMinuteRate": "232.90315844111245",
      "FiveMinuteRate": "233.71845410848283",
      "MeanRate": "221.88428832655137"
    },
    "java.lang:type=Runtime": {
      "VmVendor": "Amazon.com Inc.",
      "Uptime": "3665481416",
      "VmName": "OpenJDK 64-Bit Server VM",
      "StartTime": "1625578033321",
      "VmVersion": "11.0.10+9-LTS",
      "Name": "16281@onmscore"
    },
    "java.lang:type=OperatingSystem": {
      "Version": "4.14.219-161.340.amzn2.x86_64",
      "AvailableProcessors": "2",
      "OpenFileDescriptorCount": "1825",
      "Name": "Linux",
      "TotalPhysicalMemorySize": "8362287104"
    }
  }
}
```

# Things we aim to measure

* Events per second
* Metrics persisted per second
* Polls per second
* Flows persisted per second
* Count of monitoring locations (TODO)
* Count of Minions (TODO)

All but the last two we can fetch via JMX through the local MBeanServer.
