package server.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.thrift.TException;
import server.CalculatorService;
import server.InvalidOperation;
import server.Work;
import shared.SharedStruct;

import java.util.List;
import java.util.Map;

/**
 * User: NFadin
 * Date: 21.11.13
 * Time: 13:25
 */
public class CalculatorServiceHandler implements CalculatorService.Iface {

    private Map<Integer,SharedStruct> log = Maps.newHashMap();

    @Override
    public void ping() throws TException {
        System.out.println("ping");
    }

    @Override
    public int add(int n1, int n2) {
        System.out.println(String.format("add(n1 = %s, n2 = %s", n1, n2));
        return n1 + n2;
    }

    @Override
    public int calculate(int logid, Work work) throws TException {
        System.out.println("calculate(" + logid + ", {" + work.op + "," + work.num1 + "," + work.num2 + "})");
        int val = 0;
        switch (work.op) {
            case ADD:
                val = work.num1 + work.num2;
                break;
            case SUBTRACT:
                val = work.num1 - work.num2;
                break;
            case MULTIPLY:
                val = work.num1 * work.num2;
                break;
            case DIVIDE:
                if (work.num2 == 0) {
                    InvalidOperation io = new InvalidOperation();
                    io.what = work.op.getValue();
                    io.why = "Cannot divide by 0";
                    throw io;
                }
                val = work.num1 / work.num2;
                break;
            default:
                InvalidOperation io = new InvalidOperation();
                io.what = work.op.getValue();
                io.why = "Unknown operation";
                throw io;
        }

        SharedStruct struct = new SharedStruct();
        struct.key = logid;
        struct.value = Integer.toString(val);
        log.put(logid, struct);

        return val;
    }

    @Override
    public void zip() throws TException {
        System.out.println("zip");
    }

    @Override
    public SharedStruct getStruct(int key) throws TException {
        System.out.println("get struct");
        return log.get(key);
    }

    @Override
    public List<SharedStruct> getAllStructsList() throws TException {
        System.out.println("get all structs list");
        return Lists.newArrayList(log.values());
    }

    @Override
    public Map<Integer, SharedStruct> getAllStructsMap() throws TException {
        System.out.println("get all structs map");
        return log;
    }
}
