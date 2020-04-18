package pl.javastart.library.io.file;

import pl.javastart.library.exception.DataExportException;
import pl.javastart.library.exception.DataImportException;
import pl.javastart.library.exception.InvalidDataException;
import pl.javastart.library.model.*;

import java.io.*;
import java.time.Year;
import java.util.Collection;
import java.util.Scanner;

public class CsvFileManager implements FileManager {
    private static final String FILE_NAME = "Library.csv";
    private static final String USERS_FILE_NAME = "Library_users.csv";

    @Override
    public Library importData() {
        Library library = new Library();
        importPublications(library);
        importUsers(library);
        return library ;
    }

    private void importUsers(Library library) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(USERS_FILE_NAME))){
            bufferedReader.lines()
                    .map(this::createUserFromString)
                    .forEach(library::addUser);

        } catch (FileNotFoundException e) {
            throw new DataImportException("Brak pliku" + FILE_NAME);
        } catch (IOException e) {
            throw new DataImportException("Błąd occzytu pliku" + FILE_NAME);
        }
    }

    private LibraryUser createUserFromString(String csvText) {
        String [] split =csvText.split(";");
        String name = split[0];
        String surname = split[1];
        String pesel = split[2];
        return new LibraryUser(name, surname, pesel);

    }

    private void importPublications(Library library) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME))){
            bufferedReader.lines()
                    .map(this::createObjectFromString)
                    .forEach(library::addPublication);

        } catch (FileNotFoundException e) {
            throw new DataImportException("Brak pliku" + FILE_NAME);
        } catch (IOException e) {
            throw new DataImportException("Błąd occzytu pliku" + FILE_NAME);
        }
    }

    private Publication createObjectFromString(String csvText) {
        String[] split =csvText.split(";");
        String type = split[0];
        if(Book.TYPE.equals(type)){
            return createBook(split);
        }else if (Magazine.TYPE.equals(type)){
            return createMagazine(split);
        }
        throw new InvalidDataException("Nieznany typ publikacji " + type);
    }

    private Magazine createMagazine(String[] data) {
        String title = data[1];
        String publisher = data[2];
        int year = Integer.valueOf(data[3]);
        int day = Integer.valueOf(data[4]);
        int month = Integer.valueOf(data[5]);
        String language = data[6];
        return new Magazine(title, publisher, year, month, day, language);
    }

    private Book createBook(String[] data) {
        String title = data[1];
        String publisher = data[2];
        int year = Integer.valueOf(data[3]);
        String author = data[4];
        int pages = Integer.valueOf(data[5]);
        String isbn = data[6];
        return new Book (title, author, year, pages, publisher, isbn);
    }

    @Override
    public void exportData(Library library) {
        exportPublications(library);
        exportUsers(library);
    }

    private void exportUsers(Library library) {
        Collection<LibraryUser> libUsers = library.getUsers().values();
        exportToCsv(libUsers, USERS_FILE_NAME);
    }

    private void exportPublications(Library library) {
        Collection<Publication> publications = library.getPublications().values();
        exportToCsv(publications, FILE_NAME);
    }
    private <T extends CsvConvertible> void exportToCsv(Collection<T> collection, String fileName){
        try(FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
            for (T element: collection) {
                bufferedWriter.write(element.toCsv());
                bufferedWriter.newLine();
            }
        }
        catch (IOException e){
            throw new DataExportException("Błąd zapisu danych do pliku " + fileName);
    }
}
}