namespace java shared

typedef i32 int

struct SharedStruct {
    1: int key,
    2: string value
}

service SharedService {  // defines the service to add two numbers
    SharedStruct getStruct(1:int key)
    list<SharedStruct> getAllStructsList() // container example (list)
    map<int, SharedStruct> getAllStructsMap() // container example (map)
}