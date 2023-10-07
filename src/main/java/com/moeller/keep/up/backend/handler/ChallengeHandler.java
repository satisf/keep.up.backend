package com.moeller.keep.up.backend.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moeller.keep.up.backend.model.*;
import com.moeller.keep.up.backend.repository.ChallengeRepository;
import com.moeller.keep.up.backend.repository.ParticipantRepository;
import com.moeller.keep.up.backend.repository.RepetitionRepository;
import com.moeller.keep.up.backend.service.ChallengeService;
import com.moeller.keep.up.backend.service.RepetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChallengeHandler {


    private ChallengeService challengeService;
    private ChallengeRepository challengeRepository;
    private RepetitionService repetitionService;
    private RepetitionRepository repetitionRepository;
    private ParticipantRepository participantRepository;

    @Autowired
    public void setChallengeService(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }
    @Autowired
    public void setChallengeRepository(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }
    @Autowired
    public void setRepetitionService(RepetitionService repetitionService) {
        this.repetitionService = repetitionService;
    }
    @Autowired
    public void setRepetitionRepository(RepetitionRepository repetitionRepository) {
        this.repetitionRepository = repetitionRepository;
    }
    @Autowired
    public void setParticipantRepository(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, User user) throws JsonProcessingException {
        String[] path = apiGatewayProxyRequestEvent.getPath().substring(1).split("/");

        ObjectMapper objectMapper = new ObjectMapper();

        // GET challenges
        if (apiGatewayProxyRequestEvent.getHttpMethod().equals(HttpMethod.GET.name())) {
            if (path.length == 1) {
                System.out.println("getting challenges");
                return getChallenges(user);
            }
            if (path.length == 2) {
                return getChallenge(user, Long.parseLong(path[1]));
            }
        }
        // PUT Challenge
        else if (apiGatewayProxyRequestEvent.getHttpMethod().equals(HttpMethod.PUT.name())) {
            if (path.length == 1 && !apiGatewayProxyRequestEvent.getBody().isEmpty()) {
                CreateChallengeDTO createChallengeDTO = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), CreateChallengeDTO.class);
                return createChallenge(createChallengeDTO, user);
            }
        }
        // POST Challenge
        else if (apiGatewayProxyRequestEvent.getHttpMethod().equals(HttpMethod.POST.name())) {
            if (path.length > 2) {
                switch (path[2]) {
                    case "add":
                        return addParticipantToChallenge(Long.parseLong(path[1]), path[3], user);
                    case "repeat":
                        return completeChallenge(Long.parseLong(path[1]), user);
                    default:
                }
            }
        }
        return new APIGatewayProxyResponseEvent().withBody("default");

    }

    private ChallengeDTO getChallengeDTO(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId).get();
        System.out.println("getting challenge " + challengeId);
        List<Participant> participants = participantRepository.findByChallenge(challenge);
        System.out.println("participants: " + participants.size());
        List<ParticipantDTO> participantDTOS = participants.stream()
                .map(participant -> ParticipantDTO.fromParticipant(participant, challenge, repetitionRepository.findByParticipant(participant)))
                .collect(Collectors.toList());
        System.out.println("participants dto");
        return ChallengeDTO.fromChallenge(challengeRepository.findById(challengeId).get(), user, participantDTOS);
    }

    private APIGatewayProxyResponseEvent getChallenge(User user, Long challengeId) throws JsonProcessingException {

        return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.OK.value()).withBody(
                new ObjectMapper().writeValueAsString(getChallengeDTO(challengeId, user)));
    }

    private APIGatewayProxyResponseEvent getChallenges(User user) throws JsonProcessingException {
        List<Challenge> challenges = challengeService.findAllChallengesForUser(user);
        System.out.println("challenges: " + challenges.size());
        return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.OK.value()).withBody(
                new ObjectMapper().writeValueAsString(challenges.stream()
                        .map(challenge -> getChallengeDTO(challenge.id, user))
                        .collect(Collectors.toList())));
    }

    private APIGatewayProxyResponseEvent createChallenge(CreateChallengeDTO createChallengeDTO, User user) throws JsonProcessingException {
        Challenge challenge = challengeService.createChallenge(
                createChallengeDTO.getName(),
                createChallengeDTO.getTimes(),
                Period.parse(createChallengeDTO.getDuration()),
                createChallengeDTO.getStartDate(),
                user);
        return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.OK.value()).withBody(
                new ObjectMapper().writeValueAsString(getChallengeDTO(challenge.id, user)));
    }


    private APIGatewayProxyResponseEvent addParticipantToChallenge(long challengeId, String participantName, User user) throws JsonProcessingException {
        challengeService.addUserToChallenge(challengeId, user, participantName);
        return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.OK.value());
    }

    private APIGatewayProxyResponseEvent completeChallenge(long challengeId, User user) throws JsonProcessingException {
        repetitionService.addRepetition(user, challengeId);
        return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.OK.value());
    }
}
