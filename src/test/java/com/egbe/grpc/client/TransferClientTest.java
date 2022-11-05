package com.egbe.grpc.client;

import com.egbe.grpc.models.BankServiceGrpc;
import com.egbe.grpc.models.TransferRequest;
import com.egbe.grpc.models.TransferServiceGrpc;
import com.egbe.grpc.server.TransferStreamingResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferClientTest {


    private TransferServiceGrpc.TransferServiceStub transferServiceStub;

    @BeforeAll
    public void setup () {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        this.transferServiceStub = TransferServiceGrpc.newStub(channel);
    }


    @Test
    public void transfer() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TransferStreamingResponse response = new TransferStreamingResponse(latch);
        StreamObserver<TransferRequest> requestStreamObserver = this.transferServiceStub.transfer(response);


        for (int i = 0; i < 100; i++) {
            TransferRequest request = TransferRequest.newBuilder().setFromAccount(ThreadLocalRandom.current().nextInt(1, 11))
                    .setToAccount(ThreadLocalRandom.current().nextInt(1, 11))
                    .setAmount(ThreadLocalRandom.current().nextInt(1, 21))
                    .build();

            requestStreamObserver.onNext(request);
        }
        requestStreamObserver.onCompleted();
        latch.await();
    }
}
