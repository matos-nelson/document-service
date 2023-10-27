#!/bin/sh
aws s3 mb s3://document --endpoint-url http://localhost:4566

rm -f helloworld.txt
rm -f file1.txt
rm -f file2.txt
touch helloworld.txt
echo -e "Hello World!!! 1\n" >> helloworld.txt

touch file1.txt
echo -e "File 1!!! 1\n" >> file1.txt

touch file2.txt
echo -e "File 2!!! 1\n" >> file2.txt

aws s3 cp helloworld.txt s3://document/123/helloworld.txt --endpoint-url http://localhost:4566
aws s3 cp file1.txt s3://document/123/folder1/file1.txt --endpoint-url http://localhost:4566
aws s3 cp file2.txt s3://document/123/folder2/file2.txt --endpoint-url http://localhost:4566

rm -f helloworld.txt
rm -f file1.txt
rm -f file2.txt