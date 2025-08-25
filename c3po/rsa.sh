#!/bin/bash

RSA_BITS=2048

echo "=== Private Key ==="
openssl genrsa $RSA_BITS 2>/dev/null | tee /tmp/uni-private.pem | grep -v "BEGIN\|END" | tr -d '\n'
echo ""

echo -e "\n=== Public Key ==="
openssl rsa -in /tmp/uni-private.pem -pubout 2>/dev/null | grep -v "BEGIN\|END" | tr -d '\n'
echo ""

rm -f /tmp/uni-private.pem
