# SC6103

Implementation of a distributed bank system

## Prerequisites

server:  go version go1.23.12 linux/amd64

client:  openjdk 25 2025-09-16 LTS

## Server Deployment

install go

```bash
cd /tmp
wget https://go.dev/dl/go1.23.12.linux-amd64.tar.gz

ls -lh go1.23.12.linux-amd64.tar.gz

sudo tar -C /usr/local -xzf go1.23.12.linux-amd64.tar.gz

echo 'export PATH=/usr/local/go/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# test
which go
go version
go env GOROOT

```

Windows:

```shell
go build -o server.exe server/main.go
.\server.exe -port=8080 -loss=0
```

linux:

```shell
go build -o server_bin ./server
./server_bin -port 8080 -loss 0
```

## Client Deployment

```shell
cd client/
mvn clean package
java -jar target/SC6103.jar
```
