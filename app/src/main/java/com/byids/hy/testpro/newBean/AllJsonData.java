package com.byids.hy.testpro.newBean;

import java.io.Serializable;

/**
 * Created by gqgz2 on 2016/10/28.
 */

public class AllJsonData implements Serializable{
    private CommandData CommandData;
    private CommandUser CommandUser;

    public CommandData getCommandData() {
        return CommandData;
    }

    public void setCommandData(CommandData commandData) {
        this.CommandData = commandData;
    }

    public CommandUser getCommandUser() {
        return CommandUser;
    }

    public void setCommandUser(CommandUser commandUser) {
        this.CommandUser = commandUser;
    }
}
