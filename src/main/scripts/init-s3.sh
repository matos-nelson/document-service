#!/bin/sh
aws s3 mb s3://document --endpoint-url http://localhost:4566

rm helloworld.txt
touch helloworld.txt
echo -e "Hello World!!! 1\n" >> helloworld.txt

aws s3 cp helloworld.txt s3://document --endpoint-url http://localhost:4566

rm helloworld.txt