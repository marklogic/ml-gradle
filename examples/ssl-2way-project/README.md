This project shows how to create an SSLContext to support deploying modules when 2-way SSL is configured for an app server.

2-way SSL requires a certificate template to be configured on the MarkLogic app server, "ssl require client certificate" set to true and one or more "ssl client certificate authorities" selected indicating which CAs the client certificates must be signed by.

Follow instructions here <https://docs.marklogic.com/guide/security/SSL> to setup SSL for the MarkLogic app server. 

To make this work, you will need a certificate authority (CA) that can sign certificates for you. You can either use a known 3rd party or setup your own CA for testing. The rest of the instructions assume that the same CA is used to sign both the client and the server certificates.

### Create a server certificate
If the server does not yet have a certificate signed by your CA, you will need to get one and imort it to the server. If you already have server certificated signed by the CA, you can skip this step.

#### Generate and download a certificate request (CSR)

Using the certificate template created above, generate and download a certificate request (CSR). Use that CSR to request a certificate from the CA (or generate one yourself if you have your own CA).

_Important: Your CSR will have the values for country, state/province, city/town, organization, organizational unit and email address from the certificate template. Your CA will have requirements for what must be in each of those fields. If using your own CA to sign certificates, you will need to configure openssl to be able to process CSRs with the values from the template or set the template values to the values needed for your openssl CA configuration before generating the server CSR._  

The following are helpful resources if considering setting up your own CA for testing:
* <https://www.area536.com/projects/be-your-own-certificate-authority-with-openssl/>
* <https://jamielinux.com/docs/openssl-certificate-authority/create-the-root-pair.html>

#### Import the signed server certificate
Once you have the signed server certificate, import it into the server using the "Import" tab of the certificate template used to generate the CSR.

_Important: The CN in the server cert will be the hostname of the MarkLogic host as seen from the "Hosts" list in the admin UI. This will need to be the hostname you use to connect to MarkLogic if hostname verification is being used._

### Create a client certificate
Since we are using ml-gradle which uses the MarkLogic Java Client under the covers, we need to use the Java SSL libraries to setup the client SSL configuration. Java uses a "keystore" to manage client and CA certificates. We will use the Java _keytool_ commandline tool to setup a keystore for the client.

The follow instructions were guided by the following helpful resources:
* <https://docs.oracle.com/cd/E19509-01/820-3503/ggfen/index.html>
* <https://www.digitalocean.com/community/tutorials/java-keytool-essentials-working-with-java-keystores>

#### Create a keystore
```
keytool -keystore clientkeystore -genkey -alias client
```

You will be prompted for a password for the keystore. Remember this and use it as the `mlKeystorePassword` in the `gradle.properties` file.

You will be propted to enter values for your name (CN), country, state/province, city/town, organization and organizational unit. Enter values as required by your CA but make sure to use the MarkLogic username that will be using the certificate when propted for "your first and last name". This is the CN stored in the certificate.

_Important: The CN in client certificate needs to match the user name that you will use to connect to MarkLogic or authentication will fail._

If you want a different password on the client certificate, enter one when prompted and use this as the `mlKeystoreCertPassword` in the `gradle.properties` file. Otherwise, hit enter to use the same password as the keystore.

When complete, this will create a file called `clientkeystore` (you can name this file whatever you want though). Use this as the value for `mlKeystore` in the `gradle.properties` file.

#### Create a client CSR
```
keytool -keystore clientkeystore -certreq -alias client -keyalg rsa -file client.csr
```

#### Generate a signed client certificate
Use the CSR to have a CA generate a signed client certificate (or generate one using your own CA)

#### Import the CA certificate
```
keytool -import -keystore clientkeystore -file ca.crt -alias theCARoot
```

#### Import the signed client certificate
```
keytool -import -keystore clientkeystore -file client.crt -alias client
```

Use the client keystore filename as the value for `mlKeystore` in the `gradle.properties` file.
