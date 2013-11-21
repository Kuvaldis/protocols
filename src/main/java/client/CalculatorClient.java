package client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import server.CalculatorService;
import server.InvalidOperation;
import server.Operation;
import server.Work;
import shared.SharedStruct;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: NFadin
 * Date: 21.11.13
 * Time: 13:31
 */
public class CalculatorClient {

    private static ExecutorService service = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            service.submit(new ClientTask());
        }
    }

    private static class ClientTask implements Runnable {

        @Override
        public void run() {
            try {
                TTransport transport = new TSocket("localhost", 9090);
                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                CalculatorService.Client client = new CalculatorService.Client(protocol);

                client.ping();

                int add = client.add(100, 200);
                System.out.println("100 + 200 = " + add);

                Work work = new Work();
                work.op = Operation.DIVIDE;
                work.num1 = 1;
                work.num2 = 0;
                try {
                    int quotient = client.calculate(1, work);
                    System.out.println("Whoa we can divide by 0");
                } catch (InvalidOperation io) {
                    System.out.println("Invalid operation: " + io.why);
                }

                work.op = Operation.SUBTRACT;
                work.num1 = 15;
                work.num2 = 10;
                try {
                    int diff = client.calculate(1, work);
                    System.out.println("15-10=" + diff);
                } catch (InvalidOperation io) {
                    System.out.println("Invalid operation: " + io.why);
                }

                SharedStruct log = client.getStruct(1);
                System.out.println("Check log: " + log.value);

                System.out.println("Print list...");
                for (SharedStruct sharedStruct : client.getAllStructsList()) {
                    System.out.println(sharedStruct.key + ": " + sharedStruct.value);
                }

                System.out.println("Print map...");
                for (Map.Entry<Integer, SharedStruct> entry : client.getAllStructsMap().entrySet()) {
                    System.out.println();
                }

                transport.close();
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }
}
