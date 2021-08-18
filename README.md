# opennms-brannock
A minimalist tool for gathering info about the sizing and performance of an OpenNMS instance

Principal author Jeff Gehlbach.
Huge thanks to Lang Yi and Jesse White for their help getting this thing packaged as a bundle and integrating JDBC support.

*A Brannock device is the gadget used to measure a person's foot to determine shoe size.*

# Building

`mvn clean install`

You'll find a JAR in the `target` directory.

# Usage

Once you have built a JAR, simply copy it into the `OPENNMS_HOME/deploy` directory of a running OpenNMS system.
Karaf will install and start the bundle, and barring any unforeseen trouble, the tool will write its output file as `OPENNMS_HOME/logs/brannock_stats.json`.
The bundle runs once and then stops itself, so there is no lasting impact on your system.
When you are done, delete the JAR from the `deploy` directory; Karaf will uninstall the bundle when the JAR disappears.
A sample output follows.

```json
{
  "finishTime": 1629320905716,
  "startupTime": 1629320905700,
  "brannockErrors": [],
  "derivedData": {
    "pollsCompletedPerSec": 5.962612957184287E-7,
    "metricsPersistedPerSec": 222.10388821728415
  },
  "brannockVersion": "v1",
  "jmxData": {
    "OpenNMS:Name=Queued": {
      "CreatesCompleted": 0,
      "StartTime": 0,
      "DequeuedItems": 0,
      "ElapsedTime": 1629320905705,
      "SignificantOpsEnqueued": 0,
      "SignificantOpsDequeued": 0,
      "DequeuedOperations": 0,
      "Errors": 0,
      "TotalOperationsPending": 0,
      "EnqueuedOperations": 0,
      "SignificantOpsCompleted": 0,
      "PromotionCount": 0,
      "UpdatesCompleted": 0
    },
    "OpenNMS:Name=Pollerd": {
      "ActiveThreads": 0,
      "TaskCompletionRatio": 1,
      "CorePoolThreads": 30,
      "PeakPoolThreads": 30,
      "NumPoolThreads": 30,
      "TaskQueuePendingCount": 0,
      "TasksTotal": 2231730,
      "MaxPoolThreads": 30,
      "TasksCompleted": 2231730,
      "NumPolls": 2231730,
      "TaskQueueRemainingCapacity": 2147483647
    },
    "org.opennms.netmgt.eventd:name=eventlogs.process": {
      "75thPercentile": 25.592758,
      "Mean": 23.385982194448793,
      "StdDev": 3.8147418821828483,
      "98thPercentile": 25.592758,
      "RateUnit": "events/second",
      "95thPercentile": 25.592758,
      "99thPercentile": 30.000935,
      "Max": 351.59396499999997,
      "Count": 141972,
      "FiveMinuteRate": 0.014486228175940093,
      "50thPercentile": 25.592758,
      "MeanRate": 0.03793147534364054,
      "Min": 4.301921,
      "OneMinuteRate": 0.00630943580245027,
      "DurationUnit": "milliseconds",
      "999thPercentile": 34.549198,
      "FifteenMinuteRate": 0.015163108928050254
    },
    "org.opennms.netmgt.flows:name=flowsPersisted": {
      "RateUnit": "events/second",
      "OneMinuteRate": 2.964393875E-314,
      "Count": 1077946460,
      "FifteenMinuteRate": 4.44659081257E-313,
      "FiveMinuteRate": 1.4821969375E-313,
      "MeanRate": 288.003216805817
    },
    "org.opennms.newts:name=repository.samples-inserted": {
      "RateUnit": "events/second",
      "OneMinuteRate": 245.47802678376334,
      "Count": 831303053,
      "FifteenMinuteRate": 233.4112279299726,
      "FiveMinuteRate": 235.12457293712183,
      "MeanRate": 222.10388821728415
    },
    "java.lang:type=Runtime": {
      "VmVendor": "Amazon.com Inc.",
      "Uptime": 3742872489,
      "VmName": "OpenJDK 64-Bit Server VM",
      "StartTime": 1625578033321,
      "VmVersion": "11.0.10+9-LTS",
      "Name": "16281@onmscore"
    },
    "java.lang:type=OperatingSystem": {
      "Version": "4.14.219-161.340.amzn2.x86_64",
      "AvailableProcessors": 2,
      "OpenFileDescriptorCount": 1832,
      "Name": "Linux",
      "TotalPhysicalMemorySize": 8362287104
    }
  },
  "jdbcData": {
    "monitoringLocationCount": 7,
    "minionCount": 8
  }
}
```

# Main things we aim to measure

* Events per second (JMX)
* Metrics persisted per second (JMX)
* Polls per second (JMX)
* Flows persisted per second (JMX)
* Count of monitoring locations (JDBC for now)
* Count of Minions (JDBC for now)
