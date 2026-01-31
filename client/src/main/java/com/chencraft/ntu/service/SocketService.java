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
import java.net.SocketTimeoutException;
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
    private final IdGenerator idGenerator;
    private final String host;
    private final int port;
    private final int timeout;
    private final int maxRetries;

    private DatagramSocket socket;

    @Autowired
    public SocketService(IdGenerator idGenerator, ApplicationArguments args,
                         @Value("${socket.server.host}") String defaultHost,
                         @Value("${socket.server.port}") int defaultPort,
                         @Value("${socket.timeout:1000000}") int timeout,
                         @Value("${socket.max-retries:0}") int maxRetries) {
        this.idGenerator = idGenerator;
        this.host = args.containsOption("host") && !Objects.requireNonNull(args.getOptionValues("host")).isEmpty()
                ? Objects.requireNonNull(args.getOptionValues("host")).getFirst()
                : defaultHost;

        this.port = args.containsOption("port") && !Objects.requireNonNull(args.getOptionValues("port")).isEmpty()
                ? Integer.parseInt(Objects.requireNonNull(args.getOptionValues("port")).getFirst())
                : defaultPort;

        this.timeout = timeout;
        this.maxRetries = maxRetries;

        log.info("SocketService initialized with host: {}, port: {}, timeout: {}, maxRetries: {}",
                 host, port, timeout, maxRetries);
        this.ensureSocketConnectionEstablished();
    }

    public Integer sendAndReceiveInt(MySerializable request) {
        byte[] responseData = sendAndReceiveWithRetry(request);
        return Converter.toInt(responseData);
    }

    public Double sendAndReceiveDouble(MySerializable request) {
        byte[] responseData = sendAndReceiveWithRetry(request);
        return Converter.toDouble(responseData);
    }

    public String sendAndReceiveString(MySerializable request) {
        byte[] responseData = sendAndReceiveWithRetry(request);
        return Converter.toString(responseData);
    }

    public String receiveCallback(int timeoutMillis) {
        this.ensureSocketConnectionEstablished();
        try {
            socket.setSoTimeout(timeoutMillis);
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            byte[] responseData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
            // Callback messages should have MessageType.MsgCallback (value 3)
            if (responseData.length > 0 && responseData[0] == 3) {
                return Converter.toString(responseData);
            }
            return null; // Not a callback or different message type
        } catch (java.net.SocketTimeoutException e) {
            return null; // Normal timeout
        } catch (IOException e) {
            log.error("Error receiving callback: {}", e.getMessage());
            return null;
        }
    }

    private byte[] sendAndReceiveWithRetry(MySerializable request) {
        this.ensureSocketConnectionEstablished();
        int requestId = idGenerator.getNextId();
        byte[] buffer = request.marshall(requestId);
        int attempts = 0;

        while (attempts <= maxRetries) {
            try {
                return sendAndReceive(buffer, requestId);
            } catch (OperationFailedException e) {
                attempts++;
                if (attempts > maxRetries) {
                    throw new OperationFailedException("Request failed after " + maxRetries + " retries: " + e.getMessage());
                }
                log.warn("Attempt {} failed, retrying... ({})", attempts, e.getMessage());
            }
        }
        throw new OperationFailedException("Request failed after max retries");
    }

    private synchronized byte[] sendAndReceive(byte[] buffer, int requestId) {
        try {
            InetAddress address = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.setSoTimeout(timeout);
            socket.send(packet);

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                byte[] responseData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());

                // Match response ID with request ID
                int responseId = Converter.byteArrayToInt(responseData, 1);
                if (responseId == requestId) {
                    return responseData;
                } else {
                    log.debug("Received response with ID {}, but expected {}. Ignoring.", responseId, requestId);
                }
            }
        } catch (SocketTimeoutException e) {
            throw new OperationFailedException("Timeout waiting for response");
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
