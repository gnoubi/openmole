#!/bin/sh
cd web/ && mvn clean install -T4 && cd ../application && mvn clean install -T4
