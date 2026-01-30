# SC6103

Implementation of a distributed bank system

### Prerequisites

server:  go 1.25.6

### Server Deployment

Windows:

```shell
go build -o server.exe server/main.go
.\server.exe -port=8080 -loss=0
```

linux:

```
go build -o server_bin ./server
./server_bin -port 8080 -loss 0
```
