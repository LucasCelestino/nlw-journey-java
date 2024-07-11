package com.celestino.planner.activity;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.celestino.planner.trip.Trip;

@Service
public class ActivityService
{
    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip)
    {
        Activity newActivity = new Activity(payload.title(), payload.occursAt(), trip);

        this.activityRepository.save(newActivity);

        return new ActivityResponse(newActivity.getId());
    }
}
