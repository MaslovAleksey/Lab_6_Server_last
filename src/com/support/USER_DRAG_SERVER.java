package com.support;


import com.beg_data.Dragon;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class USER_DRAG_SERVER {
    /**
     * Коллекция для хранения элементов
     */
    private Hashtable<Integer, Dragon> dragonHashtable = new Hashtable<Integer, Dragon>();
    /**
     * Переменная, хранящая имя файла, содержащего элементы коллекции
     */
    private String Name_f; // файл, содержащий сведения о коллекции
    /**
     * Переменная, хранящая список запросов от клиента
     */
    LinkedList<String> commandsHistory = new LinkedList<>();
    /**
     * Класс, обрабатывающий запрос клиента
     */
    PROVIDE_RESULT provideResult = new PROVIDE_RESULT();
    Gson gson = new Gson();
    /**
     * Значение порта по-умолчанию
     */
    private Integer port = 8734;
    /**
     * Переменная, содержащая в себе целочисленной значение из строки в случае успешного преобразования
     */
    private Integer k;
    //-----------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------
    //1.) Метод, реализующий работу приложения
    /**
     * Метод, реализующий работу приложения
     */
    public void process() {
        Scanner in = new Scanner(System.in);
        try {
            System.out.println("Завершение работы серверного приложения осуществляется вводом ключевого слова 'exit' или сочетанием клавиш <ctrl> + <d>");

            System.out.print("Укажите имя файла, содержащего элементы коллекции - ");
            Name_f = in.nextLine().trim();
            if (Name_f.equals("exit"))
                throw new NoSuchElementException();


            read_file(Name_f);
            receivePort(in);
            waiting(in);
        } catch (NoSuchElementException e)
        {
            System.out.println("Поступила команда завершения работы серверного приложения");
            save();
            System.exit(0);
        }

    }
    //---------------------------------------------------------------------------------------
    //2.) Метод, считывающий данные из файла и заполняющий хеш-таблицу
    /**
     * Метод для считывания из файла и записи в хеш-таблицу
     *
     * @param name_f Имя файла, содержащего элементы коллекции
     * @throws NoSuchElementException Отслеживание команды завершения пользовательского ввода
     */
    private void read_file(String name_f) throws NoSuchElementException {
            //Name_f = "Crazy_dragon_hashtable.json"
            Name_f = name_f;
            Gson gson = new Gson();
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(Name_f));
                dragonHashtable = gson.fromJson(new String(bytes), new TypeToken<Hashtable<Integer, Dragon>>() {}.getType());
            } catch (JsonSyntaxException e) {
                System.out.println("Некорректный формат данных в указанном файле, отредактируйте данные и перезапустите приложение");
                System.exit(0);
            } catch (IOException e1) {
                System.out.println("Имя файла указано неверно, перезапустите приложение");
                System.exit(0);
            }
            for (Dragon dragon:dragonHashtable.values())
            {
                dragon.generate_id();
                dragon.set_creationDate();
            }
    }
    //------------------------------------------------------------------------------------------------------------------
    //3.) Метод, осуществляющий взаимодействие с клиентом
    /**
     * Метод, осуществляющий взаимодействие с клиентом
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void waiting(Scanner in_wait) throws NoSuchElementException
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) // Установка сокета на стороне сервера
        {
            while (true) {
                System.out.println("\nОжидание запроса от клиента");
                Socket server = serverSocket.accept();// Ожидание подключения клиентов к серверу и воссоздания клиентского сокета на стороне сервера

                ObjectInputStream serverIn = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));
                ObjectOutputStream serverOut = new ObjectOutputStream(new BufferedOutputStream(server.getOutputStream()));

                OBJ_TO_SERV request = (OBJ_TO_SERV) serverIn.readObject();

                Serializable result = provideResult.provideResult(request, dragonHashtable, commandsHistory);

                serverOut.writeObject(result);
                serverOut.flush();

                if (request.get_command().equals("exit")) {
                    System.out.println("Поступила команда завершения работы клиентского приложения");
                    System.out.println("Для ожидания нового запроса - нажмите <Enter>\n" +
                            "Для завершения работы серверного приложения - введите 'exit' или сочетание клавиш <ctrl> + <d>");
                    if (in_wait.nextLine().trim().equals("exit"))
                        throw new NoSuchElementException();
                }


                System.out.println("Запрос клиента обработан\n" +
                        "Для ожидания нового запроса - нажмите <Enter>\n" +
                        "Для завершения работы серверного приложения - введите 'exit' или сочетание клавиш <ctrl> + <d>");
                if (in_wait.nextLine().trim().equals("exit"))
                    throw new NoSuchElementException();
            }
        }
        catch (IOException e1)
        {
            System.out.println("Не удалось установить соединение с клиентом");
            save();
            System.exit(0);
        }
        catch (ClassNotFoundException e2 )
        {
            System.out.println("Клиент отправил пустой запрос");
            save();
            System.exit(0);
        }
    }
    //----------------------------------------------------------------------------------------------------------------
    //4.) Метод, сохраняющий коллекцию в файл
    /**
     * Метод, сохраняющий коллекцию в файл
     */
    private void save()
    {
        String json = gson.toJson(dragonHashtable);
        try {
            Files.write(Paths.get(Name_f), json.getBytes());
        } catch (IOException e)
        {
            System.out.println("Impossible");
        }
    }
    //-------------------------------------------------------------------------------------------------
    //5.) Метод, запрашивающий номер порта
    /**
     * Метод, запрашивающий номер порта
     * @param in_port
     * @throws NoSuchElementException Отслеживание команды завершения пользовательского ввода
     */
    private void receivePort (Scanner in_port) throws NoSuchElementException
    {
        String  port_;
        System.out.println("\nВведите значение порта для установления связи с клиентом\n" +
                "(для использования данных по умолчанию оставьте поля пустыми");
        while (true) {
            System.out.print("\nПорт (целочисленное значение) - ");
            port_ = in_port.nextLine().trim();
            if (port_.length() == 0) {
                System.out.println("Порт - " + port);
                break;
            }
            else {
                if(isInteger(port_)) {
                    port = k;
                    break;
                }
                else System.out.println("Введите корректное значение порта (целочисленное значение)");
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    //6.) Метод, проверяющий строку на целочисленной значение
    /**
     * Метод, проверяющий строку на целочисленной значение
     * @param st Строка для проверки
     * @return Успешно ли выполнена провека (true/false)
     */
    private boolean isInteger(String st)
    {
        try {
            k = Integer.parseInt(st);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
