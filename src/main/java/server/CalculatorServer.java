package server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import server.handler.CalculatorServiceHandler;

import static org.apache.thrift.server.TServer.Args;

/**
 * User: NFadin
 * Date: 21.11.13
 * Time: 13:26
 */
public class CalculatorServer {

    public static void main(String[] args) {
        startSimpleServer(new CalculatorService.Processor<>(new CalculatorServiceHandler()));
    }

    private static void startSimpleServer(CalculatorService.Processor<CalculatorServiceHandler> processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            System.out.println("Starting the simple server...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
