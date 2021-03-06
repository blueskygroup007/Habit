package com.bluesky.habit;

import com.bluesky.habit.util.TimeUtils;

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
        int percent = current * 100 / interval;
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
        System.out.println("person1:" + person1.toString() + "----" + person1.addr.hashCode() + "----" + person1.addr.toString());
        System.out.println("person2:" + person2.toString() + "----" + person2.addr.hashCode() + "----" + person2.addr.toString() + "\n");
        System.out.println("person1.addr.street:" + person1.addr.street);
        System.out.println("person2.addr.street:" + person2.addr.street + "\n");
        System.out.println("person1.addr.street:" + person1.addr.street.hashCode());
        System.out.println("person2.addr.street:" + person2.addr.street.hashCode() + "\n");

        person2.age = 22;
        person2.name = "li si";
        person2.addr.number = 202;
        person2.addr.street = "222 venture";

        System.out.println("person1:" + person1.toString() + "----" + person1.addr.hashCode() + "----" + person1.addr.toString());
        System.out.println("person2:" + person2.toString() + "----" + person2.addr.hashCode() + "----" + person2.addr.toString() + "\n");
        System.out.println("person1.addr.street:" + person1.addr.street);
        System.out.println("person2.addr.street:" + person2.addr.street + "\n");
        System.out.println("person1.addr.street:" + person1.addr.street.hashCode());
        System.out.println("person2.addr.street:" + person2.addr.street.hashCode());

    }

    @Test
    public void timeTest() {
//        long ms = 136 ;//当前运行了136秒
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//        String str = formatter.format(ms);
//        System.out.println(str);
        long time = 300;
        String str = TimeUtils.secToTime(Integer.parseInt(String.valueOf(time)));
        int ii = new Long(time).intValue();
        System.out.println(str + "--" + ii);


    }

    @Test
    public void enumTest() {
//        int index = Types.WakeStyle.RING.ordinal();
//        Types.WakeStyle.RING.getVal();
//        System.out.println("index=    " + index);
//        System.out.println("Types.WakeStyle.RING=    " + Types.WakeStyle.RING);
//        System.out.println("Types.WakeStyle.RING.values    " + Types.WakeStyle.RING.getVal());
//        System.out.println("Types.WakeStyle.RING.values    " + Types.WakeStyle.LIGHT.getVal());
//        System.out.println("Types.WakeStyle.RING.values    " + Types.WakeStyle.VIBRATE.getVal());
        boolean a = true;
        boolean b = true;
        assertEquals(a, b);


    }


}