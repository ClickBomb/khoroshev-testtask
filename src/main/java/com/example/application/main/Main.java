package com.example.application.main;

import com.example.application.tasks.MagicSquare;
import com.example.application.tasks.NumberDecomposition;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.IOUtils;
import org.vaadin.olli.FileDownloadWrapper;


import java.io.*;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

@Route
public class Main extends VerticalLayout {
    private final ComboBox<String> box_tasks;
    private final IntegerField number_input;
    private final TextArea area_result;
    private final Button btn_solve_number;
    private final Button btn_solve_matrix;

    private final IntegerField[] matrix;

    public Main() {
        box_tasks = new ComboBox<>("Задание");
        box_tasks.setPlaceholder("Выберите задание");
        box_tasks.setItems("Расширенная форма числа", "Магический квадрат");
        box_tasks.setWidth("300px");
        box_tasks.setAllowCustomValue(false);

        number_input = new IntegerField("Число");
        number_input.setPlaceholder("Введите число");
        number_input.setVisible(false);

        area_result = new TextArea();
        area_result.setReadOnly(true);

        matrix = new IntegerField[9];
        for(int i = 0; i < matrix.length; ++i) {
            matrix[i] = new IntegerField();
            matrix[i].setWidth("50px");
        }



        VerticalLayout layout_matrix = make_matrix(matrix);
        layout_matrix.setVisible(false);

        btn_solve_number = new Button("Посчитать");  //две кнопки "Посчитать" для каждой задачи
        btn_solve_matrix = new Button("Посчитать");
        btn_solve_number.setVisible(false);
        btn_solve_matrix.setVisible(false);

        HorizontalLayout input_and_solve = new HorizontalLayout(number_input, btn_solve_number, layout_matrix, btn_solve_matrix);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1); //можно загрузить только 1 файл
        upload.setMaxFileSize(300); // файл должен не превышать 300 байт
        upload.setUploadButton(new Button("Загрузить"));

        upload.addSucceededListener(event -> {
            String input_text = createTextComponent(buffer.getInputStream());
            if(box_tasks.getValue().equals("Расширенная форма числа")) {
                try {
                    number_input.setValue(Integer.parseInt(input_text));
                } catch (NumberFormatException ignored) {
                    show_error("Parse Int error, please check the number in your file");
                    upload.interruptUpload();
                    number_input.setValue(null);
                    area_result.setValue(null);
                }
            }
            else if (box_tasks.getValue().equals("Магический квадрат")) {
                clear_matrix();
                StringBuilder num = new StringBuilder();
                int cur_pose = 0;
                char cur_char;
                for(int i = 0; i < input_text.length(); ++i) {
                    cur_char = input_text.charAt(i);
                    if(cur_char == '\r') continue;
                    if(cur_char == ' ' || cur_char == '\n') {
                        try {
                            matrix[cur_pose++].setValue(Integer.parseInt(num.toString()));
                            num.delete(0, num.length());
                        }
                        catch (NumberFormatException ignored) {
                            clear_matrix();
                            show_error(String.format("Parse Int error on pose %d", cur_pose));
                            break;
                        }
                        catch (ArrayIndexOutOfBoundsException exception) {
                            clear_matrix();
                            area_result.setValue(null);
                            show_error("Too much elements in file, matrix must be 3x3!");
                            break;
                        }
                    }
                    else
                        num.append(cur_char);
                }
                if(cur_pose != 9) {
                    clear_matrix();
                    area_result.setValue(null);
                    show_error("Too less elements in file!");
                }
            }
        });

        upload.setDropAllowed(false);


        Button btn_save = new Button("Сохранить");
        FileDownloadWrapper fd = new FileDownloadWrapper(getStreamResource("save.txt",
                String.format("%s\n%s", box_tasks.getValue(), area_result.getValue())));
        fd.wrapComponent(btn_save);


        btn_save.addClickListener(buttonClickEvent -> {
            if(area_result.getValue() == null || area_result.getValue().isEmpty()) {
                show_error("Сейчас в поле результата пусто");
            }
        });


        HorizontalLayout save_and_upload = new HorizontalLayout(fd, upload);
        save_and_upload.setDefaultVerticalComponentAlignment(Alignment.END);


        add(box_tasks, input_and_solve, area_result, save_and_upload);


        btn_solve_number.addClickListener(event ->  {
            area_result.setValue(NumberDecomposition.do_task(number_input.getValue()));
            fd.setResource(getStreamResource("save.txt",
                    String.format("%s\n%s", box_tasks.getValue(), area_result.getValue())));
        });

        btn_solve_matrix.addClickListener(event -> {
            int[] values = new int[9];
            int i = 0;
            for (; i < matrix.length; ++i) {
                if(matrix[i].getValue() == null) {
                    show_error(String.format("В ячейке №%d введено некорректное число, введите нормальное число", i));
                    clear_matrix();
                    break;
                }
                values[i] = matrix[i].getValue();
            }
            if(i == 9) {
                area_result.setValue(MagicSquare.do_task(values));
                fd.setResource(getStreamResource("save.txt",
                        String.format("%s\n%s", box_tasks.getValue(), area_result.getValue())));
            }
        });

        box_tasks.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                box_tasks.setPlaceholder("No option selected");
            } else {
                if (box_tasks.getValue().equals("Расширенная форма числа")) {
                    layout_matrix.setVisible(false);
                    input_and_solve.setDefaultVerticalComponentAlignment(Alignment.END);
                    number_input.setVisible(true);
                    number_input.setValue(null);

                    btn_solve_matrix.setVisible(false);
                    btn_solve_number.setVisible(true);

                    area_result.setLabel("Расширенная форма:");
                    area_result.setValue("");
                }
                else if (box_tasks.getValue().equals("Магический квадрат")) {
                    number_input.setVisible(false);
                    input_and_solve.setDefaultVerticalComponentAlignment(Alignment.CENTER);
                    clear_matrix();
                    layout_matrix.setVisible(true);

                    btn_solve_matrix.setWidthFull();
                    btn_solve_matrix.setVisible(true);
                    btn_solve_number.setVisible(false);

                    area_result.setLabel("Магический квадрат:");
                    area_result.setValue("");
                }
            }
        });
    }


    private VerticalLayout make_matrix (IntegerField[] matrix) {
        VerticalLayout cols = new VerticalLayout();
        for(int i = 0; i <= 6; i+=3) {
            HorizontalLayout rows = new HorizontalLayout(matrix[i], matrix[i+1], matrix[i+2]);
            cols.add(rows);
        }
        return cols;
    }

    public static void show_error(String error_description) {
        Dialog dialog = new Dialog();
        dialog.add(new Text(error_description));
        dialog.setWidth("400px");
        dialog.setHeight("150px");
        dialog.open();
    }

    private void clear_matrix() {
        for(IntegerField num : this.matrix)
            num.setValue(null);
    }

    private String createTextComponent(InputStream stream) {
        String text;
        try {
            text = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        return text;
    }

    public StreamResource getStreamResource(String filename, String content) {
        return new StreamResource(filename,
                () -> new ByteArrayInputStream(content.getBytes()));
    }
}