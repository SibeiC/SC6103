package com.chencraft.ntu.service;

import com.chencraft.ntu.exception.OperationFailedException;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.util.Converter;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

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
    private final String host;
    private final int port;

    private DatagramSocket socket;

    @Autowired
    public SocketService(ApplicationArguments args,
                         @Value("${socket.server.host}") String defaultHost,
                         @Value("${socket.server.port}") int defaultPort) {
        this.host = args.containsOption("host") && !Objects.requireNonNull(args.getOptionValues("host")).isEmpty()
                ? Objects.requireNonNull(args.getOptionValues("host")).getFirst()
                : defaultHost;

        this.port = args.containsOption("port") && !Objects.requireNonNull(args.getOptionValues("port")).isEmpty()
                ? Integer.parseInt(Objects.requireNonNull(args.getOptionValues("port")).getFirst())
                : defaultPort;

        log.info("SocketService initialized with host: {}, port: {}", host, port);
        this.ensureSocketConnectionEstablished();
    }

    public void sendAndForget(MySerializable request) {
        // TODO: Should not wait for the response
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

    private byte[] sendAndReceive(MySerializable request) {
        this.ensureSocketConnectionEstablished();

        // TODO: Add synchronization lock
        // TODO: Handle timeout and retransmit
        try {
            byte[] buffer = request.marshall();
            InetAddress address = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            return Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (IOException e) {
            throw new OperationFailedException("IOException: " + e.getMessage());
        }
    }

    private void ensureSocketConnectionEstablished() {
        if (this.socket == null || this.socket.isClosed()) {
            try {
                this.socket = new DatagramSocket();
                log.info("UDP Socket initialized");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        log.info("Disconnected from server");
    }
}
