package hu.bca.library.services;

import hu.bca.library.models.Book;
import hu.bca.library.models.BookWithFirstPublishYearResult;

import java.util.concurrent.CompletableFuture;

public interface BookFirstPublishYearRetrieverService {
    CompletableFuture<BookWithFirstPublishYearResult> retrieveBookFirstPublishYearAsync(Book book);
}
