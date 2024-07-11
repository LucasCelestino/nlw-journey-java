package com.celestino.planner.trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.celestino.planner.activity.ActivityData;
import com.celestino.planner.activity.ActivityRequestPayload;
import com.celestino.planner.activity.ActivityResponse;
import com.celestino.planner.activity.ActivityService;
import com.celestino.planner.participant.Participant;
import com.celestino.planner.participant.ParticipantCreateResponse;
import com.celestino.planner.participant.ParticipantData;
import com.celestino.planner.participant.ParticipantRequestPayload;
import com.celestino.planner.participant.ParticipantService;

@RestController
@RequestMapping("/trips")
public class TripController
{

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TripRepository tripRepository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload)
    {
        Trip newTrip = new Trip(payload);

        tripRepository.save(newTrip);

        participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id)
    {
        Optional<Trip> trip = this.tripRepository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload)
    {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(!trip.isPresent())
        {
            return ResponseEntity.notFound().build();
        }

        Trip rawTrip = trip.get();

        rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
        rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
        rawTrip.setDestination(payload.destination());

        this.tripRepository.save(rawTrip);

        return ResponseEntity.ok(rawTrip);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id)
    {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(!trip.isPresent())
        {
            return ResponseEntity.notFound().build();
        }

        Trip rawTrip = trip.get();

        rawTrip.setIsConfirmed(true);

        this.tripRepository.save(rawTrip);

        participantService.triggerConfirmationEmailToParticipants(id);

        return ResponseEntity.ok(rawTrip);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload)
    {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(!trip.isPresent())
        {
            return ResponseEntity.notFound().build();
        }

        Trip rawTrip = trip.get();

        ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(), rawTrip);

        if(rawTrip.getIsConfirmed()) participantService.triggerConfirmationEmailToParticipants(rawTrip.getId());

        return ResponseEntity.ok(participantResponse);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id)
    {
        List<ParticipantData> participantsList = this.participantService.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participantsList);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, ActivityRequestPayload payload)
    {
        Optional<Trip> trip = this.tripRepository.findById(id);
        
        if(!trip.isPresent())
        {
            return ResponseEntity.notFound().build();
        }

        Trip rawTrip = trip.get();

        ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);
        
        return ResponseEntity.ok(activityResponse);
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id)
    {
        List<ActivityData> activityDataList = this.activityService.getAllActivitiesFromTripId(id);

        return ResponseEntity.ok(activityDataList);
    }
}
