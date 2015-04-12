#!/bin/bash
set -x

check() {
    sha256sum data.tx | sed s/tx/rx/ | sha256sum -c
}

dd if=/dev/urandom of=data.tx bs=10kB count=1
java -cp bin jmodem.Sender <data.tx >audio.tx
amodem recv -q -i audio.tx -l- -o data.rx
check

amodem send -q -i data.tx -l- -o audio.tx
java -cp bin jmodem.Receiver <audio.tx >data.rx
check
