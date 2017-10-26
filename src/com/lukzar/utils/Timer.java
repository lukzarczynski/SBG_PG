/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.utils;

public class Timer {

    private long start;

    public static Timer start() {
        Timer timer = new Timer();
        timer.start = System.currentTimeMillis();
        return timer;
    }

    public void end(String message) {
//        System.out.println(
//                String.format("%s ms, %s", String.valueOf(System.currentTimeMillis() - start), message));
    }
}
