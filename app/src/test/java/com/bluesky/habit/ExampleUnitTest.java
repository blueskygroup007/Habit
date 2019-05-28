package com.bluesky.habit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void javaTest() {
        int current = 10;
        int interval = 30;
        int percent = current*100 / interval;
        System.out.println("result=" + percent);
    }

    @Test
    public void copyTest() throws CloneNotSupportedException {
        Address addr1 = new Address();
        addr1.number = 110;
        addr1.street = "101 venture";
        Person person1 = new Person();
        person1.age = 11;
        person1.name = "zhang san";
        person1.addr = addr1;

        Person person2 = (Person) person1.clone();
        System.out.println("person1:" + person1.toString());
        System.out.println("person2:" + person2.toString() + "\n");

        person2.age = 22;
        person2.name = "li si";
        person2.addr.number = 202;
        person2.addr.street = "222 venture";

        System.out.println("person1:" + person1.toString());
        System.out.println("person2:" + person2.toString());

    }


}