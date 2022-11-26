#!/bin/bash

java -Xmx6G -Xms6G -Xmn3G -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC -XX:+AlwaysPreTouch -XX:+UseFastAccessorMethods -jar atec2022-0.0.1-SNAPSHOT.jar
