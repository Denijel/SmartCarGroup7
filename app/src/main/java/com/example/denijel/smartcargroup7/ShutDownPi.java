package com.example.denijel.smartcargroup7;

import android.os.AsyncTask;

/**
 * Created by adinH on 2016-04-09.
 */
public class ShutDownPi extends AsyncTask<String, Void, ShutDownPi> {

    private Exception exception;

    protected ShutDownPi doInBackground(String... urls) {

        String command = "sudo shutdown -h now";
        String userName = "pi";
        String password = "raspberry";
        String connectionIP = "192.168.43.71";
        SSHmanager instance = new SSHmanager(userName, password, connectionIP, "");
        instance.connect();

        instance.sendCommand(command);
        // close only after all commands are sent
        instance.close();
        return null;
    }

    protected void onPostExecute(CommTerm feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}

