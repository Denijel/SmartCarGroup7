package com.example.denijel.smartcargroup7;

import android.os.AsyncTask;

/**
 * Created by adinH on 2016-04-09.
 */
public class CommTerm extends AsyncTask<String, Void, CommTerm> {

        private Exception exception;

        protected CommTerm doInBackground(String... urls) {

            String command = "python raspimjpeg.py -w 640 -h 480 -r 15 -q 20 | streameye";
            String userName = "pi";
            String password = "raspberry";
            String connectionIP = "129.232.12.12";
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

