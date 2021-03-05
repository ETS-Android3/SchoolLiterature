package fitisov123.schoolliterature;

public class Book {
    private String bookName;
    private String bookAuthor;
    private String bookText;

    public Book(String bookName, String bookAuthor, String bookText) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookText = bookText;
    }

    public Book(String bookName, String bookAuthor) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookText = null;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookText() {
        return bookText;
    }
}
