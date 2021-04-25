package com.example.application.tasks;

import com.example.application.main.Main;

import java.util.LinkedList;


public abstract class NumberDecomposition {

    public static String do_task(Integer value) {
        if (value == null)
            Main.show_error("Вы не ввели число или число слишком велико");

        else {
            try {
                LinkedList<Integer> numbers = decomposition(value);

                String sign = " + "; //123 = 100 + 20 + 3
                if (value < 0)
                    sign = " "; //-123 = -100 -20 -3

                StringBuilder str = new StringBuilder();
                str.append(String.format("Input: %d\nResult: ", value));
                for (int i = 0; i < numbers.size() - 1; ++i) {
                    str.append(numbers.get(i));
                    str.append(sign);
                }
                str.append(numbers.get(numbers.size() - 1));
                return str.toString();

            } catch (NumberFormatException ex) {
                Main.show_error("Некорректное число или число слишком велико, введите другое");

                return "";
            }
        }
        return null;
    }

    public static LinkedList<Integer> decomposition(int number) {
        LinkedList<Integer> numbers = new LinkedList<>();
        if (number != 0) {
            int temp = 10;
            while (number != 0) {
                int current = number % temp;
                if(current != 0) {
                    numbers.addFirst(current);
                    number -= current;
                }
                temp *= 10;
            }
        }
        else
            numbers.add(number);

        return numbers;
    }


}