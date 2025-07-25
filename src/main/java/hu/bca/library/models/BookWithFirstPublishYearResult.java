package hu.bca.library.models;

import java.util.Optional;

public record BookWithFirstPublishYearResult(Book book, Optional<Integer> firstPublishYear) {
}
