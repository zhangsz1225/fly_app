package com.taikesoft.fly.business.common.utils;

import com.squareup.otto.BasicBus;

/**
 * Created by qhzhao on 2015/9/23.
 */
public class MessageBus extends BasicBus {

    private static MessageBus bus;

    private MessageBus(){}

    public static MessageBus getBusInstance()
    {
        if (bus==null)
        {
            bus=new MessageBus();
        }
        return bus;
    }
}
