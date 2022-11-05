package com.egbe.grpc.server;

import com.egbe.grpc.models.TransferRequest;
import com.egbe.grpc.models.TransferResponse;
import com.egbe.grpc.models.TransferServiceGrpc;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {

    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {

        return new TransferStreamingRequest(responseObserver);
        //return super.transfer(responseObserver);
    }
}
