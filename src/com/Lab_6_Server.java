package com;

import com.support.UserDragServer;


public class Lab_6_Server
{
    /**
     * @param args Аргумент командной строки, принимающий имя файла, содержащего элементы коллекции.
     */
    public static void main (String[] args) //C:\Study\My_programe\Java_pr\IDEA\Lab_5\Crazy_dragon.json
    {
        UserDragServer userServer = new UserDragServer();
        userServer.process();
    }
}

