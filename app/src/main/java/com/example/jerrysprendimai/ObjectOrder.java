package com.example.jerrysprendimai;

public class ObjectOrder {
    private Integer id;

    ObjectObject myObject;
    ObjectDealer myDealer;

    public ObjectOrder() {
        this.id = -1;
    }

    public ObjectOrder(Integer id, ObjectObject myObject, ObjectDealer myDealer) {
        this.id = id;
        this.myObject = myObject;
        this.myDealer = myDealer;
    }



    public Integer getId() {        return id;    }
    public void setId(Integer id) {        this.id = id;    }
    public ObjectObject getMyObject() {        return myObject;    }
    public void setMyObject(ObjectObject myObject) {        this.myObject = myObject;    }
    public ObjectDealer getMyDealer() {        return myDealer;    }
    public void setMyDealer(ObjectDealer myDealer) {        this.myDealer = myDealer;    }
}
