package com.example.application.tasks;

import java.util.Arrays;
import java.util.HashSet;

public abstract class MagicSquare {

    public static String do_task(int[] matrix) {

        if (is_magic(matrix))
            return "Ваш квадрат уже магический";

        int sum = 0; //сумма элементов матрицы
        for (int num : matrix) sum += num;

        int n = 1;
        int siam_sum, lower = 0, bigger = 9;
        int abs1, abs2, old_abs1, old_abs2;

        abs1 = Math.abs(sum - lower);
        abs2 = Math.abs(sum - bigger);
        old_abs1 = abs1;
        old_abs2 = abs2;

        //ниже описан поиск ближайшей суммы элементов сиамской матрицы
        if (abs1 < abs2) {
            while (old_abs1 >= abs1) {
                lower = 9 * (--n);
                old_abs1 = abs1;
                abs1 = Math.abs(sum - lower);
            }
            siam_sum = (n + 1) * 9;
        } else {
            while (old_abs2 >= abs2) {
                bigger = 9 * (++n);
                old_abs2 = abs2;
                abs2 = Math.abs(sum - bigger);
            }
            siam_sum = (n - 1) * 9;
        }

        int min_elem = matrix[0];
        int max_elem = matrix[0];
        for(int i = 1; i < matrix.length; ++i) {
            if(matrix[i] < min_elem)
                min_elem = matrix[i];
            if(matrix[i] > max_elem)
                max_elem = matrix[i];
        }

        int mag_const = siam_sum/3;
        int[] min_matrix = new int[9];
        int[] temp_matrix = new int[9];
        int temp_price;
        int price_of_min_matrix= -1;

        int min_siam_elem = siam_sum/9 -4;

        /*if(min_elem > 1)
            min_elem = 1;*/
        if (min_elem > min_siam_elem)
            min_elem = min_siam_elem;
        //else if (min_elem )
        if(mag_const > max_elem)
            max_elem = siam_sum;
        for(int a = min_elem; a <= max_elem; ++a) { // поиск матрицы с наименьшей стоимостью
            for(int b = min_elem; b <= max_elem; ++b) {
                for(int c = min_elem; c <= max_elem; ++c) {
                    if (/*((a+b+c)*3 == mag_const) && */(b != 2*c) && (c!= 2*b) && (c != b)) {
                        siam_method(temp_matrix, a, b, c);
                        lowerPriceMatrix(matrix, temp_matrix, temp_matrix.length); // крутит, вертит temp_matrix чтобы найти самый "дешёвый" её вариант
                        temp_price = price_matrix(matrix, temp_matrix);
                        if ((temp_price < price_of_min_matrix || price_of_min_matrix == -1) && is_magic(temp_matrix)) {
                            price_of_min_matrix = temp_price;
                            System.arraycopy(temp_matrix, 0, min_matrix, 0, temp_matrix.length);
                        }
                        Arrays.fill(temp_matrix, 0);
                    }
                }
            }
        }

        StringBuilder min_magic_square = new StringBuilder();
        int size = (int) Math.sqrt(matrix.length);

        min_magic_square.append("Input:\n");
        make_string_of_matrix(min_magic_square, matrix, size);
        min_magic_square.append("Result:\n");

        make_string_of_matrix(min_magic_square, min_matrix, size);
        min_magic_square.append(String.format("Price = %d", price_of_min_matrix));

        return min_magic_square.toString();
    }

