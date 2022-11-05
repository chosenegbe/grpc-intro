package com.egbe.grpc.server;

import com.egbe.grpc.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        int accountNumber = request.getAccountNumber();

/*        Balance balance = Balance.newBuilder()
                .setAmount(accountNumber * 10)
                .build();*/

        Balance balance = Balance.newBuilder()
                .setAmount(AccountDatabase.getBalance(accountNumber))
                .build();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithDrawRequest request, StreamObserver<Money> responseObserver) {
        int account_number = request.getAccountNumber();
        int amount = request.getAmount();
        int balance = AccountDatabase.getBalance(account_number);

        if (balance < amount) {
            Status status = Status.FAILED_PRECONDITION.withDescription("No enough money. You have only " + balance);
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        for (int i = 0; i < (amount/10); i++) { //e.g., if the user request 40 dollars, we run the lopp 4 times delivering 10 dolar each loop
            Money money = Money.newBuilder()
                    .setValue(10)
                    .build();

            responseObserver.onNext(money);
            AccountDatabase.deductBalance(account_number, 10);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver);
        //return super.cashDeposit(responseObserver);
    }
}
