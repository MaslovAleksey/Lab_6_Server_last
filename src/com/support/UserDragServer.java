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

public class UserDragServer {
    /**
     * Коллекция для хранения элементов
     */
    private Hashtable<Integer, Dragon> dragonHashtable = new Hashtable<Integer, Dragon>();
    /**
     * Переменная, хранящая имя файла, содержащего элементы коллекции
     */
    private String nameFile; // файл, содержащий сведения о коллекции
    /**
     * Переменная, хранящая список запросов от клиента
     */
    LinkedList<String> commandsHistory = new LinkedList<>();
    /**
     * Класс, обрабатывающий запрос клиента
     */
    ProvideResult provideResult = new ProvideResult();
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
            nameFile = in.nextLine().trim();
            if (nameFile.equals("exit"))
                throw new NoSuchElementException();


            readFile(nameFile);
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
     * @param nameFile Имя файла, содержащего элементы коллекции
     * @throws NoSuchElementException Отслеживание команды завершения пользовательского ввода
     */
    private void readFile(String nameFile) throws NoSuchElementException {
            //nameFile = "Crazy_dragon_hashtable.json"
            nameFile = nameFile;
            Gson gson = new Gson();
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(nameFile));
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
                dragon.generateId();
                dragon.setCreationDate();
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
                System.out.println("\nОжидание запроса от клиента (время ожидания - 5 минут)");
                serverSocket.setSoTimeout(300000); // Установка времени ожидания запроса от клиента - 3 минуты
                Socket server = serverSocket.accept();// Ожидание подключения клиентов к серверу и воссоздания клиентского сокета на стороне сервера

                ObjectInputStream serverIn = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));
                ObjectOutputStream serverOut = new ObjectOutputStream(new BufferedOutputStream(server.getOutputStream()));

                ObjToServer request = (ObjToServer) serverIn.readObject();

                Serializable result = provideResult.provideResult(request, dragonHashtable, commandsHistory);

                serverOut.writeObject(result);
                serverOut.flush();

                if (request.getCommand().trim().equals("save"))
                    save();

                if (request.getCommand().trim().equals("exit"))
                    throw new NoSuchElementException();
                System.out.println("\nЗапрос клиента обработан");
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
            Files.write(Paths.get(nameFile), json.getBytes());
        } catch (IOException | NullPointerException e2)
        {
            System.out.println("Невозможно сохранить коллекцию в указанный файл");
        }
    }
    //-------------------------------------------------------------------------------------------------
    //5.) Метод, запрашивающий номер порта
    /**
     * Метод, запрашивающий номер порта
     * @param inPort
     * @throws NoSuchElementException Отслеживание команды завершения пользовательского ввода
     */
    private void receivePort (Scanner inPort) throws NoSuchElementException
    {
        String  portMy;
        System.out.println("\nВведите значение порта для установления связи с клиентом (целочисленное значение [1024-65535])\n" +
                "(для использования данных по умолчанию оставьте поля пустыми");
        while (true) {
            System.out.print("\nПорт - ");
            portMy = inPort.nextLine().trim();
            if (portMy.length() == 0) {
                System.out.println("Порт - " + port);
                break;
            }
            else {
                if((isInteger(portMy) && (k<=65535) && (k>=1024))) {
                    port = k;
                    break;
                }
                else System.out.println("Введите корректное значение порта (целочисленное значение [1024-65535])");
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
