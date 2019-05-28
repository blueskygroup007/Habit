package com.bluesky.rabbit_habit;

/**
 * @author BlueSky
 * @date 2019/4/25
 * Description:
 */
public class Person implements Cloneable{
    public int age;
    public String name;
    public Address addr;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Person person = (Person) super.clone();
        person.addr = (Address) person.addr.clone();
        return person;
    }


    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", addr=" + addr +
                '}' + "addr.number=" + addr.number + " addr.street=" + addr.street;
    }
}
