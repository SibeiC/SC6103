package com.chencraft.ntu.service;

import com.chencraft.ntu.exception.OperationFailedException;
import com.chencraft.ntu.model.MyDeserializable;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.util.Converter;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Provides functionality for messaging with a server over a socket connection.
 * This service allows sending requests and receiving responses in various formats
 * such as integers, doubles, or custom deserializable objects.
 * <p>
 * The service manages the lifecycle of the socket connection, ensuring that it
 * is properly opened, used, and closed. It supports a variety of request/response
 * patterns, including "send and forget" and "send/request and response."
 * <p>
 * This class is intended for use in Spring-based applications and is configured
 * with socket connection parameters via externalized properties.
 */
@Slf4j
@Service
public class SocketService {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    @Autowired
    public SocketService(@Value("${socket.server.host}") String host,
                         @Value("${socket.server.port}") int port) {
        try {
            this.socket = new Socket(host, port);
            log.info("Connected to server");

            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAndForget(MySerializable request) {
        sendAndReceive(request);
    }

    public Integer sendAndReceiveInt(MySerializable request) {
        byte[] responseData = sendAndReceive(request);
        return Converter.toInt(responseData);
    }

    public Double sendAndReceiveDouble(MySerializable request) {
        byte[] responseData = sendAndReceive(request);
        return Converter.toDouble(responseData);
    }

    public <T extends MyDeserializable> T sendAndReceiveCustomType(MySerializable request, Class<T> responseType) {
        byte[] responseData = sendAndReceive(request);
        return MyDeserializable.unmarshal(responseData, responseType);
    }

    private byte[] sendAndReceive(MySerializable request) {
        try {
            out.write(request.marshall());
            out.flush();

            return in.readAllBytes();
        } catch (IOException e) {
            throw new OperationFailedException("IOException: " + e.getMessage(), 400);
        }
    }

    @PreDestroy
    public void shutdown() throws IOException {
        in.close();
        out.close();
        socket.close();
        log.info("Disconnected from server");
    }
}
