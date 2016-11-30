#!/bin/bash

docker run -d -p 1521:1521 sath89/oracle-12c >> oracleDatabaseInit.log 2>&1
