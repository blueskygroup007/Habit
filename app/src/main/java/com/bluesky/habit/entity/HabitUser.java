package com.bluesky.habit.entity;


import cn.bmob.v3.BmobUser;

/**
 * @author BlueSky
 * @date 2019/9/30
 * Description:
 */
public class HabitUser extends BmobUser {
    String nickname;
    Integer age;
    String desc;
    Boolean gender;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "HabitUser{" +
                "nickname='" + nickname + '\'' +
                ", age=" + age +
                ", desc='" + desc + '\'' +
                ", gender=" + gender +
                '}';
    }
}
