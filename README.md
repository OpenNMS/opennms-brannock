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


## Events per second (eps)

Best we can do, I think, is poll `/api/v2/events/count` or `/rest/events/count` a few times and measure the delta.

## Metrics persisted per second (mps)

Where JMX self-monitoring is enabled, we can get a feel for this item by looking at the collected data for one of two MBean properties.

On instances persisting timeseries data to Newts, use `NewtsSmplsInsertd`. For RRD instances, use `ONMSQueUpdates`.

## Polls per second (pps)

Where JMX self-monitoring is enabled, use `ONMSPollCount`.

## Flows persisted per second (fps)

Where JMX self-monitoring is enabled, use `FlowPerst5m`.

## Count of monitoring locations (loc)

Use `/api/v2/monitoringLocations` or `/rest/monitoringLocations`, value of top-level `count` key (using `/count` in URL seems broken)

## Count of Minions (mnc)

Easy, just ask for `/api/v2/minions/count` or `/rest/minions/count` where API v2 is missing.

# How to sniff out available avenues

## Availability of collected JMX self-monitor data

Use `/rest/nodes?filterRule=isOpenNMS-JVM` to identify nodes that could be the one we want. In case of multiple hits, prompt user to specify the right one?

## Availability of v2 REST API

Just try each v2 endpoint in turn as a first pass, make a note of whether it is present, refer back in data-gathering pass
