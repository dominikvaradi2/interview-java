package hu.bca.library.services.impl;

import hu.bca.library.dtos.OpenLibraryWorkResponseDTO;
import hu.bca.library.exceptions.OpenLibraryException;
import hu.bca.library.models.Book;
import hu.bca.library.models.BookWithFirstPublishYearResult;
import hu.bca.library.services.BookFirstPublishYearRetrieverService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class OpenLibraryService implements BookFirstPublishYearRetrieverService {
    @Value("${open-library.works-api.base-url}")
    private String openLibraryWorksApiBaseUrl;
    @Value("${open-library.fetch-retry-count}")
    private Integer openLibraryFetchRetryCount;

    private final RestTemplate openLibraryWorksApiRestTemplate;

    public OpenLibraryService() {
        this.openLibraryWorksApiRestTemplate = new RestTemplateBuilder().build();
    }

    @Async
    @Override
    public CompletableFuture<BookWithFirstPublishYearResult> retrieveBookFirstPublishYearAsync(Book book) {
        Optional<Integer> result = this.retrieveBookFirstPublishYearWithRetry(book, openLibraryFetchRetryCount);
        
        return CompletableFuture.completedFuture(new BookWithFirstPublishYearResult(book, result));
    }

    private Optional<Integer> retrieveBookFirstPublishYearWithRetry(Book book, int retries) {
        log.info("Retrieving first publish year from open library work api for book: (id: '{}', workId: '{}').", book.getId(), book.getWorkId());

        int retryCounter = retries;
        do {
            try {
                Optional<Integer> firstPublishYear = this.getBookFirstPublishYearByWorkId(book.getWorkId());

                log.info("Successfully retrieved first publish year: '{}' from open library work api for book: (id: '{}', workId: '{}').", firstPublishYear.orElse(null), book.getId(), book.getWorkId());

                return firstPublishYear;
            } catch (Exception e) {
                retryCounter--;

                if (retryCounter > 0) {
                    log.warn("Retrieving first publish year from open library work api has failed for book: (id: '{}', workId: '{}'). Retrying...", book.getId(), book.getWorkId());
                } else {
                    log.error("Retrieving first publish year from open library work api has failed after {} retries. Book: (id: '{}', workId: '{}').", retries, book.getId(), book.getWorkId(), e);
                }
            }
        } while (retryCounter > 0);

        return Optional.empty();
    }

    private Optional<Integer> getBookFirstPublishYearByWorkId(String bookWorkId) throws RestClientException, OpenLibraryException {
        String absoluteUrl = String.format("%s/%s.json", openLibraryWorksApiBaseUrl, bookWorkId);

        log.debug("Calling open library works api to get first publish year for book. url: '{}'", absoluteUrl);

        ResponseEntity<OpenLibraryWorkResponseDTO> response = openLibraryWorksApiRestTemplate.getForEntity(absoluteUrl, OpenLibraryWorkResponseDTO.class);

        if (!response.hasBody() || !response.getStatusCode().is2xxSuccessful()) {
            throw new OpenLibraryException("Unexpected error occurred when calling open library works api.", response.getStatusCode());
        }

        log.debug(response);

        Optional<String> firstPublishDate = response.getBody().first_publish_date();
        return firstPublishDate.map(s -> Integer.parseInt(s.substring(s.length() - 4)));

    }
}
