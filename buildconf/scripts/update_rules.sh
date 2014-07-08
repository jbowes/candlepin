#!/bin/bash

SERVERURL=https://localhost:8443/canadianTenPin/rules
SRCDIR=~/src/canadianTenPin/

base64 $SRCDIR//src/main/resources/rules/rules.js > $SRCDIR/buildconf/rules.base64
curl  -H "Accept:text/plain" -H "Content-type: text/plain"  -k -u admin:admin -X POST $SERVERURL --data @$SRCDIR/buildconf/rules.base64
