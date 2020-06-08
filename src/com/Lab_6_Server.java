package com;

import com.support.USER_DRAG_SERVER;


public class Lab_6_Server
{
    /**
     * @param args Аргумент командной строки, принимающий имя файла, содержащего элементы коллекции.
     */
    public static void main (String[] args) //C:\Study\My_programe\Java_pr\IDEA\Lab_5\Crazy_dragon.json
    {
        USER_DRAG_SERVER userServer = new USER_DRAG_SERVER();
        userServer.process();
    }
}

