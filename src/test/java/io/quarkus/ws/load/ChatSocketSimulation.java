package io.quarkus.ws.load;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.repeat;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.ws;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class ChatSocketSimulation extends Simulation {

        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .doNotTrackHeader("1")
                        .acceptLanguageHeader("en-US,en;q=0.5")
                        .acceptEncodingHeader("gzip, deflate")
                        .userAgentHeader("Gatling")
                        .wsBaseUrl("ws://localhost:8080");

        ScenarioBuilder scn = scenario("ChatSocket")
                        .exec(
                                        exec(session -> session.set("id", "Gatling" + session.userId())),
                                        ws("Connect").connect("/chat/#{id}"),
                                        pause(1),
                                        repeat(5, "i").on(
                                                        ws("Say Hello WS")
                                                                        .sendText(
                                                                                        "{\"type\":\"CHAT_MESSAGE\",\"from\":\"#{id}\",\"message\": \"Hello #{i} from #{id}!\"}")),
                                        pause(1),
                                        ws("Close").close());

        {
                setUp(scn.injectOpen(rampUsers(200).during(60))).protocols(httpProtocol);
        }
}