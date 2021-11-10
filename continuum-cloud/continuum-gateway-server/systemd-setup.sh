#!/bin/bash


sudo cp ./gateway.service  /etc/systemd/system/
sudo cp ./gateway-rsyslog.conf /etc/rsyslog.d/

sudo systemctl enable gateway.service
sudo systemctl restart rsyslog
sudo systemctl start gateway.service