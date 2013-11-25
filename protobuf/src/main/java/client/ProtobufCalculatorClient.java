package client;

import calculator.Calculator;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

import static calculator.Calculator.*;
import static calculator.Calculator.CalculatorService.BlockingInterface;
import static calculator.Calculator.CalculatorService.newBlockingStub;
import static shared.Shared.SharedStruct;

/**
 * User: NFadin
 * Date: 25.11.13
 * Time: 13:32
 */
public class ProtobufCalculatorClient {

    public static void main(String[] args) throws IOException, ServiceException {
        PeerInfo clientInfo = new PeerInfo("localhost", 1234);
        PeerInfo serverInfo = new PeerInfo("localhost", 9091);

        RpcClientChannel channel = getChannel(clientInfo, serverInfo);
        BlockingInterface calculatorService = newBlockingStub(channel);
        RpcController controller = channel.newRpcController();

        // ping
        VoidRequest ping = VoidRequest.newBuilder().build();
        calculatorService.ping(controller, ping);

        // add
        AddRequest addRequest = AddRequest.newBuilder().setNum1(100).setNum2(200).build();
        Calculator.Number number = calculatorService.add(controller, addRequest);
        System.out.println("100 + 200 = " + number.getNumber());

        // divide
        try {
            CalculateRequest divideRequest = CalculateRequest.newBuilder()
                    .setWork(Work.newBuilder().setNum1(1).setNum2(0).setOp(Work.Operation.DIVIDE).build())
                    .setLogid(1).build();
            Calculator.Number quotient = calculatorService.calculate(controller, divideRequest);
            System.out.println("Whoa we can divide by 0");
        } catch (Exception e) {
            controller.reset();
            System.out.println("Invalid operation");
        }

        // subtract
        CalculateRequest subtractRequest = CalculateRequest.newBuilder()
                .setWork(Work.newBuilder().setNum1(15).setNum2(10).setOp(Work.Operation.SUBTRACT).build())
                .setLogid(1).build();
        Calculator.Number diff = calculatorService.calculate(controller, subtractRequest);
        System.out.println("15 - 10 = " + diff.getNumber());

        // get struct
        SharedStruct log = calculatorService.getStruct(controller, Calculator.Number.newBuilder().setNumber(1).build());
        System.out.println("Check log: " + log.getValue());

        // get struct list
        System.out.println("Print list...");
        System.out.println(calculatorService.getAllStructsList(controller, VoidRequest.newBuilder().build()));

        // get struct map
        System.out.println("Print map...");
        System.out.println(calculatorService.getAllStructsMap(controller, VoidRequest.newBuilder().build()));
    }

    private static RpcClientChannel getChannel(PeerInfo client, PeerInfo server) throws IOException {
        DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory(client);
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .handler(clientFactory)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_SNDBUF, 1048576)
                .option(ChannelOption.SO_RCVBUF, 1048576);
        return clientFactory.peerWith(server, bootstrap);
    }
}