    private static int price_matrix(int[] source_matrix, int[] compare_matrix) { //вычисление "цены" матрицы
        int price = 0;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j)
                price += Math.abs(source_matrix[i * 3 + j] - compare_matrix[i * 3 + j]);
        }
        return price;
    }

    private static void swap_row(int[] matrix) {// функция для свапа 1 и 3 строк в матрице
        int temp;
        for (int i = 0; i < 3; ++i) {
            temp = matrix[i];
            matrix[i] = matrix[i + 6];
            matrix[i + 6] = temp;
            //swap(matrix[i], matrix[6 + i]);
        }
    }

    private static void swap_col(int[] matrix) { // функция для свапа 1 и 3 столбцов в матрице
        int temp;
        for (int i = 0; i < 3; ++i) {
            temp = matrix[i * 3];
            matrix[i * 3] = matrix[i * 3 + 2];
            matrix[i * 3 + 2] = temp;
            //swap(matrix[i * 3], matrix[i * 3 + 2]);
            }
    }

    private static void transpose(int[] matrix) { //транспонирование матриц
        int n = (int) Math.sqrt(matrix.length);
        int temp;
        for (int i = 0; i < n; ++i) {
            for (int j = i+1; j < n; ++j) {
                temp = matrix[i*n + j];
                matrix[i*n + j] = matrix[j*n + i];
                matrix[j*n + i] = temp;
                //swap(matrix[i*n + j], matrix[j*n + i]);
            }
        }
    }

    private static void siam_method(int[] matrix, int a, int b, int c) {
        int size = matrix.length;
        int n = (int) Math.sqrt(size);
        int cur_p = n/2;
        int elem = a + 3*b + 2*c;
        boolean right_col = false;
        for(int i = 0; i < size; ++i) {
            if(matrix[cur_p] != 0) { //если елемент, стоящий на правой верхней диагонали занят, то след позиция - под текущей
                elem = elem - c + 3*b;
                cur_p += 2*n-1;
                if(cur_p > size-1) // если текущая позиция находилась на самой нижней строчке, то след позиция - на первой строчке
                    cur_p -= size - n;
            }
            elem -= b;
            matrix[cur_p] = elem;
            for(int j = 0; j <= n; ++j) {
                if(cur_p == j*n - 1) { // если текущая позиция находится на крайнем правом столбце
                    right_col = true;
                    break;
                }
            }
            if(right_col) {// если текущая позиция находится на крайнем правом столбце
                right_col = false;
                cur_p -= 2*n-1;
                if(cur_p < 0) // если текущей позицией был 1 элемент самого правого столбца, то след элемент - первый элемент последней строки
                    cur_p += size;
            }
            else {
                cur_p -= (n - 1); // идем по верхней диагонали
                if (cur_p < 0)
                    cur_p += size;
            }
        }
    }

    private static void lowerPriceMatrix(int[] source_matrix, int[] compare_matrix, int n) {
        //меняем столбцы и строчки в матрице, чтобы узнать какая перестановка имеет минимальную цену
        int[] prices = new int[8];
        int[] temp = new int[n];
        System.arraycopy(compare_matrix, 0, temp, 0, compare_matrix.length);

        for(int i = 0; i < 8; ++i) {
            prices[i] = price_matrix(source_matrix, temp);
            if (i % 2 == 0)
                swap_row(temp);
            else if (i != 3)
                swap_col(temp);
            else
                transpose(temp);
        }

        int index_min_elem = 0;
        int temp_min = prices[0];
        for(int i = 1; i < prices.length; ++i) {
            if(prices[i] < temp_min) {
                index_min_elem = i;
                temp_min = prices[i];
            }
        }
        switch(index_min_elem) {
            case 0:
                return; //минимальная цена у обычной сиамской матрицы
            case 1:
                swap_row(compare_matrix);
                return;//минимальная цена у сиамской матрицы со свапнутыми 1 и 3 строками
            case 2:
                swap_row(compare_matrix);
                swap_col(compare_matrix);
                return;//минимальная цена у сиамской матрицы сначала со свапнутыми 1 и 3 строками, а затем с 1 и 3 столбцами
            case 3:
                swap_row(compare_matrix);
                swap_col(compare_matrix);
                swap_row(compare_matrix);
                return;//минимальная цена у сиамской матрицы сначала со свапнутыми 1 и 3 строками, затем с 1 и 3 столбцами, а потом ещё раз со строками
            case 4:
                transpose(compare_matrix);
                swap_row(compare_matrix);
                return;// минимальная цена у транспонированной смамской матрицы, у которой свапнуты строки
            case 5:
                transpose(compare_matrix);
                return;// минимальная цена у транспонированной смамской матрицы
            case 6:
                transpose(compare_matrix);
                swap_col(compare_matrix);
                return;// минимальная цена у транспонированной смамской матрицы, у которой свапнуты столбцы
            case 7:
                transpose(compare_matrix);
                swap_col(compare_matrix);
                swap_row(compare_matrix);
        }
    }

    private static boolean is_magic(int[] matrix) { // проверка матрицы на "магичность"
        HashSet<Integer> set = new HashSet<>();
        for(int a: matrix) { // проверяем все ли элементы в матрице разные (должны быть разные, если это маг квадрат)
            if(!set.add(a))
                return false;
        }

        int sum1 = matrix[0] + matrix[1] + matrix[2];
        int sum2 = 0, sum3 = 0;
        for (int i = 0; i < 3; ++i) {
            sum2 += matrix[i + 3];
            sum3 += matrix[i + 6];
        }
        if (sum1 != sum2 || sum1 != sum3)
            return false;
        sum2 = 0;
        sum3 = 0;
        for (int i = 0; i < 3; ++i) {
            sum2 += matrix[i * 3 + 1];
            sum3 += matrix[i * 3 + 2];
        }
        if (sum1 != sum2 || sum1 != sum3)
            return false;

        sum2 = matrix[0] + matrix[4] + matrix[8];
        sum3 = matrix[2] + matrix[4] + matrix[6];

        return sum1 == sum2 && sum1 == sum3;
    }

    private static void make_string_of_matrix(StringBuilder str, int[] matrix, int n) { //делаю из матрицы строку (записывается всё в str)
        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < n; ++j){
                str.append(matrix[i*n + j]);
                str.append(' ');
            }
            str.append('\n');
        }
    }
}