package server.impl;

import calculator.Calculator;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import shared.Shared;

import java.util.Map;

import static shared.Shared.SharedStruct;

/**
 * User: NFadin
 * Date: 25.11.13
 * Time: 14:14
 */
public class CalculatorServiceImpl extends Calculator.CalculatorService {

    private Map<Integer, SharedStruct> log = Maps.newHashMap();

    @Override
    public void ping(RpcController controller, Calculator.VoidRequest request, RpcCallback<Calculator.VoidResponse> done) {
        System.out.println("ping");
        done.run(Calculator.VoidResponse.getDefaultInstance());
    }

    @Override
    public void add(RpcController controller, Calculator.AddRequest request, RpcCallback<Calculator.Number> done) {
        System.out.println(String.format("add(n1 = %s, n2 = %s", request.getNum1(), request.getNum2()));
        done.run(Calculator.Number.newBuilder().setNumber(request.getNum1() + request.getNum2()).build());
    }

    @Override
    public void calculate(RpcController controller, Calculator.CalculateRequest request, RpcCallback<Calculator.Number> done) {
        Calculator.Work work = request.getWork();
        System.out.println("calculate(" + request.getLogid() + ", {" + work.getOp() + "," + work.getNum1() + "," + work.getNum2() + "})");
        int val = 0;
        switch (work.getOp()) {
            case ADD:
                val = work.getNum1() + work.getNum2();
                break;
            case SUBTRACT:
                val = work.getNum1() - work.getNum2();
                break;
            case MULTIPLY:
                val = work.getNum1() * work.getNum2();
                break;
            case DIVIDE:
                if (work.getNum2() == 0) {
                    throw new RuntimeException("Cannot divide by 0");
                }
                val = work.getNum1() / work.getNum2();
                break;
            default:
                throw new RuntimeException("Unknown operation");
        }
        SharedStruct struct = SharedStruct.newBuilder().setKey(request.getLogid()).setValue(Integer.toString(val)).build();
        log.put(request.getLogid(), struct);

        done.run(Calculator.Number.newBuilder().setNumber(val).build());
    }

    @Override
    public void zip(RpcController controller, Calculator.VoidRequest request, RpcCallback<Calculator.VoidResponse> done) {
        System.out.println("zip");
    }

    @Override
    public void getStruct(RpcController controller, Calculator.Number request, RpcCallback<SharedStruct> done) {
        System.out.println("get struct");
        done.run(log.get(request.getNumber()));
    }

    @Override
    public void getAllStructsList(RpcController controller, Calculator.VoidRequest request, RpcCallback<Calculator.SharedStructList> done) {
        System.out.println("get all structs list");
        done.run(Calculator.SharedStructList.newBuilder().addAllStruct(log.values()).build());
    }

    @Override
    public void getAllStructsMap(RpcController controller, Calculator.VoidRequest request, RpcCallback<Calculator.SharedStructMap> done) {
        System.out.println("get all structs map");
        done.run(Calculator.SharedStructMap.newBuilder()
                .addAllEntry(Lists.newArrayList(Iterables.transform(log.entrySet(), new Function<Map.Entry<Integer, SharedStruct>, Shared.SharedStructEntry>() {
                    @Override
                    public Shared.SharedStructEntry apply(Map.Entry<Integer, SharedStruct> input) {
                        return Shared.SharedStructEntry.newBuilder().setKey(input.getKey()).setValue(input.getValue()).build();
                    }
                }))).build());
    }
}
