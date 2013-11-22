include "../shared/shared.thrift"

namespace java server // defines the namespace

typedef shared.int int  //typedefs to get convenient names for your types

enum Operation {
    ADD = 1,
    SUBTRACT = 2,
    MULTIPLY = 3,
    DIVIDE = 4
}

struct Work {
    1: int num1 = 0,
    2: int num2,
    3: Operation op,
    4: optional string comment
}

exception InvalidOperation {
    1: int what,
    2: string why
}

service CalculatorService extends shared.SharedService {  // defines the service to add two numbers
    void ping(),
    i32 add(1:int n1, 2:int n2), //defines a method
    i32 calculate(1:int logid, 2:Work w) throws (1:InvalidOperation ouch),
    oneway void zip() // oneway - client won't listen to response
}