package com.support;

import com.beg_data.Dragon;
import com.response.AVG_AGE_RESPONSE;
import com.response.COUNT_RESPONSE;
import com.response.INFO_RESPONSE;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PROVIDE_RESULT {
    /**
     * Поле для хранения элемента коллекции, переданного от клиента
     */
    private Dragon dr;

    public Serializable provideResult(OBJ_TO_SERV objServer, Hashtable<Integer, Dragon> dragons, List<String> commandsHistory) {
        if (!objServer.get_command().equals("history"))
            commandsHistory.add(objServer.get_command());
        String command = objServer.get_command();
        switch (command) {
            case "info":
                return info(dragons);

            case "show":
                return sortByName(dragons);

            case "help":
                List<String> help_comand = new LinkedList<String>(){};
                help_comand.add("help - вывод справки по доступным командам");
                help_comand.add("info - вывод в стандартный поток вывода информации о коллекции (тип, дата инициализации, количество элементов и т.д.)");
                help_comand.add("show - вывод в стандартный поток вывода всех элементы коллекции в строковом представлении");
                help_comand.add("insert_key {element} - добавление нового элемента с заданным ключом");
                help_comand.add("update_id {element} - обновление значения элемента коллекции, id которого равен заданному");
                help_comand.add("remove_key {key} - удаление элемента из коллекции по его ключу");
                help_comand.add("clear - очищение коллекции");
                help_comand.add("execute_script {file_name} - считывание и исполнение скрипта из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
                help_comand.add("exit - завершение программы");
                help_comand.add("history - вывод последних 6 команд (без их аргументов)");
                help_comand.add("replace_if_lowe_key {element} - замена значения по ключу, если новое значение меньше старого");
                help_comand.add("remove_greater_key {key} - удаление из коллекции всех элементов, ключ которых превышает заданный");
                help_comand.add("average_of_age - вывод среднего значения поля age для всех элементов коллекции");
                help_comand.add("count_less_then_cave {cave} - вывод количества элементов, значение поля cave которых меньше заданного");
                help_comand.add("print_ascending - вывод элементов коллекции в порядке возрастания");
                help_comand.add("<ctrl> + <d> - завершение пользовательского ввода(вызов возможен в произвольном месте при вводе данных )");
                return help_comand.stream().collect(Collectors.toCollection(LinkedList::new));
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
                        .mapToLong(dragon -> dragon.get_age())
                        .average()
                        .orElse(0.0);
                return new AVG_AGE_RESPONSE(avg);

            case "print_ascending":
                return dragons.values()
                        .stream()
                        .sorted()
                        .collect(Collectors.toCollection(LinkedList::new));

            case "remove_key":
                dragons.remove(objServer.get_value());
                return sortByName(dragons);

            case "remove_greater_key":
                int key_new = objServer.get_value();
                Set <Integer> keySet;
                keySet = dragons.keySet()
                        .stream()
                        .filter(key -> key > key_new)
                        .collect(Collectors.toSet());
                for (Integer key:keySet)
                    dragons.remove(key);
                return sortByName(dragons);

            case "update_id":
                dr = objServer.get_Dragon();
                dr.generate_id();
                dr.set_creationDate();
                int id_new = objServer.get_value();
                dragons.entrySet()
                        .stream()
                        .filter(map -> map.getValue().get_id()==id_new)
                        .forEach(map -> dragons.put(map.getKey(),dr));
                return sortByName(dragons);

            case "insert_key":
                dr = objServer.get_Dragon();
                dr.generate_id();
                dr.set_creationDate();
                dragons.put(objServer.get_value(), dr);
                return sortByName(dragons);

            case "count_less_then_cave":
                long count = dragons.values()
                        .stream()
                        .map(d -> d.get_depth() + d.get_numberOfTreasures())
                        .filter( value -> value < objServer.get_cave())
                        .count();
                return new COUNT_RESPONSE(count);

            case "replace_if_lowe_key":
                Dragon dragon = dragons.get(objServer.get_value());
                if (dragon != null) {
                    Dragon dragon_client = objServer.get_Dragon();
                    dragon_client.set_creationDate();
                    dragon_client.generate_id();
                    if (dragon.compareTo(dragon_client) > 0) {
                        dragons.replace(objServer.get_value(), dragon_client);
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
                .sorted(Comparator.comparing(dragon -> dragon.get_name()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private INFO_RESPONSE info(Hashtable<Integer, Dragon> dragons) {
        INFO_RESPONSE response = new INFO_RESPONSE();
        response.setSize(dragons.size());
        response.setCollectionType(dragons.getClass().toString());
        response.setElementType(Dragon.class.toString());

        Optional<LocalDateTime> min = dragons.values()
                .stream()
                .map(d -> d.get_creationDate())
                .min((localDateTime, other) -> localDateTime.compareTo(other));

        min.ifPresent(minDate -> response.setCreationDate(minDate));

        return response;
    }


}
