package ru.murad.NauJava.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.murad.NauJava.service.BookService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommandProcessor {
    private final BookService bookService;

    @Autowired
    public CommandProcessor(BookService bookService) {
        this.bookService = bookService;
    }

    public void processCommand(String input) {
        List<String> args = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (m.find()) {
            args.add(m.group(1).replace("\"", ""));
        }

        if (args.isEmpty()) return;

        String action = args.get(0).toLowerCase();

        try {
            switch (action) {
                case "create" -> {
                    Long id = Long.valueOf(args.get(1));
                    String title = args.get(2);
                    String author = args.get(3);

                    bookService.createBook(id, title, author);
                    System.out.println("Книга успешно добавлена!");
                }
                case "read" -> {
                    Long id = Long.valueOf(args.get(1));
                    bookService.findById(id).ifPresentOrElse(
                            book -> System.out.println(book.getTitle() + " [" + book.getAuthor().getFullName() + "]"),
                            () -> System.out.println("Книга не найдена")
                    );
                }
                case "update" -> {
                    if (args.size() < 4) {
                        System.out.println("Ошибка! Формат: update <id> \"Название\" \"Автор\"");
                    } else {
                        Long id = Long.valueOf(args.get(1));
                        String newTitle = args.get(2);
                        String newAuthor = args.get(3);

                        bookService.updateBook(id, newTitle, newAuthor);
                        System.out.println("Данные книги с ID " + id + " успешно обновлены!");
                    }
                }
                case "list" -> {
                    bookService.getAllBooks().forEach(b ->
                            System.out.println(b.getId() + ": \"" + b.getTitle() + "\" — " +
                                    (b.getAuthor() != null ? b.getAuthor().getFullName() : "Автор не указан")));
                }
                case "delete" -> {
                    bookService.deleteBook(Long.valueOf(args.get(1)));
                    System.out.println("Книга удалена.");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Ошибка! Недостаточно аргументов. Пример: create 1 \"Война и мир\" Лев Толстой");
        } catch (Exception e) {
            System.out.println("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}