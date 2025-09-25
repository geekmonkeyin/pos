package com.gkmonk.pos.configuration;

// boot/SeedData.java
import com.gkmonk.pos.model.ai.ChatOption;
import com.gkmonk.pos.repo.ai.ChatOptionRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SeedData {
    @Bean CommandLineRunner seed(ChatOptionRepo repo){
        return args -> {
            if (repo.count()>0) return;

            var orders = ChatOption.builder().optionId("opt_orders").label("Orders")
                    .replyMessage("What do you need help with in Orders?")
                    .nextOptionIds(List.of("opt_track","opt_cancel")).root(true).build();

            var shipping = ChatOption.builder().optionId("opt_shipping").label("Shipping rates")
                    .replyMessage("Pick a shipping category:")
                    .nextOptionIds(List.of("opt_domestic","opt_international")).root(true).build();

            var human = ChatOption.builder().optionId("opt_agent").label("Talk to human")
                    .replyMessage("Okay! A human agent will reach out shortly.").root(true).build();

            var track = ChatOption.builder().optionId("opt_track").label("Track order")
                    .replyMessage("Share your order number next (feature stub).").build();

            var cancel = ChatOption.builder().optionId("opt_cancel").label("Cancel request")
                    .replyMessage("Cancellation recorded. An agent will confirm.")
                    .nextOptionIds(List.of("opt_agent")).build();

            var domestic = ChatOption.builder().optionId("opt_domestic").label("Domestic")
                    .replyMessage("Domestic shipping starts at ₹49. Anything else?")
                    .nextOptionIds(List.of("opt_agent")).build();

            var international = ChatOption.builder().optionId("opt_international").label("International")
                    .replyMessage("International depends on zone & weight. Want a rate card?")
                    .nextOptionIds(List.of("opt_agent")).build();

            repo.saveAll(List.of(orders, shipping, human, track, cancel, domestic, international));
        };
    }
}
