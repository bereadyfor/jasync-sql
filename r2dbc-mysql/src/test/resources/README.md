The *.pem files are similar to the ones generated by mysql_ssl_rsa_setup, but with a modified CN and for client keys that will load in the JVM.

```bash
echo "basicConstraints=CA:TRUE" > cav3.ext
echo "basicConstraints=CA:FALSE" > certv3.ext
# Generate the CA files for the server
openssl req -newkey rsa:2048 -days 3650 -nodes -keyout ca-key.pem -subj /CN=jasync_sql_ca -out ca-req.pem && openssl rsa -in ca-key.pem -out ca-key.pem
openssl x509 -sha256 -days 3650 -extfile cav3.ext -set_serial 1 -req -in ca-req.pem -signkey ca-key.pem -out ca.pem
# Generate the server key/cert
openssl req -newkey rsa:2048 -days 3650 -nodes -keyout server-key.pem -subj /CN=localhost -out server-req.pem && openssl rsa -in server-key.pem -out server-key.pem
openssl x509 -sha256 -days 3650 -extfile certv3.ext -set_serial 2 -req -in server-req.pem -CA ca.pem -CAkey ca-key.pem -out server-cert.pem
# Generate the client key/cert
openssl req -newkey rsa:2048 -days 3650 -nodes -keyout client-key.pem -subj /CN=mysql_async -out client-req.pem && openssl rsa -in client-key.pem -out client-key.pem
openssl x509 -sha256 -days 3650 -extfile certv3.ext -set_serial 3 -req -in client-req.pem -CA ca.pem -CAkey ca-key.pem -out client-cert.pem
# Verify the CA actually created the server/client key/certs.
openssl verify -CAfile ca.pem server-cert.pem client-cert.pem
# So that java can read the client-key.
openssl pkcs8 -topk8 -nocrypt -in client-key.pem -out client-key2.pem
# Clean up the request files and unneeded client key.
mv client-key2.pem client-key.pem
rm *-req.pem
rm *.ext
# Generate the private/public RSA key pair.
openssl genrsa -out private_key.pem 2048
openssl rsa -in private_key.pem -pubout -out public_key.pem
```