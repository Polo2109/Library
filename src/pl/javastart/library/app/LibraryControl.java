package pl.javastart.library.app;

import pl.javastart.library.exception.*;
import pl.javastart.library.io.ConsolePrinter;
import pl.javastart.library.io.DataReader;
import pl.javastart.library.io.file.FileManager;
import pl.javastart.library.io.file.FileManagerBuilder;
import pl.javastart.library.model.*;

import java.time.Year;
import java.util.Comparator;
import java.util.InputMismatchException;

public class LibraryControl {

    private ConsolePrinter printer = new ConsolePrinter();
    private DataReader dataReader = new DataReader(printer);
    private FileManager fileManager;

    private Library library;

    public LibraryControl() {
        fileManager = new FileManagerBuilder(printer, dataReader).build();
        try {
            library = fileManager.importData();
            printer.printLine("Zaimportowane dane z pliku");
        }catch (DataImportException |InvalidDataException e){
            printer.printLine(e.getMessage());
            printer.printLine("Zainicjowano nową bazę");
            library = new Library();
        }
    }


    void controlLoop(){
        Option option;

        do{
            printOptions();
            option = getOption();
            switch (option){
                case ADD_BOOK:
                    addBook();
                    break;
                case ADD_MAGAZINE:
                    addMagazine();
                    break;
                case PRINT_BOOKS:
                    printBooks();
                    break;
                case PRINT_MAGAZINES:
                    printMagazines();
                    break;
                case DELETE_BOOKS:
                    deleteBooks();
                    break;
                case DELETE_MAGAZINES:
                    deleteMagazines();
                    break;
                case ADD_USER:
                    addUser();
                    break;
                case PRINT_USERS:
                    printusers();
                    break;
                case FIND_BOOK:
                    findBook();
                    break;
                case EXIT:
                    exit();
                    break;
                default:
                    printer.printLine("Nie ma takiej opcji, wprowadź ponownie: ");
            }
        }while (option != Option.EXIT);
    }

    private void findBook() {
        printer.printLine("Podaj tytuł publikacji");
        String title = dataReader.getString();
        String notFoundMessage = "Brak publikacji o takim tytule";
        library.findPublicationByTitle(title)
                .map(Publication::toString)
                .ifPresentOrElse(System.out::println, () -> System.out.println(notFoundMessage));

    }

    private void printusers() {
        printer.printUsers(library.getSortedLibraryUsers(
                Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER)
        ));
    }

    private void addUser() {
        LibraryUser libraryUser = dataReader.createLibraryUser();
        try{
            library.addUser(libraryUser);
        }catch (UserAlreadyExistsException e){
            printer.printLine(e.getMessage());
        }
    }

    private Option getOption() {
        boolean optionOK = false;
        Option option = null;
        while (!optionOK){
            try{
                option = Option.createFromInt(dataReader.getIn());
                optionOK = true;
            }catch (NoSuchOptionException e) {
                printer.printLine((e.getMessage() + ", podaj ponownie:"));
            }catch (InputMismatchException ignored){
                printer.printLine("Wprowadzono wartość, która nie jest liczbą, podaj ponownie");
            }
        }
        return option;
    }

    private void printMagazines() {
        printer.printMagazines(library.getSortedPublications(
                Comparator.comparing(Publication::getTitle, String.CASE_INSENSITIVE_ORDER)
        ));
    }

    private void addMagazine() {
        try {
            Magazine magazine = dataReader.readAndCreateMagazine();
            library.addPublication(magazine);
            printer.printLine("Dodano nowy magazyn");
        }catch (InputMismatchException e){
            printer.printLine("Nie udało się utworzyć magazynu, nieporpawne dane");
        }catch (ArrayIndexOutOfBoundsException e){
            printer.printLine("Osiągnięto limit pojemności, nie można dodać kolejnego magazynu");
        }
    }

    private void deleteMagazines() {
        try{
            Magazine magazine = dataReader.readAndCreateMagazine();
            if(library.removePublication(magazine))
                printer.printLine("Usunięto magazyn");
            else
                printer.printLine("Brak wskazanego magazynu");
        }catch (InputMismatchException e){
            printer.printLine("Nie udało się usunąć magazynu, niepoprawne dane");
        }
    }

    private void printBooks() {
        printer.printBooks(library.getSortedPublications(
                Comparator.comparing(Publication::getTitle, String.CASE_INSENSITIVE_ORDER)
        ));
    }

    private void addBook() {
        try {
            Book book = dataReader.readAndCreateBook();
            library.addPublication(book);
            printer.printLine("Dodano nową książkę");
        }catch (InputMismatchException e){
            printer.printLine("Nie udało się utworzyć książki, nieporpawne dane");
        }catch (ArrayIndexOutOfBoundsException e){
            printer.printLine("Osiągnięto limit pojemności, nie można dodać kolejnej książki");
        }
    }
    private void deleteBooks() {
        try{
            Book book = dataReader.readAndCreateBook();
            if(library.removePublication(book))
                printer.printLine("Usunięto książkę");
            else
                printer.printLine("Brak wskazanej książki");
        }catch (InputMismatchException e){
            printer.printLine("Nie udało się usunąć książki, niepoprawne dane");
        }
    }

    private void exit(){
        try {
            fileManager.exportData(library);
            printer.printLine("Export danych do pliku zakończony powodzeniem");
        } catch (DataExportException e){
            printer.printLine(e.getMessage());
        }
        printer.printLine("Koniec programu, papa!");
        dataReader.close();
    }

    private void printOptions() {
        printer.printLine("Wybierz opcję: ");
        for (Option option : Option.values()) {
            printer.printLine(option.toString());

        }

    }
    private enum Option {
        EXIT(0, "wyjście z programu"),
        ADD_BOOK(1, "dodanie nowej książki"),
        ADD_MAGAZINE(2, "dodanie nowego magazynu"),
        PRINT_BOOKS(3, "wyświetl dostępne książki"),
        PRINT_MAGAZINES(4, "wyświetl dostępne magazyny"),
        DELETE_BOOKS(5, "usuń książkę"),
        DELETE_MAGAZINES(6, "usuń magazyn"),
        ADD_USER(7, "dodaj czytelnika"),
        PRINT_USERS(8, "Wyświetl czytelników"),
        FIND_BOOK(9, "Wyszukaj książkę");


        private int option;
        private String description;

        public int getOption() {
            return option;
        }

        public String getDescription() {
            return description;
        }

        Option(int option, String description) {
            this.option = option;
            this.description = description;
        }

        @Override
        public String toString() {
            return option + " - " + description;
        }
        static Option createFromInt(int option) throws NoSuchOptionException{
            try {
                return Option.values()[option];
            }catch (ArrayIndexOutOfBoundsException e){
                throw new NoSuchOptionException("Brak opcji o id " + option);
            }
        }
    }

}
