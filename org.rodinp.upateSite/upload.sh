#!/bin/bash
read -p "Sourceforge username: " USER
scp site.xml $USER,rodin-b-sharp@web.sourceforge.net:htdocs/updates
