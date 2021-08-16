#!/usr/bin/env sh

DEFAULT_ONMS_BASE_URL="http://127.0.0.1:8980/opennms"
CURL_BASE_CMD="curl -sS -k -H 'Accept: application/json'"

OUTPUT_DIR=$(mktemp -d)

function getBaseUrl() {
    /bin/echo -n "Base URL of your OpenNMS install [${DEFAULT_ONMS_BASE_URL}]: "
    read ONMS_BASE_URL
    if [ -z $ONMS_BASE_URL ]; then ONMS_BASE_URL=$DEFAULT_ONMS_BASE_URL; fi
    echo $ONMS_BASE_URL > ${OUTPUT_DIR}/onms_base_url.txt
}

function getCredentials() {
    /bin/echo "Please provide a valid username for accessing your OpenNMS server."
    /bin/echo "The user you provide need not have any special privileges, and will"
    /bin/echo "not be included with the collected data."
    /bin/echo
    while [ -z $ONMS_USERNAME ]; do
        /bin/echo -n "Username: "
        read ONMS_USERNAME
    done
    while [ -z $ONMS_PASSWORD ]; do
        /bin/echo -n "Password: "
        stty -echo
        read ONMS_PASSWORD
        stty echo
    done
    CURL_CMD="${CURL_BASE_CMD} -u ${ONMS_USERNAME}:${ONMS_PASSWORD}"
}

function getEventsPerSec() {
    TARGET_URL="${ONMS_BASE_URL}/rest/events?limit=1"
    /bin/echo "Calculating event rate. This involves fetching the highest event-ID"
    /bin/echo "in your system twice, 5 minutes apart, so it will take that long."
    /bin/echo
    /bin/echo -n "Fetching current highest event-ID... "
    $CURL_CMD "$TARGET_URL" | egrep '"id":' > "${OUTPUT_DIR}/events_0.txt"
    /bin/echo "done."
    /bin/echo -n "Pausing for 5 minutes"
    #DEBUG for i in {1..30}; do
    for i in {1..1}; do
        sleep 10
        /bin/echo -n "."
    done
    /bin/echo -n "Fetching new highest event-ID..."
    $CURL_CMD "$TARGET_URL" | egrep '"id":' > "${OUTPUT_DIR}/events_1.txt"
    /bin/echo "done."
}

echo "Welcome to Brannock: it's less hazardous than an x-ray(tm)"
echo

getBaseUrl
getCredentials

getEventsPerSec

echo DEBUG $OUTPUT_DIR $(ls -ltr $OUTPUT_DIR)
