#!/usr/bin/bash

rm -rf Deku-SMS-Android
git clone --recurse-submodules -j8 git@github.com:deku-messaging/Deku-SMS-Android.git
cd Deku-SMS-Android && \
	git checkout staging && \
	git submodule update --init --recursive && \
	cp ../../release.properties . && \
	cp ../../app/keys/app-release-key.jks app/keys/ && \
	make clean && \ 
	make release-cd jks_pass="$1" && cd .. \
