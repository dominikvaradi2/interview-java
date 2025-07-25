package hu.bca.library.dtos;

import java.util.Optional;

/* We might add more fields in the future when needed.
 * Check the response to add more fields: https://openlibrary.org/works/OL2163638W.json
 */
public record OpenLibraryWorkResponseDTO(Optional<String> first_publish_date) {
}