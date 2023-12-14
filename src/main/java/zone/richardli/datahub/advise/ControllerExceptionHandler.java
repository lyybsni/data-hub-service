package zone.richardli.datahub.advise;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zone.richardli.datahub.advise.exceptions.InvalidInputException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public void handleInvalidInput(HttpServletResponse response, InvalidInputException e) throws IOException {
        response.sendError(400, e.toString());
    }

}
