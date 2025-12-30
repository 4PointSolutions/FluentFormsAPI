FROM aem:aem65lts_sp1 AS ff_it_test

ENV container="aem-lts-quickstart,aem-author,ubuntu,java21"

# Switch to AEM user 
USER aem_user

RUN mkdir -p /opt/adobe/ff_it_files

COPY ff_it_files/* /opt/adobe/ff_it_files

RUN /bin/bash -c "/opt/adobe/AEM*/runStart ; \
jbang --java=25 /opt/adobe/aem_cntrl-0.0.2-SNAPSHOT.jar wflog --startup ; \
jbang --java=25 /opt/adobe/ff_it_files/deploy_it_assets.jar ; \ 
/opt/adobe/AEM*/runStop ; \
jbang --java=25 /opt/adobe/aem_cntrl-0.0.2-SNAPSHOT.jar wflog --shutdown"

#NOTE: make sure to copy admin.password.file and license.properties files to the /opt/aem-config folder.
# VOLUME ["/opt/aem-config/"]
#VOLUME ["/opt/adobe/???/crx-quickstart/logs"]
EXPOSE 4502

#Command below is executed at runtime, instead of build
# CMD /bin/bash
# To set the admin password to something other than default, add -Dadmin.password.file=adminpassword.properties and to the java command line below
# and place admin.password = <password> in a file called adminpassword.properties within the aem directory.
# see https://experienceleague.adobe.com/docs/experience-manager-65/administering/security/security-configure-admin-password.html?lang=en
# CMD /bin/bash -c "cp -v /opt/aem-config/* /opt/aem; cd /opt/aem/ ;  java -Xms1024m -Xmx2048m --add-opens=java.desktop/com.sun.imageio.plugins.jpeg=ALL-UNNAMED --add-opens=java.base/sun.net.www.protocol.jrt=ALL-UNNAMED --add-opens=java.naming/javax.naming.spi=ALL-UNNAMED --add-opens=java.xml/com.sun.org.apache.xerces.internal.dom=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/jdk.internal.loader=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED -Dnashorn.args=--no-deprecation-warning -jar ./AEM_6.5_Quickstart.jar -v -nointeractive"
CMD /bin/bash -c "cd /opt/adobe/AEM* ; eval '$(jbang jdk java-env 21)' ;  \
java -Xms1024m -Xmx2048m \
--add-opens=java.desktop/com.sun.imageio.plugins.jpeg=ALL-UNNAMED \
--add-opens=java.base/sun.net.www.protocol.jrt=ALL-UNNAMED \
--add-opens=java.naming/javax.naming.spi=ALL-UNNAMED \
--add-opens=java.xml/com.sun.org.apache.xerces.internal.dom=ALL-UNNAMED \
--add-opens=java.base/java.lang=ALL-UNNAMED \
--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED \
--add-opens=java.base/java.net=ALL-UNNAMED \
-Dnashorn.args=--no-deprecation-warning \
-Djava.awt.headless=true \
-Dsling.run.modes=author,crx3,crx3tar \
-Djava.locale.providers=CLDR,JRE,SPI \
-jar crx-quickstart/app/cq-quickstart-6.6.1-standalone-quickstart.jar \
start \
-c crx-quickstart \
-i launchpad \
-p 4502 \
-Dsling.properties=conf/sling.properties"

