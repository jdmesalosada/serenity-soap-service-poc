package tasks;

import model.GetCountryRequest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.HashMap;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class AskForCountry implements Task {

    private final String resource = "/ws";
    private final GetCountryRequest countryRequest;

    public AskForCountry(GetCountryRequest countryRequest) {
        this.countryRequest
                = countryRequest;
    }

    public static Performable information(GetCountryRequest countryRequest) {
        return instrumented(AskForCountry.class, countryRequest);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to(resource)
                        .with(
                                req -> req.headers(headers())
                                        .body(countryRequest)
                        )
        );
    }

    private HashMap<String, String> headers() {
        return new HashMap<String, String>() {{
            put("Content-Type", "text/xml;charset=UTF-8");
            put("SOAPAction", "");
        }};
    }
}
