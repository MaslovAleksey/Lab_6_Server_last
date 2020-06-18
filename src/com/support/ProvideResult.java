package com.support;

import com.beg_data.Dragon;
import com.response.AvgAgeResponse;
import com.response.CountResponse;
import com.response.InfoResponse;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProvideResult {
    /**
     * Поле для хранения элемента коллекции, переданного от клиента
     */
    private Dragon dr;

    public Serializable provideResult(ObjToServer objServer, Hashtable<Integer, Dragon> dragons, List<String> commandsHistory) {
        if (!objServer.getCommand().equals("history"))
            commandsHistory.add(objServer.getCommand());
        String command = objServer.getCommand();
        switch (command) {
            case "info":
                return info(dragons);

            case "show":
                return sortByName(dragons);

            case "help":
                List<String> helpComand = new LinkedList<String>(){};
                helpComand.add("help - вывод справки по доступным командам");
                helpComand.add("info - вывод в стандартный поток вывода информации о коллекции (тип, дата инициализации, количество элементов и т.д.)");
                helpComand.add("show - вывод в стандартный поток вывода всех элементы коллекции в строковом представлении");
                helpComand.add("insert_key {element} - добавление нового элемента с заданным ключом");
                helpComand.add("update_id {element} - обновление значения элемента коллекции, id которого равен заданному");
                helpComand.add("remove_key {key} - удаление элемента из коллекции по его ключу");
                helpComand.add("clear - очищение коллекции");
                helpComand.add("execute_script {file_name} - считывание и исполнение скрипта из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
                helpComand.add("save - сохрание коллекции в файл\n");
                helpComand.add("exit - завершение программы");
                helpComand.add("history - вывод последних 6 команд (без их аргументов)");
                helpComand.add("replace_if_lowe_key {element} - замена значения по ключу, если новое значение меньше старого");
                helpComand.add("remove_greater_key {key} - удаление из коллекции всех элементов, ключ которых превышает заданный");
                helpComand.add("average_of_age - вывод среднего значения поля age для всех элементов коллекции");
                helpComand.add("count_less_then_cave {cave} - вывод количества элементов, значение поля cave которых меньше заданного");
                helpComand.add("print_ascending - вывод элементов коллекции в порядке возрастания");
                helpComand.add("<ctrl> + <d> - завершение пользовательского ввода(вызов возможен в произвольном месте при вводе данных )");
                return helpComand.stream().collect(Collectors.toCollection(LinkedList::new));
            case "clear":
                dragons.clear();
                return sortByName(dragons);

            case "history":
                long elementsToSkip = 0;
                if (commandsHistory.size() > 6) {
                    elementsToSkip = commandsHistory.size() - 6;
                }
                LinkedList<String> resultList = commandsHistory.stream()
                        .skip(elementsToSkip) // Пропускает первые элементы
                        .collect(Collectors.toCollection(LinkedList::new));
                Collections.reverse(resultList); // чтобы последняя команда была сверху
                return resultList;

            case "average_of_age":
                double avg = dragons.values()
                        .stream()
                        .mapToLong(dragon -> dragon.getAge())
                        .average()
                        .orElse(0.0);
                return new AvgAgeResponse(avg);

            case "print_ascending":
                return dragons.values()
                        .stream()
                        .sorted()
                        .collect(Collectors.toCollection(LinkedList::new));

            case "remove_key":
                dragons.remove(objServer.getValue());
                return sortByName(dragons);

            case "remove_greater_key":
                int key_new = objServer.getValue();
                Set <Integer> keySet;
                keySet = dragons.keySet()
                        .stream()
                        .filter(key -> key > key_new)
                        .collect(Collectors.toSet());
                for (Integer key:keySet)
                    dragons.remove(key);
                return sortByName(dragons);

            case "update_id":
                dr = objServer.getDragon();
                dr.generateId();
                dr.setCreationDate();
                int id_new = objServer.getValue();
                dragons.entrySet()
                        .stream()
                        .filter(map -> map.getValue().getId()==id_new)
                        .forEach(map -> dragons.put(map.getKey(),dr));
                return sortByName(dragons);

            case "insert_key":
                dr = objServer.getDragon();
                dr.generateId();
                dr.setCreationDate();
                dragons.put(objServer.getValue(), dr);
                return sortByName(dragons);

            case "count_less_then_cave":
                long count = dragons.values()
                        .stream()
                        .map(d -> d.getDepth() + d.getNumberOfTreasures())
                        .filter( value -> value < objServer.getCave())
                        .count();
                return new CountResponse(count);

            case "replace_if_lowe_key":
                Dragon dragon = dragons.get(objServer.getValue());
                if (dragon != null) {
                    Dragon dragonClient = objServer.getDragon();
                    dragonClient.setCreationDate();
                    dragonClient.generateId();
                    if (dragon.compareTo(dragonClient) > 0) {
                        dragons.replace(objServer.getValue(), dragonClient);
                    }
                }
                return sortByName(dragons);

            default:
                return sortByName(dragons);
        }
    }

    private LinkedList<Dragon> sortByName(Hashtable<Integer, Dragon> dragons) {
//      Function<Dragon, String> dragonStringFunction = dragon -> dragon.get_name();
        return dragons.values()
                .stream()
                .sorted(Comparator.comparing(dragon -> dragon.getName()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private InfoResponse info(Hashtable<Integer, Dragon> dragons) {
        InfoResponse response = new InfoResponse();
        response.setSize(dragons.size());
        response.setCollectionType(dragons.getClass().toString());
        response.setElementType(Dragon.class.toString());

        Optional<LocalDateTime> min = dragons.values()
                .stream()
                .map(d -> d.getCreationDate())
                .min((localDateTime, other) -> localDateTime.compareTo(other));

        min.ifPresent(minDate -> response.setCreationDate(minDate));

        return response;
    }


}
