package hu.bca.library.controllers;

import hu.bca.library.models.Book;
import hu.bca.library.services.BookService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RepositoryRestController("books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ResponseStatus(HttpStatus.CREATED)

    @RequestMapping("/{bookId}/add_author/{authorId}")
    @ResponseBody
    Book addAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        return this.bookService.addAuthor(bookId, authorId);
    }

    @PatchMapping("/update-all-with-year")
    @ResponseBody
    Collection<Book> updateAllWithYear() {
        return this.bookService.updateAllWithFirstPublishYear();
    }
}
