FROM adoptopenjdk/openjdk8:jdk8u292-b10-centos
COPY target/atec2022-0.0.1-SNAPSHOT.jar /home/admin/atec_project/atec2022-0.0.1-SNAPSHOT.jar
COPY run.sh /home/admin/atec_project/run.sh