#!/bin/bash
mvn clean package

set -x -e

check() {
    sha256sum data.tx | sed s/tx/rx/ | sha256sum -c
}

dd if=/dev/urandom of=data.tx bs=10kB count=1

amodem send -q -i data.tx -l- -o audio.tx
java -cp target/jmodem-*.jar jmodem.Receiver <audio.tx >data.rx
check

java -cp target/jmodem-*.jar jmodem.Sender <data.tx >audio.tx
amodem recv -q -i audio.tx -l- -o data.rx
check

rm data.rx data.tx audio.tx
