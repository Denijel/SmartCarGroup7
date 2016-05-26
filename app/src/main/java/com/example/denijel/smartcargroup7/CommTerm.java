package com.example.denijel.smartcargroup7;

import android.os.AsyncTask;

/**
 * Created by adinH on 2016-03-15.
 * A class just using the SSHmanager to startup the camera without having to go through any trouble with the raspberry PI.
 * The default ip adress for the sshManager is the ip adress of the cellphones shared internet.
 * The string command, is the line that is executed through the raspberry pi terminal.
 * Streameye is a software used to stream the camera locally which also executes this line.
 * -w is width -h is height -q is quality(0-100) and -r is framerate
 * https://github.com/ccrisan/streameye/
 */
public class CommTerm extends AsyncTask<String, Void, CommTerm> {

        private Exception exception;

        protected CommTerm doInBackground(String... urls) {

            String command = "python raspimjpeg.py -w 360 -h 180 -r 15 -q 10 | streameye";
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

