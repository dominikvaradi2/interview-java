package hu.bca.library.services.impl;

import hu.bca.library.models.Author;
import hu.bca.library.models.Book;
import hu.bca.library.repositories.AuthorRepository;
import hu.bca.library.repositories.BookRepository;
import hu.bca.library.services.BookFirstPublishYearRetrieverService;
import hu.bca.library.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookFirstPublishYearRetrieverService bookFirstPublishYearRetrieverService;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, BookFirstPublishYearRetrieverService bookFirstPublishYearRetrieverService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookFirstPublishYearRetrieverService = bookFirstPublishYearRetrieverService;
    }

    @Override
    public Book addAuthor(Long bookId, Long authorId) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Book with id %s not found", bookId));
        }
        Optional<Author> author = this.authorRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Author with id %s not found", authorId));
        }

        List<Author> authors = book.get().getAuthors();
        authors.add(author.get());

        book.get().setAuthors(authors);
        return this.bookRepository.save(book.get());
    }

    @Override
    public Collection<Book> updateAllWithFirstPublishYear() {
        Iterable<Book> books = this.bookRepository.findAll();

        List<Book> updatedBooks = StreamSupport.stream(books.spliterator(), false)
                .map(bookFirstPublishYearRetrieverService::retrieveBookFirstPublishYearAsync)
                .map(cf -> cf.thenApply(result -> {
                    result.book().setYear(result.firstPublishYear().orElse(null));

                    return result.book();
                })).map(CompletableFuture::join).toList();


        Iterable<Book> savedBooks = this.bookRepository.saveAll(updatedBooks);

        return StreamSupport.stream(savedBooks.spliterator(), false).toList();
    }
}
