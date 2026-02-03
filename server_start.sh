#!/bin/bash

set -euo pipefail

go build -o server_bin ./server

# Should also provide -loss value, in percentage (0-100)
./server_bin -port 8866 "$@"