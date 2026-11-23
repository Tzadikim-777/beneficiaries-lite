package mx.com.empresa.backend.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

	private final String code;
	private final String message;
	private final LocalDateTime timestamp;
	private final List<String> details;

}
