package com.gkmonk.pos.services.ai;

// service/ChatService.java

import com.gkmonk.pos.model.ai.ChatDtos;
import com.gkmonk.pos.model.ai.ChatOption;
import com.gkmonk.pos.model.ai.ChatSession;
import com.gkmonk.pos.model.ai.ChatTurn;
import com.gkmonk.pos.repo.ai.ChatOptionRepo;
import com.gkmonk.pos.repo.ai.ChatSessionRepo;
import com.gkmonk.pos.repo.ai.ChatTurnRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ChatService {
    private final ChatOptionRepo optionRepo;
    private final ChatSessionRepo sessionRepo;
    private final ChatTurnRepo turnRepo;

    public ChatDtos.OptionsResponse getRootOptions(String sessionId) {
        ensureSession(sessionId);
        return new ChatDtos.OptionsResponse(toDtos(optionRepo.findByRootTrue()));
    }

    @Transactional
    public ChatDtos.ChatResponse handleChoice(ChatDtos.ChatRequest req) {
        ChatSession session = ensureSession(req.getSessionId());
        if (req.getContext()!=null && !req.getContext().isEmpty()) {
            session.getContext().putAll(req.getContext());
        }

        var opt = optionRepo.findByOptionId(req.getChoiceId())
                .orElseThrow(() -> new NoSuchElementException("Unknown choice: " + req.getChoiceId()));

        var message = Optional.ofNullable(opt.getReplyMessage()).orElse("Okay.");
        List<ChatOption> next = (opt.getNextOptionIds()==null || opt.getNextOptionIds().isEmpty())
                ? List.of() : optionRepo.findByOptionIdIn(opt.getNextOptionIds());

        session.setLastChoiceId(opt.getOptionId());
        session.setUpdatedAt(Instant.now());
        sessionRepo.save(session);

        turnRepo.save(ChatTurn.builder()
                .sessionId(session.getSessionId())
                .at(Instant.now())
                .userChoiceId(opt.getOptionId())
                .botMessage(message)
                .contextSnapshot(new HashMap<>(session.getContext()))
                .build());

        return new ChatDtos.ChatResponse(message, toDtos(next));
    }

    private ChatSession ensureSession(String sessionId){
        return sessionRepo.findBySessionId(sessionId).orElseGet(() ->
                sessionRepo.save(ChatSession.builder()
                        .sessionId(sessionId)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build())
        );
    }

    private List<ChatDtos.OptionDto> toDtos(List<ChatOption> opts){
        return opts.stream()
                .sorted(Comparator.comparing(ChatOption::getLabel, String.CASE_INSENSITIVE_ORDER))
                .map(o -> new ChatDtos.OptionDto(o.getOptionId(), o.getLabel()))
                .collect(Collectors.toList());
    }
}
