package org.studyplatform.model;

public class UserRunPK {
    MogeUserItem win;
    MogeUserItem lose;

    public MogeUserItem getLose() {
        return lose;
    }

    public MogeUserItem getWin() {
        return win;
    }

    public void setLose(MogeUserItem lose) {
        this.lose = lose;
    }

    public void setWin(MogeUserItem win) {
        this.win = win;
    }
}
