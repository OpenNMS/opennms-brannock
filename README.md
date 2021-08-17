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
      "ElapsedTime": "1629243009115",
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
      "ActiveThreads": "0",
      "TaskCompletionRatio": "0.0",
      "CorePoolThreads": "30",
      "PeakPoolThreads": "0",
      "NumPoolThreads": "0",
      "TaskQueuePendingCount": "0",
      "TasksTotal": "0",
      "MaxPoolThreads": "30",
      "TasksCompleted": "0",
      "NumPolls": "0",
      "TaskQueueRemainingCapacity": "2147483647"
    },
    "org.opennms.netmgt.eventd:name=eventlogs.process": {
      "75thPercentile": "0.260802",
      "Mean": "0.2620121242533506",
      "StdDev": "1.3791784643605132",
      "98thPercentile": "0.37954",
      "RateUnit": "events/second",
      "95thPercentile": "0.33522199999999996",
      "99thPercentile": "0.5666169999999999",
      "Max": "88.273313",
      "Count": "56",
      "FiveMinuteRate": "0.07943977930534188",
      "50thPercentile": "0.21706999999999999",
      "MeanRate": "0.11280741635919429",
      "Min": "0.11429199999999999",
      "OneMinuteRate": "0.13622650656889723",
      "DurationUnit": "milliseconds",
      "999thPercentile": "2.1512",
      "FifteenMinuteRate": "0.044937132063619975"
    },
    "org.opennms.netmgt.flows:name=flowsPersisted": {
      "RateUnit": "events/second",
      "OneMinuteRate": "0.0",
      "Count": "0",
      "FifteenMinuteRate": "0.0",
      "FiveMinuteRate": "0.0",
      "MeanRate": "0.0"
    },
    "java.lang:type=Runtime": {
      "VmVendor": "Red Hat, Inc.",
      "Uptime": "509747",
      "VmName": "OpenJDK 64-Bit Server VM",
      "StartTime": "1629242499374",
      "VmVersion": "11.0.12+7-LTS",
      "Name": "6338@meridian-2021-1-3"
    },
    "java.lang:type=OperatingSystem": {
      "Version": "3.10.0-1127.el7.x86_64",
      "AvailableProcessors": "2",
      "OpenFileDescriptorCount": "1450",
      "Name": "Linux",
      "TotalPhysicalMemorySize": "1927102464"
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
