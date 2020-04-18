package pl.javastart.library.io;

import pl.javastart.library.model.Book;
import pl.javastart.library.model.LibraryUser;
import pl.javastart.library.model.Magazine;

import java.time.Year;
import java.util.Scanner;

public class DataReader {

    private Scanner sc = new Scanner(System.in);
    private ConsolePrinter printer;

    public DataReader(ConsolePrinter printer) {
        this.printer = printer;
    }
    public String getString(){
        return sc.nextLine();
    }

    public int getIn(){
        try {
            return sc.nextInt();
        }finally {
            sc.nextLine();
        }

    }
    public Book readAndCreateBook(){
        printer.printLine("Tytuł:");
        String title = sc.nextLine();
        printer.printLine("Autor");
        String author = sc.nextLine();
        printer.printLine("Rok wydania:");
        int releaseDate = getIn();
        printer.printLine("Liczba stron:");
        int pages = getIn();
        printer.printLine("Wydawnictwo:");
        String publisher = sc.nextLine();
        printer.printLine("ISBN:");
        String isbn = sc.nextLine();

        return new Book(title, author, releaseDate, pages, publisher, isbn);
    }

    public Magazine readAndCreateMagazine(){
        printer.printLine("Tytuł:");
        String title = sc.nextLine();
        printer.printLine("Rok wydania:");
        int year = getIn();
        printer.printLine("Wydawnictwo:");
        String publisher = sc.nextLine();
        printer.printLine("Język:");
        String language = sc.nextLine();
        printer.printLine("Dzień:");
        int day = getIn();
        printer.printLine("Miesiąc:");
        int month = getIn();

        return new Magazine(title, publisher, year, day, month, language);
    }
    public LibraryUser createLibraryUser(){
        printer.printLine("Imię:");
        String name = sc.nextLine();
        printer.printLine("Nazwisko:");
        String surname = sc.nextLine();
        printer.printLine("PESEL");
        String pesel = sc.nextLine();
        return new LibraryUser(name, surname, pesel);
    }
    public void close(){
        sc.close();
    }
}
