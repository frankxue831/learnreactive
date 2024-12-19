import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Locale;

public final class ReactorServerSimulation {

    private static final Logger logger = LoggerFactory.getLogger(ReactorServerSimulation.class);
    private static final int MESSAGE_INTERVAL_MS = 200;
    private static final int MESSAGE_COUNT = 20;
    private static final int PROCESSING_DELAY_MS = 500;

    private ReactorServerSimulation() {
    }

    public static void main(String... args) {
        // Simulate an incoming stream of messages (a producer)
        Flux<String> incomingMessages = Flux.interval(Duration.ofMillis(MESSAGE_INTERVAL_MS)) // Messages every 200ms
                .map(i -> "Message " + i)         // Create message with sequence number
                .take(MESSAGE_COUNT);                       // Simulate 20 incoming messages

        // Process the incoming messages
        incomingMessages
                .doOnNext(message -> logger.info("Received: {}", message)) // Log received message
                .filter(message -> message.contains("2"))                       // Only process messages containing "2"
                .map(message -> message.toUpperCase(Locale.ROOT))                          // Transform message to uppercase
                .flatMap(ReactorServerSimulation::simulateProcessing)                // Simulate async processing
                .doOnError(error -> logger.error("Error: {}", error.getMessage())) // Handle errors
                .doOnComplete(() -> logger.info("All messages processed")) // Log on completion
                .subscribeOn(Schedulers.boundedElastic())                        // Use a thread pool for processing
                .subscribe(processedMessage -> logger.info("Processed: {}", processedMessage));
    }

    // Simulate processing of each message asynchronously
    private static Flux<String> simulateProcessing(String message) {
        return Flux.just(message)
                .delayElements(Duration.ofMillis(PROCESSING_DELAY_MS)) // Simulate delay for processing
                .doOnNext(msg -> logger.info("Processing: {}", msg));
    }
}